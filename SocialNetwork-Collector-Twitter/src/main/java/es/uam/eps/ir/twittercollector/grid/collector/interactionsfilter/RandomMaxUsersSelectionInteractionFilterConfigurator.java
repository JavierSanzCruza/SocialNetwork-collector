/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.interactionsfilter;

import es.uam.eps.ir.twittercollector.interactionsfilter.InteractionFilter;
import es.uam.eps.ir.twittercollector.interactionsfilter.RandomMaxUsersSelectionInteractionFilter;

/**
 * Configures a random max. user selection interaction filter.
 * @author Javier Sanz-Cruzado Puig
 * @see es.uam.eps.ir.twittercollector.interactionsfilter.RandomMaxUsersSelectionInteractionFilter
 */
public class RandomMaxUsersSelectionInteractionFilterConfigurator implements InteractionFilterConfigurator 
{
    /**
     * Identifier for the maximum number of users to retrieve.
     */
    private final static String MAXUSER = "maxUsers";
    
    @Override
    public InteractionFilter configure(InteractionFilterParamReader params) 
    {
        int maxUsers = params.getParams().getIntegerValue(MAXUSER);
        return new RandomMaxUsersSelectionInteractionFilter(maxUsers);
    }
    
}
