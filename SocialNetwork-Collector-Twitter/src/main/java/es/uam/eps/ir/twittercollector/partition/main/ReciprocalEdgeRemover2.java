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
import es.uam.eps.ir.graph.directed.DirectedGraphReader;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class ReciprocalEdgeRemover2
{
    public static void main(String[] args)
    {
        if(args.length < 4)
        {
            System.out.println("ERROR. Usage: <trainFile> <testFile> <outputFile> <checkFile>");
            return;
        }
        
        DirectedGraphReader dgr = new DirectedGraphReader();
        DirectedGraph<String,Integer> train = dgr.readDirectedGraph(args[0], "\t");
               
        try(    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[1])));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[2])));
                BufferedWriter cw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[3]))) )
        {
            String text;
            while((text = br.readLine())!= null)
            {
                String[] splitted = text.split("\t");
                String g1 = splitted[0];
                String g2 = splitted[1];
                if(train == null || g1 == null || g2 == null)
                    return;
                if(train.containsVertex(g1) && train.containsVertex(g2))
                {
                    if(!train.isPredecessor(g1,g2))
                    {
                        bw.write(text + "\n");
                    }
                    else
                    {
                        cw.write(text + "\n");
                    }
                }
            }
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(ReciprocalEdgeTestRemover.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(ReciprocalEdgeTestRemover.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
