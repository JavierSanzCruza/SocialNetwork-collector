/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.interactionsfilter;

import es.uam.eps.ir.twittercollector.database.data.Interaction;
import es.uam.eps.ir.twittercollector.database.data.LinkType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The set of filtered interactions
 * @author Javier Sanz-Cruzado Puig
 */
public class FilteredInteractions 
{
    /**
     * A map containing the classification for the different interactions.
     */
    private final Map<LinkType, Set<Interaction>> interactions;
    
    /**
     * Constructor.
     */
    public FilteredInteractions()
    {
        interactions = new HashMap<>();
    }
    
    /**
     * 
     * @param type interaction type
     * @param inter set of interactions
     * @return true if the interaction set was not stored, false if it was or something failed
     */
    public boolean add(LinkType type, Set<Interaction> inter)
    {
        if(inter == null || interactions.containsKey(type))
        {
            return false;
        }
        
        interactions.put(type, inter);
        return true;
    }
    
    /**
     * Obtains the interaction set.
     * @param type the type of the interactions
     * @return the set of interactions of the given type.
     */
    public Set<Interaction> get(LinkType type)
    {
        if(!this.interactions.containsKey(type)) return new HashSet<>();
        return this.interactions.get(type);
    }
}
