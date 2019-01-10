/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.indexer;

import es.uam.eps.ir.twittercollector.database.TwitterDB;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import es.uam.eps.ir.utils.exceptions.NotInitializedException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author Javier
 */
public class TwitterIndexer 
{
    private String path;
    private IndexWriter writer;
    private IndexReader reader;
    private List<String> stopwords;
    
    public TwitterIndexer(String path)
    {
        this.path = path;
        this.stopwords = null;
    }
    
    public void setStopwords(String stopwordsFile)
    {
        // Read stopwords list
        
        // Fill list
    }
    
    public void initializeWriting(boolean create)
    {
        try
        {
            Analyzer analyzer;
            if(stopwords == null)
            {
                analyzer = new StandardAnalyzer();
            }
            else
            {
                CharArraySet stopwordSet = new CharArraySet(this.stopwords,true);
                analyzer = new StandardAnalyzer(stopwordSet);
            }
            
            File f = new File(path);
            Path pathLocation = Paths.get(path);
            Directory dir = SimpleFSDirectory.open(pathLocation);
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            
            if (create)
            {
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            }
            else
            {
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            }
            
            writer = new IndexWriter(dir, iwc);
            
        } catch (IOException ex) {
            Logger.getLogger(TwitterIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void build(String database, TextParser parser) throws NotInitializedException
    {
        try {
            /** Database to use **/
            TwitterDB.startDB(database);
            TwitterDB tdb = TwitterDB.getInstance();
            
            if(this.writer == null)
            {
                throw new NotInitializedException("Not initialized");
            }
            
            List<Tweet> tweets = tdb.getTweetDAO().list();
            
            System.out.println("Tweets read");
            
            FieldType tweetType = new FieldType();
            
            tweetType.setStored(true);
            tweetType.setTokenized(true);
            tweetType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
            tweetType.setStoreTermVectors(true);
            
            System.out.println("Starting tweet indexing");
            /** Store each tweet in memory **/
            tweets.parallelStream().forEach((tw) -> {
            
                Document luceneDoc = new Document();
                luceneDoc.add(new LongPoint("tweetId", tw.getTweetId()));
                luceneDoc.add(new StoredField("tweetId", tw.getTweetId()));
                luceneDoc.add(new LongPoint("userId", tw.getUserId()));
                luceneDoc.add(new StoredField("userId", tw.getUserId()));
                luceneDoc.add(new LongPoint("date", tw.getCreated().getTime()));
                luceneDoc.add(new StoredField("tweetId", tw.getCreated().getTime()));
                luceneDoc.add(new Field("content", parser.parse(tw.getText()), tweetType));
                try {
                    this.writer.addDocument(luceneDoc);
                } catch (IOException ex) {
                    Logger.getLogger(TwitterIndexer.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            System.out.println("Finished tweet indexing");
        } catch (TwitterCollectorException ex) {
            Logger.getLogger(TwitterIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    public void initializeReader()
    {
        try {
            File f = new File(path);
            Path pathLocation = Paths.get(path);
            Directory dir = SimpleFSDirectory.open(pathLocation);
            this.reader = DirectoryReader.open(dir);
        } catch (IOException ex) {
            Logger.getLogger(TwitterIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Returns a set of TF-IDF vectors (the vectors of the user index) 
     * @param userId Identifier of the user
     * @return A map indexed by tweetId, containing a map indexed by term.
     * @throws NotInitializedException if the reader has not been initialized
     */
    public Map<String, Map<String, Double>> readUser(Long userId) throws NotInitializedException
    {
        Map<String, Map<String, Double>> userTweets = new HashMap<>();
        try 
        {
            if(this.reader == null)
            {
                throw new NotInitializedException("Not initialized");
            }
            
            IndexSearcher isearcher = new IndexSearcher(this.reader);
            Query q = LongPoint.newExactQuery("userId", userId);
            ScoreDoc docs[] = isearcher.search(q, 10000000).scoreDocs;
            
            
            for(ScoreDoc doc : docs)
            {
                Map<String, Double> map = new HashMap<>();
                Terms vector = this.reader.getTermVector(doc.doc, "content");
                TermsEnum termsEnum = vector.iterator();
                BytesRef text = null;
                while((text = termsEnum.next()) != null)
                {
                    String term = text.utf8ToString();
                    int freq = (int) termsEnum.totalTermFreq();
                    double tf = Math.log(1.0 + freq);
                    Term t = new Term("content", text);
                    double idf = Math.log((reader.numDocs()+0.0)/(reader.docFreq(t)+0.0));
                    map.put(term, tf*idf);
                }
                    
            }
            
            return userTweets;
        } catch (IOException ex) {
            Logger.getLogger(TwitterIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
