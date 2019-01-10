/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.tweetcollector;

import es.uam.eps.ir.twittercollector.tweetcollector.CountTimestampedTweetCollector;
import es.uam.eps.ir.twittercollector.tweetcollector.TweetCollector;
import java.util.Date;

/**
 * Configurator for a CountTimestamped tweet collector.
 * @author Javier Sanz-Cruzado Puig
 * @see es.uam.eps.ir.twittercollector.tweetcollector.CountTimestampedTweetCollector
 */
public class CountTimestampedTweetCollectorConfigurator implements TweetCollectorConfigurator
{
    /**
     * Identifier for the maximum number of tweets to retrieve
     */
    private final static String MAXCOUNT = "maxCount";
    /**
     * Identifier for the minimum date of the tweets.
     */
    private final static String INITIALDATE = "initialDate";
    /**
     * Identifier for the maximum date of the tweets.
     */
    private final static String ENDDATE = "endDate";
    
    @Override
    public TweetCollector configure(TweetCollectorParamReader params) 
    {
        int maxCount = params.getParams().getIntegerValue(MAXCOUNT);
        long initialDate = params.getParams().getLongValue(INITIALDATE);
        long endDate = params.getParams().getLongValue(ENDDATE);
        
        Date id = new Date(initialDate);
        Date ed = new Date(endDate);
        return new CountTimestampedTweetCollector(maxCount, id, ed);
    }
    
}
