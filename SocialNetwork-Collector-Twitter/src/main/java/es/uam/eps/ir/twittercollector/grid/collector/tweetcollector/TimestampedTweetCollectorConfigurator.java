/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.tweetcollector;

import es.uam.eps.ir.twittercollector.tweetcollector.TimestampedTweetCollector;
import es.uam.eps.ir.twittercollector.tweetcollector.TweetCollector;
import java.util.Date;

/**
 * Configurator for a timestamp-based tweet collector.
 * @author Javier Sanz-Cruzado Puig
 * @see es.uam.eps.ir.twittercollector.tweetcollector.TimestampedTweetCollector
 */
public class TimestampedTweetCollectorConfigurator implements TweetCollectorConfigurator
{
    /**
     * Identifier for the minimum date of the tweets
     */
    private final static String INITIALDATE = "initialDate";
    /**
     * Identifier for the maximum date of the tweets.
     */
    private final static String ENDDATE = "endDate";
    @Override
    public TweetCollector configure(TweetCollectorParamReader params) 
    {
        long initialDate = params.getParams().getLongValue(INITIALDATE);
        long endDate = params.getParams().getLongValue(ENDDATE);
        
        Date id = new Date(initialDate);
        Date ed = new Date(endDate);
        return new TimestampedTweetCollector(id, ed);
    }
    
}
