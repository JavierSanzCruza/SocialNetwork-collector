/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.main;

import es.uam.eps.ir.twittercollector.collector.DatasetCollector;
import es.uam.eps.ir.twittercollector.collector.StatusCollector;
import es.uam.eps.ir.twittercollector.database.TwitterDB;
import es.uam.eps.ir.twittercollector.database.dao.data.UserDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UserInteractionDAO;
import es.uam.eps.ir.twittercollector.database.data.Interaction;
import es.uam.eps.ir.twittercollector.database.data.LinkType;
import es.uam.eps.ir.twittercollector.database.data.User;
import es.uam.eps.ir.twittercollector.grid.collector.InteractionsCollectorGrid;
import es.uam.eps.ir.twittercollector.manager.NewTwitterConfiguration;
import es.uam.eps.ir.twittercollector.manager.TwitterProcessing;
import es.uam.eps.ir.twittercollector.manager.TwitterConfiguration;
import es.uam.eps.ir.twittercollector.manager.TwitterManager;
import es.uam.eps.ir.twittercollector.manager.TwitterStore;
import java.util.*;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * Collects tweets from Twitter Search API
 * @author Javier Sanz-Cruzado
 */
public class TwitterCollector 
{

    /**
     * @param args the command line arguments: Configuration file route
     * @throws twitter4j.TwitterException
     */
    public static void main(String[] args) throws TwitterException, Exception 
    {
        if(args.length < 1)
        {
            System.out.println("Invalid arguments");
            System.out.println("1. Configuration file route");
            return;
        }

        String conf = args[0];
        InteractionsCollectorGrid grid = new InteractionsCollectorGrid(conf);
        grid.readDocument();
        
        NewTwitterConfiguration config = new NewTwitterConfiguration(grid);
        DatasetCollector datasetCollector = new DatasetCollector();
        datasetCollector.configure(config);
        datasetCollector.collect(); 
    }
}