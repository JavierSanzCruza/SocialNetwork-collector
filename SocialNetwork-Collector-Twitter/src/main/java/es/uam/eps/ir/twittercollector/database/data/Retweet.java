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
 * @author nets
 */
public class Retweet 
{
    private Long originalTweet;
    private Long retweet;

    public Retweet(Long originalTweet, Long retweet) {
        this.originalTweet = originalTweet;
        this.retweet = retweet;
    }
    
    public Retweet()
    {
        
    }

    public Long getOriginalTweet() {
        return originalTweet;
    }

    public void setOriginalTweet(Long originalTweet) {
        this.originalTweet = originalTweet;
    }

    public Long getRetweet() {
        return retweet;
    }

    public void setRetweet(Long retweet) {
        this.retweet = retweet;
    }
    
    
    
}
