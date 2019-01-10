/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.manager;

/**
 * Types of sampling by Tumblr Crawler.
 * @author Javier
 */
public enum TumblrProcessing 
{
    TEMPORAL, NUMPOSTS, MIXED, ERROR;

    /**
     * String that represents temporal processing.
     */
    private static final String TEMPORAL_STRING = "temporal";
    /**
     * String that represents a maximum number of posts processing.
     */
    private static final String NUMPOSTS_STRING = "numPosts";
    /**
     * String that represents a mixed processing between the previous two.
     */
    private static final String MIXED_STRING = "mixed";
    
    /**
     * Creates TumblrProcessing object from a String
     * @param processingField String that identificates a processing type.
     * @return The TumblrProcessing object.
     */
    public static TumblrProcessing fromString(String processingField) 
    {
        if(processingField.equalsIgnoreCase(TEMPORAL_STRING))
            return TEMPORAL;
        else if(processingField.equalsIgnoreCase(NUMPOSTS_STRING))
            return NUMPOSTS;
        else if(processingField.equalsIgnoreCase(MIXED_STRING))
            return MIXED;
        else
            return ERROR;
    }
}
