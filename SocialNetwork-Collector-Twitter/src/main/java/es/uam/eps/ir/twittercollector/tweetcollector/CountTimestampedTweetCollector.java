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
import java.util.Date;
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
public class CountTimestampedTweetCollector implements TweetCollector
{
    /**
     * Maximum number of tweets to retrieve
     */
    private final int maxTweets;
    /**
     * Initial timestamp
     */
    private final Date startDate;
    /**
     * Final timestamp
     */
    private final Date endDate;
    
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
     * @param maxTweets maximum number of tweets to recover
     * @param startDate initial timestamp
     * @param endDate final timestamp
     */
    public CountTimestampedTweetCollector(int maxTweets, Date startDate, Date endDate)
    {
        this.maxTweets = maxTweets;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    @Override
    public List<Status> getTimeline(long user) 
    {
        // Initialize the search.
        List<Status> status = new ArrayList<>();
        
        // Initialize paging
        Paging paging = new Paging(1, MAX_COUNT_API);
        long minStatus;
        boolean stop = false;
        
        Twitter twitter = TwitterManager.getInstance().getTwitter(STATUSENDPOINT);
        while(stop == false)
        {
            try
            {
                ResponseList<Status> resp = twitter.getUserTimeline(user, paging);
                for(Status st : resp)
                {
                    if(st.getCreatedAt().compareTo(startDate) >= 0 && st.getCreatedAt().compareTo(endDate) <= 0 && status.size() < this.maxTweets)
                    {
                        status.add(st);
                    }
                    else if(st.getCreatedAt().compareTo(startDate) < 0)
                    {
                        stop = true;
                    }
                }
                
                // If it is possible to retrieve more tweets.
                if(!resp.isEmpty() && resp.size() == MAX_COUNT_API)
                {
                    minStatus = resp.get(resp.size() - 1).getId();
                    paging = new Paging(1, MAX_COUNT_API);
                    paging.setMaxId(minStatus);
                    twitter = TwitterManager.getInstance().getTwitter(resp.getRateLimitStatus(), STATUSENDPOINT);
                }
                else
                {
                    stop = true;
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
