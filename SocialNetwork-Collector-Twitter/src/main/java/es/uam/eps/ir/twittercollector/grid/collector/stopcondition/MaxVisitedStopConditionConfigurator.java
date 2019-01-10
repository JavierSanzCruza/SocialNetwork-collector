/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.stopcondition;

import es.uam.eps.ir.twittercollector.stopcondition.MaxVisitedStopCondition;
import es.uam.eps.ir.twittercollector.stopcondition.StopCondition;

/**
 * Configurator for a num. visited stop condition.
 * @author Javier Sanz-Cruzado Puig
 * @see es.uam.eps.ir.twittercollector.stopcondition.NumVisitedStopCondition
 */
public class MaxVisitedStopConditionConfigurator implements StopConditionConfigurator
{
    /**
     * Identifier for the number of users to visit.
     */
    private final static String COUNT = "count";
    
    @Override
    public StopCondition configure(StopConditionParamReader params) 
    {
        int count = params.getParams().getIntegerValue(COUNT);
        return new MaxVisitedStopCondition(count);
    }
    
}
