/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.manager;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import es.uam.eps.ir.twittercollector.exception.TwitterConfigurationErrorException;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.stream.Stream;


/**
 * Class to keep the configuration of the Twitter Collector program
 * @author Javier Sanz-Cruzado
 */
public class TwitterConfiguration 
{
    /**
     * Indicates the mode of processing the crawling.
     */
    private TwitterProcessing processing;
    /**
     * Database name in the configuration.
     */
    private String database;
    /**
    * Minimum number of users to store in the database.
    */
    private Integer numMinUsers;
    /**
     * Maximum number of tweet to retrieve from each user (from API Call; Aditionally, retweets will be retrieved).
     */
    private Integer numMaxTweets;
    /**
     * Number of neighbours to add to the queue each iteration.
     */
    private Double numNeighSBS;
    /**
     * Oldest date for each tweet (not retweet).
     */
    private Date startDate;
    /**
     * Newest date for each tweet (also retweets).
     */
    private Date endDate;
    /**
     * User seed.
     */
    private Set<Long> seedUsers;
    
    private static final String PROCESSING_FIELD = "processing";
    private static final String PROCESSING_TWEETS_VALUE = "tweets";
    private static final String PROCESSING_TEMPORAL_VALUE = "temporal";
    private static final String PROCESSING_MIXED_VALUE = "mixed";
    private static final String NUMMINUSERS_FIELD = "numMinUsers";
    private static final String NUMMAXTWEETS_FIELD = "numMaxTweets";
    private static final String STARTDATE_FIELD = "startDate";
    private static final String ENDDATE_FIELD = "endDate";
    private static final String DATABASE_FIELD = "database";
    private static final String NUMNEIGHSBS_FIELD = "numNeighSBS";
    private static final String SEEDUSER_FIELD = "seedUser";

    /**
    * Configures the twitter collector
    * @param conffile Route to the configuration file
     * @throws es.uam.eps.ir.twittercollector.exception.TwitterCollectorException
     * @throws java.text.ParseException
    */
    public void configure(String conffile) throws TwitterCollectorException, ParseException
    {
        BufferedReader br = null;

        HashMap<String, String> aux = new HashMap<>();
        try
        {
            // Reading the configuration file
            br = new BufferedReader(new InputStreamReader(new FileInputStream(conffile)));

            String text;
            while((text = br.readLine()) != null)
            {
                String[] str = text.split("\t");
                if(str.length != 2)
                {
                    throw(new TwitterConfigurationErrorException("Invalid configuration file"));
                }

                aux.put(str[0], str[1]);
            }

            br.close();

            // Get the parameters
            String chain = null;
            // processing
            chain = aux.get(PROCESSING_FIELD);
            if(chain != null)
            {
                if(chain.equals(PROCESSING_TWEETS_VALUE))
                {
                    processing = TwitterProcessing.TWEETS;
                }
                else if(chain.equals(PROCESSING_TEMPORAL_VALUE))
                {
                    processing = TwitterProcessing.TEMPORAL;
                }
                else if(chain.equals(PROCESSING_MIXED_VALUE))
                {
                    processing = TwitterProcessing.MIXED;
                }
                else
                {
                    throw(new TwitterConfigurationErrorException("Invalid configuration file. Invalid processing type"));
                }
            }
            else
            {
                throw(new TwitterConfigurationErrorException("Invalid configuration file. processing field missing"));
            }

            //database
            chain = aux.get(DATABASE_FIELD);
            if(chain != null)
            {
                database = chain;
            }
            else
            {
                throw(new TwitterConfigurationErrorException("Invalid configuration file. database field missing"));
            }

            //nunMinUsers
            chain = aux.get(NUMMINUSERS_FIELD);
            if(chain != null)
            {
                numMinUsers = Integer.parseInt(chain);
                if(numMinUsers <= 0)
                {
                    throw(new TwitterConfigurationErrorException("Invalid configuration file. numMinUsers field must be a positive integer"));
                }
            }
            else
            {
                throw(new TwitterConfigurationErrorException("Invalid configuration file. numMinUsers field missing"));
            }

            //numMaxTweet
            chain = aux.get(NUMMAXTWEETS_FIELD);
            if(chain == null && processing != TwitterProcessing.TEMPORAL)
            {
                throw(new TwitterConfigurationErrorException("Invalid configuration file. numMaxTweets field missing"));
            }
            else if(processing != TwitterProcessing.TEMPORAL)
            {
                numMaxTweets = Integer.parseInt(chain);
                if(numMaxTweets <= 0)
                {
                    throw(new TwitterConfigurationErrorException("Invalid configuration file. numMaxTweets must be a positive integer"));
                }
            }

            //numNeighSBS
            chain = aux.get(NUMNEIGHSBS_FIELD);
            if(chain == null)
            {
                throw(new TwitterConfigurationErrorException("Invalid configuration file. numNeighSBS field missing"));
            }
            else
            {
                numNeighSBS = new Double(chain);
                numNeighSBS /= 100.0;
                if(numNeighSBS < 0)
                {
                    throw(new TwitterConfigurationErrorException("Invalid configuration file. numNeighSBS must be a positive integer or 0"));
                }
            }

            
            //numNeighSBS
            chain = aux.get(SEEDUSER_FIELD);
            if(chain == null)
            {
                throw(new TwitterConfigurationErrorException("Invalid configuration file. seedUser field missing"));
            }
            else
            {
                String[] seeds = chain.split(",");
                
                seedUsers = new HashSet<>();
                for(String seed : seeds)
                {
                    Long user = new Long(seed);
                    seedUsers.add(user);
                }
                
                if(seedUsers.size() < 0)
                {
                    throw(new TwitterConfigurationErrorException("Invalid configuration file. seedUser must be a positive integer or 0"));
                }
            }
            //startDate
            chain = aux.get(STARTDATE_FIELD);
            if(chain == null && processing != TwitterProcessing.TWEETS)
            {
                throw(new TwitterConfigurationErrorException("Invalid configuration file. startDate field missing"));
            }
            else if(chain != null)
            {
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
                startDate = df.parse(chain);
                if(startDate.compareTo(new Date()) > 0)
                {
                    throw(new TwitterConfigurationErrorException("Invalid configuration file. startDate must be in the past"));
                }
                endDate = new Date();
            }
            
            chain = aux.get(ENDDATE_FIELD);
            if(chain == null)
            {
                endDate = new Date();
            }
            else
            {
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
                endDate = df.parse(chain);
                if(endDate.compareTo(startDate) < 0)
                {
                    throw(new TwitterConfigurationErrorException("Invalid configuration file. endDate must be newer than startDate"));
                }
            }
        }
        catch(IOException ioe)
        {
            throw(new TwitterConfigurationErrorException(ioe));
        }
    }

    public TwitterProcessing getProcessing() {
        return processing;
    }

    public String getDatabase() {
        return database;
    }

    public Integer getNumMinUsers() {
        return numMinUsers;
    }

    public Integer getNumMaxTweets() {
        return numMaxTweets;
    }

    public Double getNumNeighSBS() {
        return numNeighSBS;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Stream<Long> getSeedUsers() {
        return seedUsers.stream();
    }
    
    public boolean containsSeedUser(long user)
    {
        return this.seedUsers.contains(user);
    }
    
    
}