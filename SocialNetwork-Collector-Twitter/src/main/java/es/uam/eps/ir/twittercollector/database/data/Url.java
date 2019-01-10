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

public class Url implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long urlId;
    private String url;
    private String expandedUrl;
    private String displayUrl;

    public Url() {
    }

    public Url(Long urlId) {
        this.urlId = urlId;
    }

    public Url(Long urlId, String url, String expandedUrl, String displayUrl) {
        this.urlId = urlId;
        this.url = url;
        this.expandedUrl = expandedUrl;
        this.displayUrl = displayUrl;
    }

    public Long getUrlId() {
        return urlId;
    }

    public void setUrlId(Long urlId) {
        this.urlId = urlId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExpandedUrl() {
        return expandedUrl;
    }

    public void setExpandedUrl(String expandedUrl) {
        this.expandedUrl = expandedUrl;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (urlId != null ? urlId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Url)) {
            return false;
        }
        Url other = (Url) object;
        if ((this.urlId == null && other.urlId != null) || (this.urlId != null && !this.urlId.equals(other.urlId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.uam.eps.ir.twittercollector.database.Url[ urlId=" + urlId + " ]";
    }
    
}
