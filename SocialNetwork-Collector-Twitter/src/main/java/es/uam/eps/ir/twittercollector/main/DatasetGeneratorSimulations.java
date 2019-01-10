/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.main;

import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOFactory;
import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOHandler;
import es.uam.eps.ir.twittercollector.database.dao.data.HashtagDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.TweetDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UrlDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UserDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UserInteractionDAO;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Class to generate datasets
 * @author Javier Sanz-Cruzado
 */
public class DatasetGeneratorSimulations 
{
    private final String route;
    private final String databaseTrain;
    private final String databaseTest;
    private final String datasetName;
    
    private static final String USERS_FILE = "users.csv";
    private static final String TWEETS_FILE = "tweets.csv";
    private static final String HASHTAGS_FILE = "hashtags.csv";
    private static final String URLS_FILE = "urls.csv";
    private static final String TWEET_HASHTAGS_FILE = "tweet-hashtags.csv";
    private static final String TWEET_URL_FILE = "tweet-url.csv";
    private static final String INTERACTIONS_FILE = "interactions.csv";
    private static final String FOLLOWS_FILE = "follows.csv";
    private static final String FOLLOWS_TRAIN_FILE = "follows-train.csv";
    private static final String FOLLOWS_TEST_FILE = "follows-test.csv";
    private static final String RETWEET_FILE = "retweets.csv";
    private static final String MENTIONS_FILE = "mentions.csv";
    private static final String README_FILE = "readme.txt";
    
    /**
     * Constructor
     * @param datasetName Name of the dataset
     * @param route route of the dataset
     * @param databaseTrain database containing the training follow links
     */
    public DatasetGeneratorSimulations(String datasetName, String route, String databaseTrain)
    {
        this(datasetName, route, databaseTrain, null);
    }
    
    /**
     * Constructor
     * @param datasetName Dataset name of the dataset
     * @param route Dataset route of the dataset
     * @param databaseTrain database containing the training follow links
     * @param databaseTest database containing the test follow links
     */
    public DatasetGeneratorSimulations(String datasetName, String route, String databaseTrain, String databaseTest)
    {
        this.datasetName = datasetName;
        this.route = route;
        this.databaseTrain = databaseTrain;
        
        TwitterDAOHandler tdaoh = TwitterDAOHandler.getInstance();
        tdaoh.addFactory(databaseTrain);
        
        
        if(databaseTest != null)
        {
            this.databaseTest = databaseTest;
            tdaoh.addFactory(databaseTest);
        }
        else
        {
            this.databaseTest = null;
        }
    }
    
