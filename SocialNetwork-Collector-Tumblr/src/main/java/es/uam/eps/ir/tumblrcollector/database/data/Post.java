/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.database.data;

import java.util.Date;

/**
 *
 * @author Javier
 */
public class Post 
{
    private Long postId;
    private Long blogId;
    private Integer numNotes;
    private Date timestamp;
    private TumblrType type;
    private String status;
    private Boolean isReblog;
    private Boolean liked;
    private String url;
    private Long reblogs;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public Integer getNumNotes() {
        return numNotes;
    }

    public void setNumNotes(Integer numNotes) {
        this.numNotes = numNotes;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public TumblrType getType() {
        return type;
    }

    public void setType(TumblrType type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsReblog() {
        return isReblog;
    }

    public void setIsReblog(Boolean isReblog) {
        this.isReblog = isReblog;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getReblogs() {
        return reblogs;
    }

    public void setReblogs(Long reblogs) {
        this.reblogs = reblogs;
    }
    
    
}
