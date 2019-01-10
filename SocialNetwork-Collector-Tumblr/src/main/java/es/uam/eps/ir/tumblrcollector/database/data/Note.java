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
public class Note 
{
    private Long originalPost;
    private Long originalBlog;
    private Long interactingBlog;
    private Long rebloggingPost;
    private String type;
    private Long timestamp;

    public Long getOriginalPost() 
    {
        return originalPost;
    }

    public void setOriginalPost(Long originalPost) 
    {
        this.originalPost = originalPost;
    }

    public Long getOriginalBlog() 
    {
        return originalBlog;
    }

    public void setOriginalBlog(Long originalBlog) 
    {
        this.originalBlog = originalBlog;
    }

    public Long getInteractingBlog() 
    {
        return interactingBlog;
    }

    public void setInteractingBlog(Long interactingBlog) 
    {
        this.interactingBlog = interactingBlog;
    }

    public Long getRebloggingPost() 
    {
        return rebloggingPost;
    }

    public void setRebloggingPost(Long rebloggingPost) 
    {
        this.rebloggingPost = rebloggingPost;
    }

    public String getType() 
    {
        return type;
    }

    public void setType(String type) 
    {
        this.type = type;
    }

    public Long getTimestamp() 
    {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) 
    {
        this.timestamp = timestamp;
    }
}
