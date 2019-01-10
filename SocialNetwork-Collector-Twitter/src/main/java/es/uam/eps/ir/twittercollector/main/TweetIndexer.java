/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.main;

import es.uam.eps.ir.twittercollector.indexer.SimpleTweetParser;
import es.uam.eps.ir.twittercollector.indexer.TwitterIndexer;
import es.uam.eps.ir.utils.exceptions.NotInitializedException;

/**
 *
 * @author Javier
 */
public class TweetIndexer 
{
    public static void main(String[] args) throws NotInitializedException
    {
        if(args.length < 2)
        {
            System.out.println("Usage: <database> <indexPath>");
            return;
        }
        
        TwitterIndexer tindex = new TwitterIndexer(args[1]);
        tindex.initializeWriting(true);
        tindex.build(args[0], new SimpleTweetParser());
    }
}
