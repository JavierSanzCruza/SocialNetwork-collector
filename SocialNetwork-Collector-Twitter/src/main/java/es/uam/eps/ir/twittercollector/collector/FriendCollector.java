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
import java.util.ArrayList;
import java.util.List;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 *
 * @author Javier
 */
public class FriendCollector 
{
    private static final String FRIENDSENDPOINT = "/friends/ids";
    /**
     * Gets the full list of friends for a given user
     * @param userId
     * @return
     * @throws TwitterException 
     */
    public List<Long> getFriends(Long userId) throws TwitterException
    {
        List<Long> friends = new ArrayList<>();
        Twitter tw = TwitterManager.getInstance().getTwitter(FRIENDSENDPOINT);
        
        IDs ids;
        Long cursor = -1L;
        do
        {
            ids = tw.getFriendsIDs(userId, cursor);
            for(long l : ids.getIDs())
                friends.add(l);
            cursor = ids.getNextCursor();
            tw = TwitterManager.getInstance().getTwitter(FRIENDSENDPOINT);
        }
        while(ids.hasNext());
                
        return friends;
    }   
}
