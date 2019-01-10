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
 * Class that represents an interaction in Twitter.
 * @author Javier 
 */
public class Interaction 
{
    /**
     * Identifier of the user who has interacted.
     */
    private Long userId;
    /**
     * Identifier of the user who has been interacted.
     */
    private Long interactedUserId;
    /**
     * Interaction type (retweet, mention or reply).
     */
    private InteractionType interaction;
    /**
     * Link type (expansion link, completion link, or none of them).
     */
    private LinkType linkType;
    /**
     * Date of the interaction stored as a number.
     */
    private Long timestamp;
    
    /**
     * Identifier of the tweet where the interaction happened.
     */
    private Long tweetId;
    
    

    public Long getUserId() 
    {
        return userId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getInteractedUserId() 
    {  
        return interactedUserId;
    }

    public void setInteractedUserId(Long interactedUserId) 
    {
        this.interactedUserId = interactedUserId;
    }

    public InteractionType getInteraction() 
    {
        return interaction;
    }

    public void setInteraction(InteractionType interaction) 
    {
        this.interaction = interaction;
    }

    public LinkType getLinkType() 
    {
        return linkType;
    }

    public void setLinkType(LinkType linkType) 
    {
        this.linkType = linkType;
    }

    public Long getTimestamp() 
    {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) 
    {
        this.timestamp = timestamp;
    }

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }
    
    
}
