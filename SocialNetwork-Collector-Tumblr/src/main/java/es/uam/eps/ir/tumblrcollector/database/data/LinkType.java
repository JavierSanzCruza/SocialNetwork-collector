/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.database.data;

/**
 *
 * @author Javier
 */
public enum LinkType 
{
    EXPANSION, COMPLETION_EXP, COMPLETION_COMPL, NONE;
    
    private static final String EXPANSION_STRING = "expansion";
    private static final String COMPLETION_EXP_STRING = "completionExp";
    private static final String COMPLETION_COMPL_STRING = "completionCompl";
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
