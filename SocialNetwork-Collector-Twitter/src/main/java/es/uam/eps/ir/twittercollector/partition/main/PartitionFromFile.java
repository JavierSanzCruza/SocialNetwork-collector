/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.partition.main;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import es.uam.eps.ir.graph.directed.DirectedGraphWriter;
import java.io.*;

/**
 *
 * @author Javier
 */
public class PartitionFromFile 
{
    /**
     * Obtains a partition graph from a given file
     * @param args <ul>
     * <li>The file that contains the timeline of interactions</li>
     * <li>The timestamp to cut</li>
     * <li>The training output file</li>
     * <li>The test output file</li>
     * </ul>
     */
    public static void main(String args[])
    {
        if(args.length < 4)
        {
            System.err.println("Invalid arguments. Usage: <timelineGraph> <Timestamp cut> <training file> <test file> <MaxTimestamp>");
            return;
        }
        
        String timeLineGraph = args[0];
        Long timestampCut = new Long(args[1]);
        Long maxTimestamp = new Long(args[4]);
        String trainFile = args[2];
        String testFile = args[3];
       
        DirectedGraph<String, Integer> trainGraph = new DirectedSparseGraph<>();
        DirectedGraph<String, Integer> testGraph = new DirectedSparseGraph<>();

        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(timeLineGraph))))
        {
            String line = null;
            while((line = br.readLine()) != null)
            {
                String[] split = line.split("\t");
                if(!trainGraph.containsVertex(split[0]))
                {
                    trainGraph.addVertex(split[0]);
                    testGraph.addVertex(split[0]);
                }
                if(!trainGraph.containsVertex(split[1]))
                {
                    trainGraph.addVertex(split[1]);
                    testGraph.addVertex(split[1]);
                }
                
                Long time = new Long(split[2]);
                if(time < timestampCut)
                {
                    if(trainGraph.findEdge(split[0], split[1]) == null)
                        trainGraph.addEdge(trainGraph.getEdgeCount(), split[0], split[1]);
                }
                else if(time < maxTimestamp)
                {
                    if(trainGraph.findEdge(split[0], split[1]) == null && testGraph.findEdge(split[0], split[1]) == null)
                        testGraph.addEdge(testGraph.getEdgeCount(), split[0], split[1]);
                }
            }
            
            DirectedGraphWriter dgw = new DirectedGraphWriter();
            dgw.writeGraph(trainGraph, trainFile);
            dgw.writeGraph(testGraph, testFile);
        }
        catch(IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }
    }
}
