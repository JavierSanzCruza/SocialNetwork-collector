/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.graph.directed;
import edu.uci.ics.jung.graph.DirectedGraph;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Writes a graph to a file
 * @author Javier Sanz-Cruzado
 */
public class DirectedGraphWriter
{
    public void writeGraph(DirectedGraph<String,Integer> graph, String route)
    {
        BufferedWriter bw = null;
        
        try
        {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(route)));
            for(Integer edge : graph.getEdges())
            {
                bw.write(graph.getEndpoints(edge).getFirst() +"\t"+ graph.getEndpoints(edge).getSecond() + "\t1.0\r\n");
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(bw != null)
            {
                try {
                    bw.close();
                } catch (IOException ex) {
                    Logger.getLogger(DirectedGraphWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
