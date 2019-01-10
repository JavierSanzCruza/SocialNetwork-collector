/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.tweetcollector;

import static es.uam.eps.ir.twittercollector.grid.collector.tweetcollector.TweetCollectorIdentifiers.*;
import es.uam.eps.ir.twittercollector.tweetcollector.TweetCollector;

/**
 * Class for selecting and configuring tweet collectors.
 * @author Javier Sanz-Cruzado Puig
 */
public class TweetCollectorSelector 
{
    /**
     * Given a set of parameters, selects and configures a tweet collector.
     * @param uepr the parameters for the collector.
     * @return the tweet collector if everything went OK, null otherwise.
     */
    public TweetCollector select(TweetCollectorParamReader uepr)
    {
        String name = uepr.getName();
        TweetCollectorConfigurator conf;
        switch(name)
        {
            case COUNT:
                conf = new CountTweetCollectorConfigurator();
                break;
            case TIMESTAMP:
                conf = new TimestampedTweetCollectorConfigurator();
                break;
            case COUNTTIMESTAMP:
                conf = new CountTimestampedTweetCollectorConfigurator();
                break;
            default:
                return null;
        }
        
        TweetCollector collector = conf.configure(uepr);
        return collector;
    }
}
