/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.main;

import es.uam.eps.ir.twittercollector.collector.StatusCollector;
import es.uam.eps.ir.twittercollector.database.TwitterDB;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import twitter4j.Status;
import twitter4j.TwitterException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class IndividualTweetCollector 
{
    public static void main(String args[])
    {
        if(args.length < 3)
        {
            System.out.println("Invalid arguments");
            System.out.println("1. Database");
            System.out.println("2. Input directory/file");
            System.out.println("3. Output directory");
            return;
        }
        TwitterDB.startDB(args[0]);
        StatusCollector sc = new StatusCollector();

        //Obtaining the 
        List<String> filesToRecover = new ArrayList<>();
        List<String> outputNames = new ArrayList<>();
        File dir = new File(args[1]);
        if(!dir.isDirectory())
        {
            filesToRecover.add(dir.getAbsolutePath());
            outputNames.add(dir.getName());
        }
        else
        {
            for(File s : dir.listFiles())
            {
                outputNames.add(s.getName());
                filesToRecover.add(s.getAbsolutePath());
            }
        }
        
        for(String file : filesToRecover)
        {
            System.out.println("RECOVERING: " + file);
            List<Tweet> tweets = new ArrayList<>();
            Long tweetId;
            try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
            {
                String text;
                while((text = br.readLine()) != null)
                {
                    tweetId = new Long(text);
                    try
                    {
                        Status status = sc.getStatus(tweetId);
                        tweets.add(new Tweet(status));
                    }
                    catch(TwitterException ex)
                    {
                        System.out.println("ERROR: Tweet " + tweetId + " couldn't be retrieved");
                    }
                }
            } 
            catch (FileNotFoundException ex) {
                System.out.println("ERROR: File " + file + " was not found");
            }
            catch (IOException ioe)
            {
                System.out.println("ERROR: An error ocurred while reading file " + file);
            }
            
            String outputFile = outputNames.get(filesToRecover.indexOf(file));
            CSVFormat csvFileFormat = CSVFormat.TDF.withHeader("tweetId","authorId","text","retweetCount","favoriteCount","timestamp");
            FileWriter fileWriter;
            CSVPrinter csvFilePrinter;
            try
            {
                fileWriter = new FileWriter(args[2]+outputFile);
                csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
                
                List dataRecord = new ArrayList();
                dataRecord.add("TweetId");
                dataRecord.add("AuthorId");
                dataRecord.add("Text");
                dataRecord.add("RetweetCount");
                dataRecord.add("FavoriteCount");
                dataRecord.add("Timestamp");
                csvFilePrinter.printRecord(dataRecord);
                
                for(Tweet tw : tweets)
                {
                    dataRecord = new ArrayList();
                    dataRecord.add(tw.getTweetId());
                    dataRecord.add(tw.getUserId());
                    dataRecord.add(tw.getText());
                    dataRecord.add(tw.getRetweetCount());
                    dataRecord.add(tw.getFavoriteCount());
                    dataRecord.add(tw.getCreated().getTime());
                    csvFilePrinter.printRecord(dataRecord);
                }
                System.out.println("Recovered " + tweets.size());
            } 
            catch (FileNotFoundException ex) {
                System.out.println("ERROR: File " + file + " was not found");
                Logger.getLogger(IndividualTweetCollector.class.getName()).log(Level.SEVERE,null,ex);
            }
            catch (IOException ioe)
            {
                System.out.println("ERROR: An error ocurred while writing in file " + file);
                Logger.getLogger(IndividualTweetCollector.class.getName()).log(Level.SEVERE,null,ioe);
            }
            
            
        }
    }
}
