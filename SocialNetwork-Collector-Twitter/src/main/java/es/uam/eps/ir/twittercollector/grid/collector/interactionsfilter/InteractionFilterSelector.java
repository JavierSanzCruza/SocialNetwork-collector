/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.interactionsfilter;

import static es.uam.eps.ir.twittercollector.grid.collector.interactionsfilter.InteractionFilterIdentifiers.*;

import es.uam.eps.ir.twittercollector.interactionsfilter.InteractionFilter;

/**
 * Class for selecting and configuring interaction filters.
 * @author Javier Sanz-Cruzado Puig
 */
public class InteractionFilterSelector 
{
    /**
     * Given a set of parameters, selects and configures an interaction filter.
     * @param uepr the parameters for the filter.
     * @return the interaction filter if everything went OK, null otherwise.
     */
    public InteractionFilter select(InteractionFilterParamReader uepr)
    {
        String name = uepr.getName();
        InteractionFilterConfigurator conf;
        switch(name)
        {
            case ALL:
                conf = new AllInteractionFilterConfigurator();
                break;
            case ONLYRT:
                conf = new OnlyRetweetsInteractionFilterConfigurator();
                break;
            case ONLYRTHT:
                conf = new OnlyRetweetsWithHashtagInteractionFilterConfigurator();
                break;
            case RANDOMMAX:
                conf = new RandomMaxUsersSelectionInteractionFilterConfigurator();
                break;
            case RANDOMPROB:
                conf = new RandomProbSelectionInteractionFilterConfigurator();
                break;
            default:
                return null;
        }
        
        InteractionFilter collector = conf.configure(uepr);
        return collector;
    }
}
