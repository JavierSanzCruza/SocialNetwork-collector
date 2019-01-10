/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.interactionsfilter;

/**
 * Identifiers of the interaction filters.
 * @author Javier Sanz-Cruzado Puig
 */
public class InteractionFilterIdentifiers 
{
    public final static String ALL = "all";
    public final static String ONLYRT = "only retweets";
    public final static String ONLYRTHT = "only retweets with hashtags";
    public final static String RANDOMMAX = "random max. user count";
    public final static String RANDOMPROB = "random probability";
    
    /**
     * Obtains a string with the identifiers of the different available interaction filters.
     * @return the string with the identifiers of the available interaction filters.
     */
    public String list()
    {
        String list = "Interaction filters:\n";
        list += "\t" + ALL + "\n";
        list += "\t" + ONLYRT + "\n";
        list += "\t" + ONLYRTHT + "\n";
        list += "\t" + RANDOMMAX + "\n";
        list += "\t" + RANDOMPROB + "\n";
        return list;
    }
}
