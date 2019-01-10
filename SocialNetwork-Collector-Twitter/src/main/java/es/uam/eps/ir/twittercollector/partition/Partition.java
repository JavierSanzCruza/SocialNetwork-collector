/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.partition;

import edu.uci.ics.jung.graph.*;
import es.uam.eps.ir.twittercollector.database.TwitterDB;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import es.uam.eps.ir.twittercollector.database.data.User;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import java.util.*;

/**
 * Abstract class that shows the methods necessary for a partition.
 * @author Javier Sanz-Cruzado
 */
public abstract class Partition 
{
    /**
     * Twitter Database instance
     */
    private TwitterDB tdb;
    /**
     * JUNG Directed Graph for the train data
     */
    protected DirectedGraph<String, Integer> train;
    /**
     * JUNG Directed Graph for the test data
     */
    protected DirectedGraph<String, Integer> test;
    
    
    public Partition()
    {
        this.train = new DirectedSparseGraph<>();
        this.test = new DirectedSparseGraph<>();
    }
    /**
     * Initializes a partition method
     * @param database
     * @throws TwitterCollectorException 
     */
    public Partition(String database) throws TwitterCollectorException
    {
        TwitterDB.startDB(database);
        
        this.tdb = TwitterDB.getInstance();
        this.train = new DirectedSparseGraph<>();
        this.test = new DirectedSparseGraph<>();
        
        List<User> users = this.tdb.getUserDAO().list();
        for(User us : users)
        {
            this.train.addVertex(us.getScreenName());
            this.test.addVertex(us.getScreenName());
        }
    }
    /**
     * Does a partition of the dataset
     * @param trainFile Name of the file we want to store the train graph in
     * @param testFile Name of the file we want to store the test graph in
     */
    public abstract void doPartition(String trainFile, String testFile);
    
    /**
     * Getter for the Twitter Database.
     * @return Returns the Twitter Database.
     */
    protected TwitterDB getTwitterDB()
    {
        return tdb;
    }
    
}
