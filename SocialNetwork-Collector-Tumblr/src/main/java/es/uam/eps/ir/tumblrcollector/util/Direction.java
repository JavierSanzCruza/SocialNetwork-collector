/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.util;

/**
 * Enumeration that specifies the expansio direction of a crawling.
 * @author Javier
 */
public enum Direction 
{
    FORWARD, BACKWARD, BOTH, ERROR;

    /**
     * String for Forward direction (if A reblogs B, then, A expands with B)
     */
    private static final String FORWARD_STRING = "forward";
    /**
     * String for Backward direction (if A reblogs B, then, B expands with A)
     */
    private static final String BACKWARD_STRING = "backward";
    /**
     * String for mixed direction (if A reblogs B, then, A expands with B or B expands with A)
     */
    private static final String BOTH_STRING = "both";
    
    /**
     * Generates a direction with a string. Allowed values are "forward","backward" and "both"
     * @param directionField The string.
     * @return The corresponding direction, or ERROR if something went wrong.
     */
    public static Direction fromString(String directionField) 
    {
        if(directionField.equalsIgnoreCase(FORWARD_STRING))
            return FORWARD;
        else if(directionField.equalsIgnoreCase(BACKWARD_STRING))
            return BACKWARD;
        else if(directionField.equalsIgnoreCase(BOTH_STRING))
            return BOTH;
        else
            return ERROR;
        
    }
}
