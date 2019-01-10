/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.partition.main;

import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import es.uam.eps.ir.twittercollector.partition.Partition;
import java.text.ParseException;

/**
 * Generates partitions from a database.
 * @author Javier Sanz-Cruzado
 */
public class PartitionGenerator 
{
    /**
     * Generates partitions from a database.
     * @param args configuration file
     */
    public static void main(String args[]) throws TwitterCollectorException, ParseException
    {
        if(args.length < 1)
        {
            System.err.println("Invalid arguments");
            System.err.println("\tARGUMENT 1: Configuration file");
        }
        
        System.out.println("STARTING PARTITION");
        PartitionConfiguration pc = new PartitionConfiguration();
        pc.configure(args[0]);
        
        Partition partition = pc.getPartition();
        partition.doPartition(pc.getTrainFile(), pc.getTestFile());
    }
}
