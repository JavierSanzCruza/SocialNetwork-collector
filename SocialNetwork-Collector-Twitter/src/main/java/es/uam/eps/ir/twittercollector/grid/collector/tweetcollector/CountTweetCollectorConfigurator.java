/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.tweetcollector;

import es.uam.eps.ir.twittercollector.tweetcollector.CountTweetCollector;
import es.uam.eps.ir.twittercollector.tweetcollector.TweetCollector;

/**
 * Configurator for a Count tweet collector.
 * @author Javier Sanz-Cruzado Puig
 * @see es.uam.eps.ir.twittercollector.tweetcollector.CountTweetCollector
 */
public class CountTweetCollectorConfigurator implements TweetCollectorConfigurator
{
    /**
     * Identifier for the maximum number of tweets to retrieve for each user.
     */
    private final static String MAXCOUNT = "maxCount";
    
    @Override
    public TweetCollector configure(TweetCollectorParamReader params) 
    {
        int maxCount = params.getParams().getIntegerValue(MAXCOUNT);
        return new CountTweetCollector(maxCount);
    }
    
}
