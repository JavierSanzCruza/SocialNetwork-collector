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
public class VideoPost extends Post
{
    private String caption;
    
    public VideoPost()
    {
        this.setType(TumblrType.VIDEO_POST);
    }
    
    public String getCaption()
    {
        return caption;
    }
    
    public void setCaption(String caption)
    {
        this.caption = caption;
    }
}
