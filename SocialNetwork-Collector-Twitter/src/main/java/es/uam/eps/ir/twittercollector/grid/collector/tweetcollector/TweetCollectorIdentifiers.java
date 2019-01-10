/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.tweetcollector;

/**
 * Identifiers for the tweet collectors.
 * @author Javier Sanz-Cruzado Puig
 */
public class TweetCollectorIdentifiers 
{
    
    public final static String COUNT = "count";
    public final static String TIMESTAMP = "timestamp";
    public final static String COUNTTIMESTAMP = "count timestamp";
    
    /**
     * Prints the list of available tweet collectors.
     * @return a string containing the list of available tweet collectors.
     */
    public String list()
    {
        String list = "Tweet collector:\n";
        list += "\t" + COUNT + "\n";
        list += "\t" + TIMESTAMP + "\n";
        list += "\t" + COUNTTIMESTAMP + "\n";
        return list;
    }
}
