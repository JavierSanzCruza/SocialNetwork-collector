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
 * Separates the tweets according a given date. Every tweet before that date becomes the training data, and
 * every tweet after that date becomes the test data. This class assumes the edges are not weighted.
 * @author Javier Sanz-Cruzado
 */
public class DateTemporalPartitionNoReps extends TemporalPartition
{
    /**
     * Date of separation
     */
    private final Date date;

    /**
     * Constructor.
     * @param database Database name (for dao.jdbc).
     * @param date Limit date.
     * @throws TwitterCollectorException Error obtaining the database instance.
     */
    public DateTemporalPartitionNoReps(String database, Date date) throws TwitterCollectorException 
    {
        super(database);
        this.date = date;
    }
    
    @Override
    public void doPartition(String trainFile, String testFile) 
    {
        int numTweets = this.getTwitterDB().getTweetDAO().countTweets();
        //Obtaining the tweet list order by creation date
        
        DirectedGraph<String, String> graph = new DirectedSparseGraph<>();
        DirectedGraphWriter dgw = new DirectedGraphWriter();
        int offset = 0;
        int count = 10000000;
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

                    if(tw.getCreated().before(this.date)) //Add to train
                    {
                        if(this.train.findEdge(us.getUserId() + "", cr.getUserId() + "") == null)
                            this.train.addEdge(this.train.getEdgeCount()+1, us.getUserId() + "", cr.getUserId() + "");
                    }
                    else //Add to test
                    {
                        if(this.train.containsVertex(us.getUserId()+"") && this.train.containsVertex(cr.getUserId()+""))
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

                        if(tw.getCreated().before(this.date)) //Add to train
                        {
                            for(Long mention : mentioned)
                            {
                                User cr = this.getTwitterDB().getUserDAO().find(mention);
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
                                if(this.train.containsVertex(us.getUserId()+"") && this.train.containsVertex(cr.getUserId()+""))
                                    if(this.train.findEdge(us.getUserId() + "", cr.getUserId() + "") == null && this.test.findEdge(us.getUserId() + "", cr.getUserId() + "") == null)
                                    {
                                        this.test.addEdge(this.test.getEdgeCount()+1, us.getUserId() + "", cr.getUserId() + "");
                                    }
                            }
                        }
                    }
                } 
                
                ++i;
                if(i % 10000 == 0)
                    System.out.println(i);
            }
            offset = i;
            System.out.println(offset);
        }
        dgw.writeGraph(this.train, trainFile);
        dgw.writeGraph(this.test, testFile);
    }
    
    
}
