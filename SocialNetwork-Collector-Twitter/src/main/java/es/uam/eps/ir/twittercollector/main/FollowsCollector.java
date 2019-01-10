/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.main;

import es.uam.eps.ir.twittercollector.collector.FriendCollector;
import es.uam.eps.ir.twittercollector.database.TwitterDB;
import es.uam.eps.ir.twittercollector.database.dao.data.UserDAO;
import es.uam.eps.ir.twittercollector.database.data.User;
import java.util.HashMap;
import java.util.List;
import twitter4j.TwitterException;

/**
 *
 * @author Javier
 */
public class FollowsCollector 
{
    public static void main(String[] args) throws TwitterException, Exception 
    {
        if(args.length < 1)
        {
            System.out.println("Invalid arguments");
            System.out.println("1. Database");
            return;
        }

        /* Load the configuration for the crawler */
       

        /* Initialize the database */
        TwitterDB.startDB(args[0]);

        /* Getting the UserDAO for retrieving previous mentions/replies */
        UserDAO userdao = TwitterDB.getInstance().getUserDAO();
        HashMap<Long, User> map = userdao.hashMap();
        /* Getting the Status Collector, for collecting tweets from the Search API */
        FriendCollector fc = new FriendCollector();

        for(Long user : map.keySet())
        {
            User visited = map.get(user);
            if(userdao.getFollowing(user).isEmpty() == true)
            {
                List<Long> friends;
                try
                {
                    friends = fc.getFriends(user);
                }
                catch(TwitterException ex)
                {
                    System.out.println("ERROR USER " + user);
                    continue;
                }
                for(Long l : friends)
                {
                    if(map.containsKey(l))
                    {
                        User followed = map.get(l);
                        userdao.addFollowRelation(visited, followed);
                    }
                }
                System.out.println("USER VISITED: " + visited.getUserId() + " Num. friends: " + friends.size());
            }
            else
            {
                System.out.println("USER ALREADY VISITED: " + visited.getUserId());
            }
        }
        System.out.println("SAMPLING COMPLETED");
    }
}
