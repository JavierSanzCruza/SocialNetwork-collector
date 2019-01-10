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
public class Blog
{
    private Long blogId;
    private String name;
    private String url;
    private String title;
    private String description;
    private Boolean isNFSW;
    private Integer numPosts;
    private Boolean shareLikes;
    private Integer numLikes;
    // Sampling fields
    private Integer level;
    private Boolean isVisited;

    public Long getBlogId() 
    {
        return blogId;
    }

    public void setBlogId(Long blogId) 
    {
        this.blogId = blogId;
    }

    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getUrl() 
    {
        return url;
    }

    public void setUrl(String url) 
    {
        this.url = url;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public Boolean getIsNFSW() 
    {
        if(isNFSW == null)
        {
            isNFSW = false;
        }
        return isNFSW;
    }

    public void setIsNFSW(Boolean isNFSW) 
    {
        this.isNFSW = isNFSW;
    }

    public Integer getNumPosts() 
    {
        return numPosts;
    }

    public void setNumPosts(Integer numPosts) 
    {
        this.numPosts = numPosts;
    }

    public Boolean getShareLikes() 
    {
        if(shareLikes == null)
        {
            shareLikes = false;
        }
        return shareLikes;
    }

    public void setShareLikes(Boolean shareLikes) 
    {
        this.shareLikes = shareLikes;
    }

    public Integer getNumLikes() 
    {
        return numLikes;
    }

    public void setNumLikes(Integer numLikes) 
    {
        this.numLikes = numLikes;
    }

    public Integer getLevel() 
    {
        return level;
    }

    public void setLevel(Integer level) 
    {
        this.level = level;
    }

    public Boolean getIsVisited() 
    {
        return isVisited;
    }

    public void setIsVisited(Boolean isVisited) 
    {
        this.isVisited = isVisited;
    }

}
