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
import es.uam.eps.ir.graph.directed.DirectedGraphReader;
import es.uam.eps.ir.graph.directed.DirectedGraphWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ComparativePartition extends Partition {

    DirectedGraph<String, Integer> trainGraph;
    DirectedGraph<String, Integer> testGraph;
    DirectedGraph<String, Integer> subsampleGraph;
    
    public ComparativePartition(String trainGraph, String testGraph, String subsampleGraph)
    {
        super();
        DirectedGraphReader dgr = new DirectedGraphReader();
        this.trainGraph = dgr.readDirectedGraph(trainGraph,"\t");
        this.testGraph = dgr.readDirectedGraph(testGraph,"\t");
        this.subsampleGraph = dgr.readDirectedGraph(subsampleGraph,"\t");
    }
    
    @Override
    public void doPartition(String trainFile, String testFile) 
    {
        
        for(Integer edge : subsampleGraph.getEdges())
        {
            String orig = subsampleGraph.getSource(edge);
            String dest = subsampleGraph.getDest(edge);
            
            if(trainGraph.findEdge(orig, dest) != null)
            {
                if(!this.train.containsVertex(orig))
                {
                    train.addVertex(orig);
                }
                
                if(!this.train.containsVertex(dest))
                {
                    train.addVertex(dest);
                }
                
                train.addEdge(edge, orig, dest);
            }
            else if(testGraph.findEdge(orig, dest) != null)
            {
                if(!this.test.containsVertex(orig))
                {
                    test.addVertex(orig);
                }
                
                if(!this.test.containsVertex(dest))
                {
                    test.addVertex(dest);
                }
                
                test.addEdge(edge, orig, dest);
            }
        }
        
        List<String> vertices = new ArrayList<>(test.getVertices());
        for(String vertex : vertices)
        {
            if(!train.containsVertex(vertex))
            {
                test.removeVertex(vertex);
            }
        }
        DirectedGraphWriter dgw = new DirectedGraphWriter();
        dgw.writeGraph(train, trainFile);
        dgw.writeGraph(test, testFile);
    }
    
}
