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
import es.uam.eps.ir.graph.directed.DirectedGraphWriter;
import es.uam.eps.ir.twittercollector.database.data.*;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import java.util.*;

/**
 * Separates the tweets temporally according to a certain percentage p. p tweets become the training data, and
 * the rest of them become the test data. This class assumes the edges are not weighted.
 * @author Javier Sanz-Cruzado
 */
public class PercentageTemporalPartitionNoReps extends TemporalPartition
{
    /**
     * Date of separation
     */
    private double percentage;

    /**
     * Constructor.
     * @param database Database name (for dao.jdbc).
     * @param percentage Percentage of train data. From 0.0 to 1.0. If percentage is smaller than 0.0, it will be used as 0.0.
     * If it is greater than 1.0, it will be used as 1.0.
     * @throws TwitterCollectorException Error obtaining the database instance.
     */
    public PercentageTemporalPartitionNoReps(String database, double percentage) throws TwitterCollectorException 
    {
        super(database);
        if(percentage < 0.0)
        {
            this.percentage = 0.0;
        }
        else if(percentage > 1.0)
        {
            this.percentage = 1.0;
        }
        else
        {
            this.percentage = percentage;
        }
    }
    
    @Override
    public void doPartition(String trainFile, String testFile) 
    {
        //Obtaining the tweet list order by creation date
        Integer numTweets = this.getTwitterDB().getTweetDAO().countTweets();
        
        //Obtaining the number of tweets in training
        double numTraining = numTweets*this.percentage;
        Integer count = 10000000;
        Integer offset = 0;
        DirectedGraph<String, String> graph = new DirectedSparseGraph<>();
        DirectedGraphWriter dgw = new DirectedGraphWriter();
        
        int i = 0;
        while(offset < numTweets)
        {
            List<Tweet> tweets = this.getTwitterDB().getTweetDAO().getIntervalTweets(offset, count);
            while(tweets.isEmpty() == false)
            {

                Tweet tw = tweets.get(0);
                tweets.remove(tw);
                //Check if it is a retweet. In that case, find the other tweet in the database to add the edge to the graph
                Tweet orig = this.getTwitterDB().getTweetDAO().isRetweet(tw);
                if(orig != null) // The studied tweet is a retweet
                {
                    User us = this.getTwitterDB().getUserDAO().find(tw.getUserId());
                    User cr = this.getTwitterDB().getUserDAO().find(orig.getUserId());

                    if(i < numTraining) //Add to train
                    {
                        if(this.train.findEdge(us.getUserId() + "", cr.getUserId() + "") == null)
                            this.train.addEdge(this.train.getEdgeCount()+1, us.getUserId() + "", cr.getUserId() + "");
                    }
                    else if(this.train.containsVertex(us.getUserId()+"") && this.train.containsVertex(cr.getUserId() + ""))//Add to test
                    {
                        if(this.train.findEdge(us.getUserId() + "", cr.getUserId() + "") == null && this.test.findEdge(us.getUserId() + "", cr.getUserId() + "") == null)
                            this.test.addEdge(this.test.getEdgeCount()+1, us.getUserId() + "", cr.getUserId() + "");
                    }



                }
                else //Check if it is a mention
                {
                    List<Long> mentioned = this.getTwitterDB().getTweetDAO().isMention(tw);
                    if(mentioned.size() > 0) //The studied tweet has mentions
                    {
                        User us = this.getTwitterDB().getUserDAO().find(tw.getUserId());

                        if(i < numTraining) //Add to train
                        {
                            for(Long mention : mentioned)
                            {
                                User cr = this.getTwitterDB().getUserDAO().find(mention);
                                if(cr == null)
                                    continue;
                                
                                if(this.train.findEdge(us.getUserId() + "", cr.getUserId() + "") == null)
                                {
                                    this.train.addEdge(this.train.getEdgeCount()+1, us.getUserId() + "", cr.getUserId() + "");
                                }
                            }
                        }
                        else // Add to test
                        {
                            for(Long mention : mentioned)
                            {
                                User cr = this.getTwitterDB().getUserDAO().find(mention);
                                if(this.train.containsVertex(us.getUserId() + "") && this.train.containsVertex(cr.getUserId()+ ""))
                                {
                                    if(this.train.findEdge(us.getUserId() + "", cr.getUserId() + "") == null && this.test.findEdge(us.getUserId() + "", cr.getUserId() + "") == null)
                                    {
                                        this.test.addEdge(this.test.getEdgeCount()+1, us.getUserId() + "", cr.getUserId() + "");
                                    }
                                }
                            }
                        }
                    }
                }

                ++i;
                if(i % 10000 == 0 && i != 0)
                    System.out.println("OFFSET: " + i);            
            }
            offset = i;
            
        }
        
        dgw.writeGraph(this.train, trainFile);
        dgw.writeGraph(this.test, testFile);
    }
    
    
}
