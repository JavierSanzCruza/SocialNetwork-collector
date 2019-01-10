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
 * Condition for stoping the initial phase of a collection.
 * @author Javier Sanz-Cruzado Puig
 */
public interface StopCondition 
{
    /**
     * Checks the stop condition of a collector.
     * @param numVisited the number of visited users.
     * @param remainingUsers the number of remaining users to visit.
     * @param currentLevel the current exploration level
     * @param time time from the beginning of the collection.
     * @return true if the collector has to stop, false if not.
     */
    public boolean stopInitialPhase(int numVisited, int remainingUsers, int currentLevel, long time);
}
