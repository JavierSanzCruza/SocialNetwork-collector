/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.stopcondition;

/**
 * Condition for stopping the crawling when a number of users have been visited.
 * @author Javier Sanz-Cruzado Puig
 */
public class NumVisitedStopCondition implements StopCondition 
{
    private final int maxVisited;
    public NumVisitedStopCondition(int maxVisited)
    {
        this.maxVisited = maxVisited;
    }
    
    @Override
    public boolean stopInitialPhase(int numVisited, int remainingUsers, int currentLevel, long time) 
    {
        return numVisited > this.maxVisited;
    }
    
    
}
