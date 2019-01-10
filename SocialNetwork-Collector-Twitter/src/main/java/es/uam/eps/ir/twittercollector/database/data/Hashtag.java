/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.database.data;

import java.io.Serializable;


/**
 *
 * @author nets
 */

public class Hashtag implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long hashtagId;
    private String text;

    public Hashtag() {
    }

    public Hashtag(Long hashtagId) {
        this.hashtagId = hashtagId;
    }

    public Hashtag(Long hashtagId, String text) {
        this.hashtagId = hashtagId;
        this.text = text;
    }

    public Long getHashtagId() {
        return hashtagId;
    }

    public void setHashtagId(Long hashtagId) {
        this.hashtagId = hashtagId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (hashtagId != null ? hashtagId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Hashtag)) {
            return false;
        }
        Hashtag other = (Hashtag) object;
        if ((this.hashtagId == null && other.hashtagId != null) || (this.hashtagId != null && !this.hashtagId.equals(other.hashtagId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.uam.eps.ir.twittercollector.database.Hashtag[ hashtagId=" + hashtagId + " ]";
    }
    
}
