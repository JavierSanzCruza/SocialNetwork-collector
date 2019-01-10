/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.stopcondition;

import static es.uam.eps.ir.twittercollector.grid.collector.stopcondition.StopConditionIdentifiers.MAXVISITED;
import static es.uam.eps.ir.twittercollector.grid.collector.stopcondition.StopConditionIdentifiers.NUMVISITED;

import es.uam.eps.ir.twittercollector.stopcondition.StopCondition;

/**
 * Class for selecting and configuring interaction filters.
 * @author Javier Sanz-Cruzado Puig
 */
public class StopConditionSelector 
{
    /**
     * Given a set of parameters, selects and configures an interaction filter.
     * @param uepr the parameters for the filter.
     * @return the interaction filter if everything went OK, null otherwise.
     */
    public StopCondition select(StopConditionParamReader uepr)
    {
        String name = uepr.getName();
        StopConditionConfigurator conf;
        switch(name)
        {
            case NUMVISITED:
                conf = new NumVisitedStopConditionConfigurator();
                break;
            case MAXVISITED:
                conf = new MaxVisitedStopConditionConfigurator();
                break;
            default:
                return null;
        }
        
        StopCondition collector = conf.configure(uepr);
        return collector;
    }
}
