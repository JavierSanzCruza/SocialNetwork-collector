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
public enum TumblrType 
{
    
   TEXT_POST, PHOTO_POST, AUDIO_POST, VIDEO_POST, LINK_POST, QUOTE_POST, ANSWER_POST, CHAT_POST;

   @Override
   public String toString()
   {
        if(this.equals(TEXT_POST))
            return "text";
        else if(this.equals(PHOTO_POST))
            return "photo";
        else if(this.equals(AUDIO_POST))
            return "audio";
        else if(this.equals(VIDEO_POST))
            return "video";
        else if(this.equals(LINK_POST))
            return "link";
        else if(this.equals(QUOTE_POST))
            return "quote";
        else if(this.equals(ANSWER_POST))
            return "answer";
        else
            return "chat";

   }
}
