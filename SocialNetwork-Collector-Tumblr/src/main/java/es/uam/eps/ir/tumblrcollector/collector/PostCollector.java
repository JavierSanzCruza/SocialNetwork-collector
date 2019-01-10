/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.collector;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;
import es.uam.eps.ir.tumblrcollector.manager.TumblrManager;
import java.util.*;


/**
 *
 * @author Javier
 */
public class PostCollector 
{
    private static final int MAX_POST_1CALL = 20;
    
    public List<Post> getTimeLine(String blogName, Integer maxPosts)
    {
        List<Post> posts = new ArrayList<>();
        TumblrManager tm = TumblrManager.getInstance();
        JumblrClient jc = tm.getTumblr();
        int currentListSize;
        int newSize;
        int difference;
        //Offset
        int numObtained = 0;
        int limit;
        boolean failed = false;
        do
        {
            currentListSize = posts.size();
            
            // The number of posts to be retrieved
            limit = (maxPosts - numObtained > MAX_POST_1CALL) ? MAX_POST_1CALL : maxPosts-numObtained;
            
            //API CALL
            Map<String,Object> params = new HashMap<>();
            params.put("reblog_info", true); //We want information about reblogs
            params.put("notes_info", true); // every note we can retrieve
            params.put("filter", "text");
            params.put("limit", limit);
            params.put("offset", numObtained);
            Boolean cont = false;
            int counter = 0;
            try
            {
                posts.addAll(jc.blogPosts(blogName, params));
            }
            catch(IllegalStateException ise)
            {
                failed = true;
                System.out.println("Illegal State Exception");
            }
            
            if(failed)
            {
                return new ArrayList<>();
            }
            
            newSize = posts.size();
            
            difference = newSize - currentListSize;
            numObtained += difference;
            
        }while(numObtained < maxPosts && limit == difference);
        
        return posts;
    }
    
    public List<Post> getTimeLine(String blogName, Date startDate, Date endDate)
    {
        List<Post> posts = new ArrayList<>();
        TumblrManager tm = TumblrManager.getInstance();
        JumblrClient jc = tm.getTumblr();
        Long start = startDate.getTime();
        Long end = endDate.getTime();
        Long timestamp = start;
   
        int difference;
        //Offset
        int numObtained = 0;
        int limit = MAX_POST_1CALL;
        do
        {            
            // The number of posts to be retrieved
            limit = MAX_POST_1CALL;
            
            //API CALL
            Map<String,Object> params = new HashMap<>();
            params.put("reblog_info", true); //We want information about reblogs
            params.put("notes_info", true); // every note we can retrieve
            params.put("filter", "text");
            params.put("limit", limit);
            params.put("offset", numObtained);
            
            List<Post> retrieved = jc.blogPosts(blogName, params);
            for(Post p : retrieved)
            {
                //if it is the last one, we store its timestamp
                if(p.equals(retrieved.get(retrieved.size()-1)))
                {
                    timestamp = p.getTimestamp();
                }
                if(p.getTimestamp() >= start && p.getTimestamp() <= end)
                {
                    posts.add(p);
                }
            }
            
            posts.addAll(jc.blogPosts(blogName, params));
            difference = retrieved.size();
            numObtained += difference;            
        }while(timestamp > start && difference == limit);
        
        return posts;
    }
    
    public List<Post> getTimeLine(String blogName, Integer maxPosts, Date startDate, Date endDate)
    {
        List<Post> posts = new ArrayList<>();
        TumblrManager tm = TumblrManager.getInstance();
        JumblrClient jc = tm.getTumblr();
        Long start = startDate.getTime();
        Long end = endDate.getTime();
        Long timestamp = start;
   
        int difference = 0;
        //Offset
        int numObtained = 0;
        int limit = MAX_POST_1CALL;
        do
        {            
            // The number of posts to be retrieved
            limit = MAX_POST_1CALL;
            
            //API CALL
            Map<String,Object> params = new HashMap<>();
            params.put("reblog_info", true); //We want information about reblogs
            params.put("notes_info", true); // every note we can retrieve
            params.put("filter", "text");
            params.put("limit", limit);
            params.put("offset", numObtained);
            
            List<Post> retrieved = jc.blogPosts(blogName, params);
            for(Post p : retrieved)
            {
                //if it is the last one, we store its timestamp
                if(p.equals(retrieved.get(retrieved.size()-1)))
                {
                    timestamp = p.getTimestamp();
                }
                if(p.getTimestamp() >= start && p.getTimestamp() <= end && posts.size() < maxPosts)
                {
                    posts.add(p);
                }
            }
            
            posts.addAll(jc.blogPosts(blogName, params));
            difference = retrieved.size();
            numObtained += difference;            
        }while(timestamp > start && posts.size() < maxPosts && difference == limit);
        
        return posts;        
    }
}
