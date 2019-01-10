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
import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOFactory;
import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOHandler;
import es.uam.eps.ir.twittercollector.database.data.UsersRetweet;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import java.util.List;

/**
 *
 * @author Javier
 */
public class TwoDatabasesPartition extends Partition
{
    TwitterDAOFactory secondDB;

    public TwoDatabasesPartition(String database, String database2) throws TwitterCollectorException 
    {
        super(database);
        TwitterDAOHandler.getInstance().addFactory(database2);
        secondDB = TwitterDAOHandler.getInstance().getFactory(database2);
    }
    
    
    @Override
    public void doPartition(String trainFile, String testFile) 
    {
        TwitterDB tdb = this.getTwitterDB();
        List<UsersRetweet> usRts = tdb.getUsersRTDAO().list();
        DirectedGraph<String, String> graph = new DirectedSparseGraph<>();
        DirectedGraphWriter dgw = new DirectedGraphWriter();
        for(UsersRetweet rt : usRts)
        {
            train.addEdge(this.train.getEdgeCount()+1, rt.getRetweeter()+"", rt.getCreator()+"");
        }
        
        usRts = secondDB.getUsersRetweetDAO().list();
        for(UsersRetweet rt : usRts)
        {
            if(train.containsVertex(rt.getCreator()+"") && train.containsVertex(rt.getRetweeter()+""))
            {
                if(train.findEdge(rt.getRetweeter()+"", rt.getCreator()+"") == null)
                {
                    test.addEdge(this.test.getEdgeCount()+1, rt.getRetweeter()+"", rt.getCreator()+"");
                }
            }
        }
        
        dgw.writeGraph(train, trainFile);
        dgw.writeGraph(test, testFile);
    }
    
}
