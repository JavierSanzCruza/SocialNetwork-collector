/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.partition;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import es.uam.eps.ir.graph.directed.DirectedGraphWriter;
import es.uam.eps.ir.twittercollector.database.TwitterDB;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import es.uam.eps.ir.twittercollector.database.data.User;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import es.uam.eps.ir.utils.Par;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class GetInteractionGraph 
{
    /**
     * Twitter database.
     */
    private final TwitterDB tdb;
    /**
     * Moments of creation of the different links in the graph.
     */
    private Map<Par<Long,Long>,Long> timestamps;

    /**
     * Constructor.
     * @param database Database name (for dao.jdbc).
     * @throws TwitterCollectorException Error obtaining the database instance.
     */
    public GetInteractionGraph(String database) throws TwitterCollectorException 
    {
        TwitterDB.startDB(database);
        
        this.tdb = TwitterDB.getInstance();
        this.timestamps = new HashMap<>();
    }
    
    /**
     * Getter of the Twitter DB.
     * @return the TwitterDB object.
     */
    public TwitterDB getTwitterDB()
    {
        return this.tdb;
    }
    
    /**
     * Generates a text file containing the interaction graph, with timestamps.
     * @param graphFile The output file.
     */
    public void generateGraph(String graphFile) 
    {
        int numTweets = this.getTwitterDB().getTweetDAO().countTweets();
        //Obtaining the tweet list order by creation date
        
        DirectedGraph<String, String> graph = new DirectedSparseGraph<>();
        DirectedGraphWriter dgw = new DirectedGraphWriter();
        int offset = 0;
        int count = 10000000;
        int i = 0;
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(graphFile))))
        {
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

                        Par<Long, Long> par = new Par<>(us.getUserId(),cr.getUserId());

                        if(!timestamps.containsKey(par)) //Add to train
                        {
                            timestamps.put(par,tw.getCreated().getTime());
                            bw.write(par.getFirst()+"\t"+par.getSecond()+"\t"+tw.getCreated().getTime() + "\n");
                        }
                    }
                    else //Check if it is a mention
                    {
                        List<Long> mentioned = this.getTwitterDB().getTweetDAO().isMention(tw);
                        if(mentioned.size() > 0) //The studied tweet has mentions
                        {
                            User us = this.getTwitterDB().getUserDAO().find(tw.getUserId());

                            for(Long mention : mentioned)
                            {
                                User cr = this.getTwitterDB().getUserDAO().find(mention);
                                Par<Long,Long> par = new Par<>(us.getUserId(),cr.getUserId());

                                if(!timestamps.containsKey(par))
                                {
                                    timestamps.put(par, tw.getCreated().getTime());
                                    bw.write(par.getFirst()+"\t"+par.getSecond()+"\t"+tw.getCreated().getTime()+"\n");
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
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws TwitterCollectorException
    {
        if(args.length < 2)
        {
            System.out.println("Usage: <database> <graphfile>");
            return;
        }
        
        GetInteractionGraph gig = new GetInteractionGraph(args[0]);
        gig.generateGraph(args[1]);
        
    }
}
