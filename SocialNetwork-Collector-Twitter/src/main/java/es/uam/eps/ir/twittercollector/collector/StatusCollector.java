/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.collector;
import es.uam.eps.ir.twittercollector.manager.TwitterManager;
import java.util.*;
import twitter4j.*;
/**
 *
 * @author nets
 */
public class StatusCollector 
{
    private static final String STATUSENDPOINT = "/statuses/user_timeline";
    private static final String SINGSTATUSENDPOINT = "/statuses/show/:id";
    private final static int MAX_COUNT_API = 200;
    /**
     * Gets a user timeLine by (maximum) number of tweets to retrieve.
     * @param userId
     * @param maxTweets
     * @return A resultList with the tweets 
     */
    public List<Status> getTimeLine(Long userId, Integer maxTweets) throws TwitterException
    {
        List<Status> status = new ArrayList<Status>() {};
        Integer remaining = maxTweets;
        Integer numToSearch = Math.min(remaining, MAX_COUNT_API);
        Paging paging = new Paging(1,numToSearch);
        Long minStatus = null;
        Boolean stop = false;
        
        Twitter twitter = TwitterManager.getInstance().getTwitter(STATUSENDPOINT);
        do
        {
            try
            {
                ResponseList<Status> resp = twitter.getUserTimeline(userId, paging);

                status.addAll(resp);


                remaining -= resp.size();

                if(resp.size() < numToSearch)
                    stop = true;

                if(stop == false)
                {
                    minStatus = resp.get(resp.size() - 1).getId()-1;
                    if(remaining > 0)
                    {
                        numToSearch = Math.min(remaining, MAX_COUNT_API);
                    }
                    else
                    {
                        numToSearch = 1;
                    }
                    paging = new Paging(1, numToSearch);
                    paging.setMaxId(minStatus);
                    twitter = TwitterManager.getInstance().getTwitter(resp.getRateLimitStatus(), STATUSENDPOINT);
                }
            }
            catch(TwitterException e)
            {
                if(e.getStatusCode() == 401)
                    throw(e);
                else
                {
                    System.out.println("REVISIT: " + userId);
                    stop = true;
                }
            }
        }
        while(remaining > 0 && stop == false);  
        
        return status;
    }
    
    /**
     * Gets a user timeLine by a range of dates number of tweets to retrieve.
     * @param userId
     * @param start
     * @param end
     * @return A resultList with the tweets 
     * @throws twitter4j.TwitterException 
     */
    public List<Status> getTimeLine(Long userId, Date start, Date end) throws TwitterException
    {
        List<Status> status = new ArrayList<Status>() {};
        Paging paging = new Paging(1,MAX_COUNT_API);
        Long minStatus;
        Boolean stop = false;
        
        Twitter twitter = TwitterManager.getInstance().getTwitter(STATUSENDPOINT);
        do
        {
            try
            {
                ResponseList<Status> resp = twitter.getUserTimeline(userId, paging);
                for(Status st : resp)
                {
                    if(st.getCreatedAt().compareTo(start) > 0 && st.getCreatedAt().compareTo(end)< 0)
                    {
                        status.add(st);
                    }
                    else if(st.getCreatedAt().compareTo(start) < 0)
                    {
                        stop = true;
                    }
                }

                if(resp.isEmpty() == false && resp.size() == MAX_COUNT_API)
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
            catch(TwitterException e)
            {
                if(e.getStatusCode() == 401)
                    throw(e);
                else
                {
                    System.out.println("REVISIT: " + userId);
                    stop = true;
                }
            }
        }while(stop == false);  
        
        return status;
    }

    /**
     * Gets a user timeLine by a range of dates number of tweets to retrieve.
     * @param userId
     * @param start
     * @param end
     * @param maxTweets
     * @return A resultList with the tweets 
     * @throws twitter4j.TwitterException 
     */
    public List<Status> getTimeLine(Long userId, Date start, Date end, Integer maxTweets) throws TwitterException
    {
        List<Status> status = new ArrayList<Status>() {};
        Paging paging = new Paging(1,MAX_COUNT_API);
        Long minStatus;
        Boolean stop = false;
        
        Twitter twitter = TwitterManager.getInstance().getTwitter(STATUSENDPOINT);
        do
        {
            ResponseList<Status> resp = twitter.getUserTimeline(userId, paging);
            for(Status st : resp)
            {
                if(st.getCreatedAt().compareTo(start) > 0 && st.getCreatedAt().compareTo(end)< 0)
                {
                    status.add(st);
                    if(status.size() >= maxTweets)
                    {
                        stop = true;
                        break;
                    }
                }
                else if(st.getCreatedAt().compareTo(end) > 0)
                {
                    stop = true;
                    break;
                }
            }
            minStatus = resp.get(resp.size() - 1).getId();
            paging = new Paging(1, MAX_COUNT_API);
            paging.setMaxId(minStatus);
            twitter = TwitterManager.getInstance().getTwitter(resp.getRateLimitStatus(), STATUSENDPOINT);
        }while(stop == false);  
        
        return status;
    }
    
    public Status getStatus(Long statusId) throws TwitterException
    {
        Twitter twitter = TwitterManager.getInstance().getTwitter(SINGSTATUSENDPOINT);
        return twitter.showStatus(statusId);
    }
}
