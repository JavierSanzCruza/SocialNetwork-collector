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
 *
 * @author Javier
 */
public enum InteractionType 
{
    RETWEET, MENTION, REPLY;
    
    //toString values
    private static final String RETWEET_STRING = "retweet";
    private static final String MENTION_STRING = "mention";
    private static final String REPLY_STRING = "reply";
    
    @Override
    public String toString()
    {
        if(this.equals(RETWEET))
        {
            return RETWEET_STRING;
        }
        else if(this.equals(MENTION))
        {
            return MENTION_STRING;
        }
        else //if(this.equals(REPLY))
        {
            return REPLY_STRING;
        }
    }
    
    public static InteractionType fromString(String str)
    {
        InteractionType t = null;
        switch(str)
        {
            case RETWEET_STRING:
                t = InteractionType.RETWEET;
                break;
            case MENTION_STRING:
                t = InteractionType.MENTION;
                break;
            case REPLY_STRING:
                t = InteractionType.REPLY;
                break;
            default:
        }
        return t;
    }
}
