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
 * Class that represents the relation between users that have retweeted between them
 * @author Javier Sanz-Cruzado
 */
public class UsersRetweet 
{
    /**
     * Original creator of the tweet id.
    */
    public Long creator;
    /**
     * Retweeter id.
     */
    public Long retweeter;
    /**
     * Number of times the retweeter has retweeted the creator.
     */
    public int frequency;

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Long getRetweeter() {
        return retweeter;
    }

    public void setRetweeter(Long retweeter) {
        this.retweeter = retweeter;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    
    
}