    /**
     * Generates the user files
     * @throws IOException if something fails at writing the files
     * @throws SQLException if something fails while recovering database data
     */
    public void generateUsersFile() throws IOException, SQLException
    {
        TwitterDAOFactory twdao = TwitterDAOHandler.getInstance().getFactory(databaseTrain);
        UserDAO userDAO = twdao.getUserDAO();
        ResultSet resultSet = userDAO.listDataset();
        
        Appendable appendable = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.USERS_FILE)));
        try(CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).withDelimiter('\t').print(appendable))
        {
            printer.printRecords(resultSet);
        }     
    }
    
    /**
     * Generates a file containing the information about tweets.
     * @throws IOException
     * @throws SQLException 
     */
    public void generateTweetsFile() throws IOException, SQLException
    {
        TwitterDAOFactory twdao = TwitterDAOHandler.getInstance().getFactory(databaseTrain);
        TweetDAO tweetDAO = twdao.getTweetDAO();
        ResultSet resultSet = tweetDAO.listDatasetSimulation();
        
        Appendable appendable = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.TWEETS_FILE)));
        try(CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).withDelimiter('\t').withDelimiter('\t').print(appendable))
        {
            printer.printRecords(resultSet);
        }
    }
    
    /**
     * Generates a file containing information about the hashtags
     * @throws IOException
     * @throws SQLException 
     */
    public void generateHashtagsFile() throws IOException, SQLException
    {
        TwitterDAOFactory twdao = TwitterDAOHandler.getInstance().getFactory(databaseTrain);
        HashtagDAO hashtagDAO = twdao.getHashtagDAO();
        ResultSet resultSet = hashtagDAO.listDataset();
        
        Appendable appendable = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.HASHTAGS_FILE)));
        try(CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).withDelimiter('\t').withDelimiter('\t').print(appendable))
        {
            printer.printRecords(resultSet);
        }
    }
    
    /**
     * Generates a file containing information about the Urls
     * @throws IOException
     * @throws SQLException 
     */
    public void generateUrlsFile() throws IOException, SQLException
    {
        TwitterDAOFactory twdao = TwitterDAOHandler.getInstance().getFactory(databaseTrain);
        UrlDAO urlDAO = twdao.getUrlDAO();
        ResultSet resultSet = urlDAO.listDataset();
        
        Appendable appendable = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.URLS_FILE)));
        try(CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).withDelimiter('\t').print(appendable))
        {
            printer.printRecords(resultSet);
        }
    }
    
    /**
     * Generates a file that contains the relations between hashtags and tweets
     * @throws IOException
     * @throws SQLException 
     */
    public void generateTweetHashtagsFile() throws IOException, SQLException
    {
        TwitterDAOFactory twdao = TwitterDAOHandler.getInstance().getFactory(databaseTrain);
        TweetDAO tweetDAO = twdao.getTweetDAO();
        ResultSet resultSet = tweetDAO.listDatasetHashtagsSimulation();
        
        Appendable appendable = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.TWEET_HASHTAGS_FILE)));
        try(CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).withDelimiter('\t').print(appendable))
        {
            printer.printRecords(resultSet);
        }
    }
    
    /**
     * Generates a file that contains the relations between tweets and URLs
     * @throws IOException
     * @throws SQLException 
     */
    public void generateTweetUrlsFile() throws IOException, SQLException
    {
        TwitterDAOFactory twdao = TwitterDAOHandler.getInstance().getFactory(databaseTrain);
        TweetDAO tweetDAO = twdao.getTweetDAO();
        ResultSet resultSet = tweetDAO.listDatasetUrls();
        
        Appendable appendable = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.TWEET_URL_FILE)));
        try(CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).withDelimiter('\t').print(appendable))
        {
            printer.printRecords(resultSet);
        }
    }
    
    /**
     * Generates the different files containing information about follows relations
     * @throws SQLException
     * @throws IOException 
     */
    public void generateFollowsFiles() throws SQLException, IOException
    {
        
        TwitterDAOFactory twdao = TwitterDAOHandler.getInstance().getFactory(databaseTrain);
        UserDAO userDAO = twdao.getUserDAO();
        ResultSet resultSet = userDAO.listDatasetFollows();
        if(this.databaseTest == null)
        {
            Appendable appendable = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.FOLLOWS_FILE)));
            try(CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).withDelimiter('\t').withDelimiter('\t').print(appendable))
            {
                printer.printRecords(resultSet);
            }
        }
        else
        {
            Appendable appendable = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.FOLLOWS_TRAIN_FILE)));
            resultSet = userDAO.listDatasetFollows();
            try(CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).withDelimiter('\t').withDelimiter('\t').print(appendable))
            {
                printer.printRecords(resultSet);
            }
                        
            resultSet = userDAO.listDatasetFollows();
            Map<Long, Set<Long>> follows = new HashMap<>();
            while(resultSet.next())
            {
                Long follower = resultSet.getLong("follower");
                Long friend = resultSet.getLong("friend");

                if(!follows.containsKey(follower))
                    follows.put(follower, new HashSet<>());
                
                follows.get(follower).add(friend);
            }

            twdao = TwitterDAOHandler.getInstance().getFactory(databaseTest);
            userDAO = twdao.getUserDAO();
            resultSet = userDAO.listDatasetFollows();
            
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.FOLLOWS_TEST_FILE)));
            bw.write("follower,friend\r\n");
            while(resultSet.next())
            {
                Long follower = resultSet.getLong("follower");
                Long friend = resultSet.getLong("friend");
                
                boolean insert = true;
                if(follows.containsKey(follower))
                {
                    if(follows.containsKey(friend))
                    {
                        insert = false;
                    }
                }
                
                if(insert)
                {
                    bw.write(follower + "," + friend + "\n");
                }
                
            }
            
            
            appendable = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.FOLLOWS_FILE)));
            resultSet = userDAO.listDatasetFollows();
            try(CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).withDelimiter('\t').print(appendable))
            {
                printer.printRecords(resultSet);
            }
            bw.close();
        }
    }
    
    public void generateInteractionsFile() throws IOException, SQLException
    {
        TwitterDAOFactory twdao = TwitterDAOHandler.getInstance().getFactory(databaseTrain);
        UserInteractionDAO interactionDAO = twdao.getUserInteractionDAO();
        ResultSet resultSet = interactionDAO.listDataset();
        
        Appendable appendable = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.INTERACTIONS_FILE)));
        try(CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).withDelimiter('\t').print(appendable))
        {
            printer.printRecords(resultSet);
        }
    }
    
    public void generateRetweetsFile() throws IOException, SQLException
    {
        TwitterDAOFactory twdao = TwitterDAOHandler.getInstance().getFactory(databaseTrain);
        TweetDAO tweetDAO = twdao.getTweetDAO();
        ResultSet resultSet = tweetDAO.listDatasetRetweets();
        
        Appendable appendable = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.RETWEET_FILE)));
        try(CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).withDelimiter('\t').print(appendable))
        {
            printer.printRecords(resultSet);
        }
    }
    
    
    public void generateReadme() throws IOException
    {
        try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.README_FILE))))
        {
            bw.write("DATASET: " + this.datasetName + "\r\n");
            bw.write("AUTHOR: Javier Sanz-Cruzado\r\n");
            bw.write("GROUP: Information Retrieval Group\r\n");
            bw.write("UNIVERSITY: Universidad Autónoma de Madrid\r\n");
            bw.write("DESCRIPTION: \r\n"); //TODO: Read from description file
            bw.write("CONTENTS:\r\n");
            
            bw.write("\t" + DatasetGeneratorSimulations.USERS_FILE+": Information about users.\r\n");
            bw.write("\t\tFORMAT: userId,name,screenName,description,location,created,verified,numFollowers,numFriends,numListed,numTweets\r\n");
            bw.write("\t\t\tuserId : Identifier of the user.\r\n");
            bw.write("\t\t\tname: Complete name of the user.\r\n");
            bw.write("\t\t\tscreenName: Nickname of the user.\r\n");
            bw.write("\t\t\tdescription: Profile description for the user.\r\n");
            bw.write("\t\t\tlocation: Location given by the user.\r\n");
            bw.write("\t\t\tcreated: Date of creation of the account. Format: yyyy-MM-dd hh:mm:ss\r\n");
            bw.write("\t\t\tverified: If the user's identity has been verified.\r\n");
            bw.write("\t\t\tnumFollowers: Number of followers of the user.\r\n");
            bw.write("\t\t\tnumFriends: Number of users that the given user is following.\r\n");
            bw.write("\t\t\tnumListed: Number of lists in wich the user appears.\r\n");
            bw.write("\t\t\tnumTweets: Number of tweets published.\r\n");
            
            bw.write("\r\n\t" + DatasetGeneratorSimulations.TWEETS_FILE+": Information about tweets\r\n");
            bw.write("\t\tFORMAT: tweetId,userId,text,retweetCount,favoriteCount,created,truncated\r\n");
            bw.write("\t\t\ttweetId: Identifier of the tweet.\r\n");
            bw.write("\t\t\tuserId: Identifier of the user who published the tweet.\r\n");
            bw.write("\t\t\ttext: Text of the tweet.\r\n");
            bw.write("\t\t\tretweetCount: Number of retweets.\r\n");
            bw.write("\t\t\tfavoriteCount: Number of favourites.\r\n");
            bw.write("\t\t\tcreated: Date of creation of the tweet. Format: yyyy-MM-dd hh:mm:ss\r\n");
            bw.write("\t\t\ttruncated: Indicates if the original case has been truncated (if the tweet is a retweet).\r\n");
            
            bw.write("\r\n\t" + DatasetGeneratorSimulations.HASHTAGS_FILE+": List of hashtags found in the recovered tweets.\r\n");
            bw.write("\t\tFORMAT: hashtagId,text\r\n");
            bw.write("\t\t\thashtagId: Identifier given to the hashtag.\r\n");
            bw.write("\t\t\ttext: The text of the hashtag.\r\n");
            
            bw.write("\r\n\t" + DatasetGeneratorSimulations.TWEET_HASHTAGS_FILE+": Relation between tweets and hashtags.\r\n");
            bw.write("\t\tFORMAT: tweetId,hashtagId\r\n");
            bw.write("\t\t\ttweetId: Identifier of the tweet.\r\n");
            bw.write("\t\t\thashtagId: Identifier of the hashtag.\r\n");
            
            bw.write("\r\n\t" + DatasetGeneratorSimulations.URLS_FILE+": List of URLs found in the recovered tweets.\r\n");
            bw.write("\t\tFORMAT: urlId,url,expandedUrl,displayUrl\r\n");
            bw.write("\t\t\turlId: Identifier of the URL.\r\n");
            bw.write("\t\t\turl: Shortened URL.\r\n");
            bw.write("\t\t\texpandedUrl: Complete URL.\r\n");
            bw.write("\t\t\tdisplayUrl: URL displayed in the tweet.\r\n");
            
            bw.write("\r\n\t" + DatasetGeneratorSimulations.TWEET_URL_FILE+": Relation between tweets and URLs.\r\n");
            bw.write("\t\tFORMAT: tweetId,urlId\r\n");
            bw.write("\t\t\ttweetId: Identifier of the tweet.\r\n");
            bw.write("\t\t\turlId: Identifier of the URL.\r\n");
            
            bw.write("\r\n\t" + DatasetGeneratorSimulations.INTERACTIONS_FILE+": Interactions between users.\r\n");
            bw.write("\t\tFORMAT: userId,interactedUserId,interaction,timestamp\r\n");
            bw.write("\t\t\tuserId: Identifier of the user that interacts.\r\n");
            bw.write("\t\t\tinteractedUserId: Identifier of the user that has been mentioned, retweeted or replied.\r\n");
            bw.write("\t\t\tinteraction: Interaction type (mention, retweet or replied).\r\n");
            bw.write("\t\t\ttimestamp: Timestamp of the interaction (UNIX Timestamp, ms.)\r\n");
            
            bw.write("\r\n\t" + DatasetGeneratorSimulations.RETWEET_FILE + ": List of retweets.\r\n");
            bw.write("\t\tFORMAT: originalTweet, retweet\r\n");
            bw.write("\t\t\toriginalTweet: Identifier of the tweet that has been retweeted.\r\n");
            bw.write("\t\t\tretweet: Identifier of the retweet.\r\n");
            
            bw.write("\r\n\t" + DatasetGeneratorSimulations.MENTIONS_FILE + ": Interactions between users.\r\n");
            bw.write("\t\tFORMAT: tweetId,userId\r\n");
            bw.write("\t\t\ttweetId: Identifier of the tweet that contains the mention.\r\n");
            bw.write("\t\t\tuserId: Identifier of the mentioned user.\r\n");
            
            bw.write("\r\n\t" + DatasetGeneratorSimulations.FOLLOWS_FILE+": Follow relations between users.\r\n");
            bw.write("\t\tFORMAT: follower,friend\r\n");
            bw.write("\t\t\tfollower: User that follows the other one.\r\n");
            bw.write("\t\t\tfriend: Followed user.\r\n");
                
            if(this.databaseTest != null)
            {
                bw.write("\r\n\t" + DatasetGeneratorSimulations.FOLLOWS_TRAIN_FILE+": Follow relations between users. Train set.\r\n");
                bw.write("\t\tFORMAT: follower,friend\r\n");
                bw.write("\t\t\tfollower: User that follows the other one.\r\n");
                bw.write("\t\t\tfriend: Followed user.\r\n");

                bw.write("\r\n\t" + DatasetGeneratorSimulations.FOLLOWS_TEST_FILE+": Follow relations between users. Test set.\r\n");
                bw.write("\t\tFORMAT: follower,friend\r\n");
                bw.write("\t\t\tfollower: User that follows the other one.\r\n");
                bw.write("\t\t\tfriend: Followed user.\r\n");
            }
        }
    }
    
    public void generateMentionsFile() throws IOException, SQLException
    {
        TwitterDAOFactory twdao = TwitterDAOHandler.getInstance().getFactory(databaseTrain);
        TweetDAO tweetDAO = twdao.getTweetDAO();
        ResultSet resultSet = tweetDAO.listDatasetMentions();
        
        Appendable appendable = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.route + DatasetGeneratorSimulations.MENTIONS_FILE)));
        try(CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).withDelimiter('\t').print(appendable))
        {
            printer.printRecords(resultSet);
        }
    }
    public void zip()
    {
        String[] files = new String[]{USERS_FILE, TWEETS_FILE, HASHTAGS_FILE, URLS_FILE,
          TWEET_HASHTAGS_FILE,TWEET_URL_FILE, INTERACTIONS_FILE, FOLLOWS_FILE, FOLLOWS_TRAIN_FILE,
          FOLLOWS_TEST_FILE,RETWEET_FILE ,MENTIONS_FILE,README_FILE};
        byte[] buffer = new byte[1024];
          
        try{
            //Abrir comprimido
            FileOutputStream fos = new FileOutputStream(this.route + this.datasetName + ".zip");
            ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos));
             
            //Para todos los archivos a comprimir
            for(String file : files)
            {
                //Abrir
                ZipEntry ze= new ZipEntry(file);
                zos.putNextEntry(ze);
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(this.route + file));
      
                //Comprimir
                int len;
                while ((len = in.read(buffer)) > 0) 
                {
                    zos.write(buffer, 0, len);
                }
      
                //Cerrar
                in.close();
                zos.closeEntry();
            }
             
            //Clausura de comprimido
            zos.close();
             
            //Eliminar archivos no comprimidos
            for(String s : files)
            {
                File file = new File(this.route + s);
                file.delete();
            }
            System.out.println("Done");
             
        }catch(IOException ex){
           ex.printStackTrace();
        }
        
    }
    public void generateDataset() throws IOException,SQLException
    {
        generateUsersFile();
        generateTweetsFile();
        generateHashtagsFile();
        generateUrlsFile();
        generateInteractionsFile();
        generateFollowsFiles();
        generateTweetHashtagsFile();
        generateTweetUrlsFile();
        generateRetweetsFile();
        generateMentionsFile();
        generateReadme();
        zip();
    }
    
    public static void main(String[] args) throws Exception
    {
        if(args.length < 3)
        {
            System.out.println("Usage: <datasetName> <outputRoute> <databaseTrain> (<databaseTest>)");
        }
        
        DatasetGeneratorSimulations dsg = (args.length > 3) ? new DatasetGeneratorSimulations(args[0],args[1], args[2], args[3]) : new DatasetGeneratorSimulations(args[0], args[1], args[2]);
        dsg.generateDataset();
        
    }
}
