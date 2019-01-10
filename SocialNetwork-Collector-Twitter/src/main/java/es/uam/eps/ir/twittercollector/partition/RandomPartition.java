/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.partition;

import es.uam.eps.ir.graph.directed.DirectedGraphWriter;
import es.uam.eps.ir.twittercollector.database.data.User;
import es.uam.eps.ir.twittercollector.database.data.UsersRetweet;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import java.util.*;

/**
 * Partition method that divides data randomly (a percentage for test, and a percentage for train)
 * @author Javier Sanz-Cruzado
 */
public class RandomPartition extends Partition
{
    /**
     * Train percentage
     */
    private double percentage;
    
    
    public RandomPartition(String database, double percentage) throws TwitterCollectorException 
    {
        super(database);
        this.percentage = percentage;
    }

    @Override
    public void doPartition(String trainFile, String testFile) 
    {
        List<UsersRetweet> usersretweet = this.getTwitterDB().getUsersRTDAO().list();
        List<Integer> train = new ArrayList<Integer>();
        List<Integer> test = new ArrayList<Integer>();
        double numTrain = usersretweet.size()*percentage;
        
        DirectedGraphWriter dgw = new DirectedGraphWriter();
        Random rand = new Random();
        for(int i = 0; i < numTrain; ++i)
        {
            int next = rand.nextInt(usersretweet.size());
            
            if(train.contains(next) == false)
            {
                train.add(next);
            }
            else
            {
                --i;
            }
        }
        
        for(Integer j : train)
        {
            User us = this.getTwitterDB().getUserDAO().find(usersretweet.get(j).getRetweeter());
            User cr = this.getTwitterDB().getUserDAO().find(usersretweet.get(j).getCreator());
            this.train.addEdge(this.train.getEdgeCount()+1, us.getScreenName(), cr.getScreenName());
        }
        
        for(Integer j : test)
        {
            User us = this.getTwitterDB().getUserDAO().find(usersretweet.get(j).getRetweeter());
            User cr = this.getTwitterDB().getUserDAO().find(usersretweet.get(j).getCreator());
            this.test.addEdge(this.test.getEdgeCount()+1, us.getScreenName(), cr.getScreenName());
        }
        
        dgw.writeGraph(this.train, trainFile);
        dgw.writeGraph(this.test, testFile);
    }
    
}
