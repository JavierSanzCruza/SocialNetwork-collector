/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.tweetcollector;

import java.util.List;
import twitter4j.Status;

/**
 * Interface for collecting tweets.
 * @author Javier Sanz-Cruzado Puig
 */
public interface TweetCollector 
{
    /**
     * Get the timeline of a user.
     * @param user the user
     * @return the list of raw tweets.
     */
    public List<Status> getTimeline(long user);
    
}
