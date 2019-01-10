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
public class PhotoPost extends Post
{
    private Integer width;
    private Integer height;
    private String caption;
    private Boolean isPhotoset;

    public PhotoPost()
    {
        this.setType(TumblrType.PHOTO_POST);
    }
    
    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Boolean getIsPhotoset() {
        return isPhotoset;
    }

    public void setIsPhotoset(Boolean isPhotoset) {
        this.isPhotoset = isPhotoset;
    }
    
    
}
