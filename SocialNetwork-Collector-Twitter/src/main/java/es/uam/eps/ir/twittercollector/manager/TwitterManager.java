/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.manager;

import es.uam.eps.ir.twittercollector.database.TwitterDB;
import es.uam.eps.ir.twittercollector.database.managing.TwitterKey;
import twitter4j.Twitter;
import java.util.*;
import java.util.logging.Level;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Class that manages the use of the Twitter API Keys
 * @author Javier Sanz-Cruzado
 */
public class TwitterManager 
{
    
    /**
     * Instance
     */
    private static TwitterManager instance;
    /**
     * Current key to use
     */
    private TwitterKey currentKey;
    /**
     * Current Twitter API caller to use
     */
    private Twitter twitter;
    /**
     * Remaining requests
     */
    private Integer remainingRequests;
    
    /**
    * Constructor
    */
    private TwitterManager()
    {
        currentKey = null;
        twitter = null;
        remainingRequests = 0;
    }
   
    /**
     * Gets an instance of the twitter manager
     * @return A new TwitterManager if it did not exist before, or the old one
     */
    public static TwitterManager getInstance()
    {
        if(instance == null)
        {
            instance = new TwitterManager();
        }
        
        return instance;
        
    }
    
    /**
     * Obtains a Twitter instance to make searches.
     * @param rateLimitStatus Rate limit status of the current API Search.
     * @return null if an error ocurred, or a Twitter instance in other case.
     */
    public Twitter getTwitter(RateLimitStatus rateLimitStatus, String endpoint)
    {
        if(twitter == null || currentKey == null || rateLimitStatus == null)
        {
            return getTwitterNewKey(endpoint);
        }
        else
        {
            if(rateLimitStatus.getRemaining() > 0)
            {
                return twitter;
            }
            else
            {
                return getTwitterNewKey(currentKey.getKeyId()-1, endpoint);
            }
        }
    }
    /**
     * Obtains a Twitter instance to make searches
     * @return null if an error ocurred, or a Twitter instance
     */
    public Twitter getTwitter(String endpoint)
    {
        return getTwitterNewKey(endpoint);
    }
    
    /**
     * Gets a Twitter instance using a new key. 
     * In case no key is available, this function sleeps the program until a key can be used.
     * @return A Twitter instance if OK, null if ERROR.
     */
    private Twitter getTwitterNewKey(String endpoint)
    {
        return getTwitterNewKey(-1, endpoint);
    }
    
    /**
     * Gets a Twitter instance using a new key. 
     * In case no key is available, this function sleeps the program until a key can be used.
     * @param key Current key id
     * @return A Twitter instance if OK, null if ERROR.
     */
    private Twitter getTwitterNewKey(Integer key, String endpoint)
    {
        TwitterKey current = null;
        Integer secsUntilReset = Integer.MAX_VALUE;
        Integer limit = 0;
        Twitter tw = null;
        try
        {
            List<TwitterKey> keys = TwitterDB.getInstance().getTwitterKeyDAO().list();
            if(keys == null || keys.isEmpty())
            {
                return null;
            }

            if(key == -1L)
            {
                keys.add(new TwitterKey());
                key = keys.size()-1;
            }
            for(int i = (key+1)%keys.size(); i != key%keys.size(); i = (i+1)%keys.size())
            {
                
                try
                {
                    TwitterKey tk = keys.get(i);
                    tw = this.createTwitter(tk);

                    Paging paging = new Paging(1, 200);
                    Map<String, RateLimitStatus> rateLimitStatus = tw.getRateLimitStatus();
                    RateLimitStatus status = rateLimitStatus.get(endpoint);
                    limit = status.getLimit();

                    if(status.getRemaining() > 0)
                    {
                        this.currentKey = tk;
                        this.twitter = tw;
                        this.remainingRequests = status.getRemaining();
                        return tw;
                    }
                    else
                    {
                        if(secsUntilReset > status.getSecondsUntilReset())
                        {
                            secsUntilReset = status.getSecondsUntilReset();
                            current = tk;
                        }
                    }
                }
                catch(TwitterException te)
                {
                    if(te.getStatusCode() == -1)
                    {
                        continue;
                    }
                }
            }

            Boolean interrupted = false;
            System.out.println("Sleep for " + secsUntilReset + " seconds.");
            try {
                Thread.sleep((secsUntilReset + 10) * 1000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(TwitterManager.class.getName()).log(Level.SEVERE, null, ex);
                interrupted = true;
            }
              
            if(interrupted == true)
                return null;
            
            return getTwitterNewKey(endpoint);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Adds a use to the current key
     */
    public void addUseToKey()
    {
        this.currentKey.increaseUses();
    }
    
    /**
     * Creates a Twitter instance from a Twitter API Key
     * @param key Twitter API Key
     * @return The Twitter instance
     */
    private Twitter createTwitter(TwitterKey key)
    {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(key.getConsumerKey()).
                setOAuthConsumerSecret(key.getConsumerSecret()).
                setOAuthAccessToken(key.getAccessToken()).
                setOAuthAccessTokenSecret(key.getAccessTokenSecret()).
                setJSONStoreEnabled(true);
        TwitterFactory twF = new TwitterFactory(cb.build());
        return twF.getInstance();
    }
}
