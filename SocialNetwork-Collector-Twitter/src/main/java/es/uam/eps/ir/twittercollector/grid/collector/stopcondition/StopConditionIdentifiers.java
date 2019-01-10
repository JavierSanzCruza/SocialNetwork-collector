/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.stopcondition;

/**
 * Identifiers for the tweet collectors.
 * @author Javier Sanz-Cruzado Puig
 */
public class StopConditionIdentifiers 
{
    
    public final static String NUMVISITED = "numVisited";
    public final static String MAXVISITED = "maxVisited";
    
    /**
     * Prints the list of available tweet collectors.
     * @return a string containing the list of available tweet collectors.
     */
    public String list()
    {
        String list = "Stop condition:\n";
        list += "\t" + NUMVISITED + "\n";
        list += "\t" + MAXVISITED + "\n";
        return list;
    }
}
