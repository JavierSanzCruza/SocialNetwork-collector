/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.tweetcollector;

import es.uam.eps.ir.twittercollector.manager.TwitterManager;
import java.util.ArrayList;
import java.util.List;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Tweet collector that establishes a fixed number of tweets.
 * @author Javier Sanz-Cruzado Puig
 */
public class CountTweetCollector implements TweetCollector
{
    /**
     * Maximum number of tweets to retrieve
     */
    private final int maxTweets;
    
    /**
     * Twitter API Endpoint for the timeline of a user
     */
    private static final String STATUSENDPOINT = "/statuses/user_timeline";
    
    /**
     * Maximum number of tweets allowed by the Twitter API
     */
    private final static int MAX_COUNT_API = 200;
    
    /**
     * Constructor.
     * @param maxTweets the maximum number of tweets to retrieve.
     */
    public CountTweetCollector(int maxTweets)
    {
        this.maxTweets = maxTweets;
    }
    
    @Override
    public List<Status> getTimeline(long user) 
    {
        // Initialize the search.
        List<Status> status = new ArrayList<>();
        int remaining = this.maxTweets;
        int numToSearch = Math.min(remaining, MAX_COUNT_API);
        
        // Initialize paging
        Paging paging = new Paging(1, numToSearch);
        long minStatus;
        boolean stop = false;
        
        Twitter twitter = TwitterManager.getInstance().getTwitter(STATUSENDPOINT);
        while(remaining > 0 && stop == false)
        {
            try
            {
                ResponseList<Status> resp = twitter.getUserTimeline(user, paging);
                status.addAll(resp);
                remaining -= resp.size();
                
                // If we could not retrieve as many tweets as we wanted, stop.
                if(resp.size() < numToSearch)
                {
                    stop = true;
                }
                else
                {
                    
                    minStatus = resp.get(resp.size() - 1).getId() - 1;
                    if(remaining > 0)
                    {
                        numToSearch = Math.min(remaining, MAX_COUNT_API);
                    }
                    else
                    {
                        stop = true;
                    }
                    
                    // Update paging for obtaining the next tweets.
                    paging = new Paging(1, numToSearch);
                    paging.setMaxId(minStatus);
                    twitter = TwitterManager.getInstance().getTwitter(resp.getRateLimitStatus(), STATUSENDPOINT);
                }
            }
            catch(TwitterException ioe)
            {
                if(ioe.getStatusCode() == 401)
                {
                    TwitterManager.getInstance().addUseToKey();
                    return null;
                }
                else
                {
                    System.out.println("Revisit: " + user);
                    stop = true;
                }
            }
        }
        
        return status;
    }
    
}
