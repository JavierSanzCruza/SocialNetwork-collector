/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.database.data;

import es.uam.eps.ir.utils.TextCleaner;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import twitter4j.Status;


/**
 *
 * @author nets
 */
public class Tweet implements Serializable 
{
    private static final long serialVersionUID = 1L;
    
    private Long tweetId;
    
    private Long userId;
    
    private String text;

    private Integer retweetCount;

    private Integer favoriteCount;
  
    private Date created;

    private Short truncated;

    public Tweet() {
    }

    public Tweet(Long tweetId) {
        this.tweetId = tweetId;
    }

    public Tweet(Long tweetId, long userId, String text, Date created) {
        this.tweetId = tweetId;
        this.userId = userId;
        this.text = text;
        this.created = created;
    }

    public Tweet(Status status) throws UnsupportedEncodingException
    {
        this.tweetId = status.getId();
        this.userId = status.getUser().getId();          
        this.text = status.getText();
        this.retweetCount = status.getRetweetCount();
        this.favoriteCount = status.getFavoriteCount();
        this.created = status.getCreatedAt();
        if(status.isTruncated())
            this.truncated = (short)1;
        else
            this.truncated= (short) 0;
    }
   
    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(Integer retweetCount) {
        this.retweetCount = retweetCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Short getTruncated() {
        return truncated;
    }

    public void setTruncated(Short truncated) {
        this.truncated = truncated;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tweetId != null ? tweetId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tweet)) {
            return false;
        }
        Tweet other = (Tweet) object;
        if ((this.tweetId == null && other.tweetId != null) || (this.tweetId != null && !this.tweetId.equals(other.tweetId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.uam.eps.ir.twittercollector.database.Tweet[ tweetId=" + tweetId + " ]";
    }
    
}
