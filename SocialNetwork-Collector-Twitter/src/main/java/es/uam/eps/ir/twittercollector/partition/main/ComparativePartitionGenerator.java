/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.partition.main;

import es.uam.eps.ir.twittercollector.partition.ComparativePartition;
import es.uam.eps.ir.twittercollector.partition.Partition;

/**
 *
 * @author Javier
 */
public class ComparativePartitionGenerator 
{
    
    public static void main(String args[])
    {
        Partition partition = new ComparativePartition(args[0],args[1],args[2]);
        partition.doPartition(args[3], args[4]);
    }
}
