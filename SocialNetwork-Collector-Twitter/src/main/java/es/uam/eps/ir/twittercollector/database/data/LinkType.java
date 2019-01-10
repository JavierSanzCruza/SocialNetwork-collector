/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.database.data;

/**
 * Enumeration for representing the different types of edges.
 * Description:
 * <ul>
 *  <li><b>EXPANSION:</b> Link for expanding the selection</li>
 *  <li><b>COMPLETION_EXP:</b> Link for completing the graph during the expansion</li>
 *  <li><b>COMPLETION_COMPL:</b> Link for completing the graph during the completion</li>
 *  <li><b>NONE:</b> Any other link.</li>
 * </ul>
 * @author Javier Sanz-Cruzado Puig
 */
public enum LinkType 
{
    EXPANSION, COMPLETION_EXP, COMPLETION_COMPL, NONE;
    
    /**
     * A string that represents the EXPANSION value
     */
    private static final String EXPANSION_STRING = "expansion";
    /**
     * A string that represents the COMPLETION_EXP value
     */
    private static final String COMPLETION_EXP_STRING = "completionExp";
    /**
     * A string that represents the COMPLETION_COMPL value
     */
    private static final String COMPLETION_COMPL_STRING = "completionCompl";
    /**
     * A string that represents the NONE value
     */
    private static final String NONE_STRING = "none";
    
    @Override
    public String toString()
    {
        if(this.equals(EXPANSION))
        {
            return EXPANSION_STRING;
        }
        else if(this.equals(COMPLETION_EXP))
        {
            return COMPLETION_EXP_STRING;
        }
        else if(this.equals(COMPLETION_COMPL))
        {
            return COMPLETION_COMPL_STRING;
        }
        else //if(this.equals(NONE))
        {
            return NONE_STRING;
        }
    }
    
    /**
     * Given a string, finds the LinkType value.
     * @param str the string
     * @return the link type related to the string
     */
    public static LinkType fromString(String str)
    {
        LinkType t = null;
        switch(str)
        {
            case EXPANSION_STRING:
                t = LinkType.EXPANSION;
                break;
            case COMPLETION_EXP_STRING:
                t = LinkType.COMPLETION_EXP;
                break;
            case COMPLETION_COMPL_STRING:
                t = LinkType.COMPLETION_COMPL;
                break;
            case NONE_STRING:
                t = LinkType.NONE;
                break;
            default:
        }
        return t;
    }
}
