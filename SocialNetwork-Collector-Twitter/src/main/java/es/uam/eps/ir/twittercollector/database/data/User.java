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
import java.util.Date;

/**
 * Class that represents a Twitter user in the database.
 * @author Javier Sanz-Cruzado Puig
 */
public class User implements Serializable 
{
    private static final long serialVersionUID = 1L;

    /**
     * User identifier.
     */
    private Long userId;
    /**
     * Full name of the user.
     */
    private String name;
    /**
     * Nickname (or screen name) of the user (the chain that follows the @)
     */
    private String screenName;
    /**
     * Public description of the user.
     */
    private String description;
    /**
     * Public location of the user.
     */
    private String location;
    /**
     * Creation time of the user account in Twitter
     */
    private Date created;
    /**
     * Indiates if the user is verified (famous) or not.
     */
    private Short verified;
    /**
     * The number of people that follows this user at the retrieval time.
     */
    private Integer numFollowers;
    /**
     * The number of people followed by this user at the retrieval time.
     */
    private Integer numFriends;
    /**
     * The number of lists this user appears in.
     */
    private Integer numListed;
    /**
     * The total number of tweets published at retrieval time.
     */
    private Integer numTweets;
    /**
     * True if the user has been visited, false if it has not.
     */
    private Boolean visited;
    /**
     * Retrieval level
     */
    private Integer level;
    private Boolean wantedToVisit;

    /**
     * Basic constructor
     */
    public User() 
    {
        this.visited = false;
        this.wantedToVisit = false;
    }

    /**
     * Constructor
     * @param userId user identifier.
     */
    public User(Long userId) 
    {
        this.userId = userId;
        this.visited = false;
        this.wantedToVisit = false;
    }

    /**
     * Constructor.
     * @param userId user identifier.
     * @param name complete name
     * @param screenName nickname
     * @param created date of creation
     */
    public User(Long userId, String name, String screenName, Date created) 
    {
        this.userId = userId;
        this.name = name;
        this.screenName = screenName;
        this.created = created;
        this.visited = false;
        this.wantedToVisit = false;
    }

    /**
     * Obtains the identifier of the user
     * @return the identifier of the user
     */
    public Long getUserId() 
    {
        return userId;
    }

    /**
     * Sets a new identifier for the user
     * @param userId the new identifier
     */
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    /**
     * Obtains the complete name for the user.
     * @return the complete name for the user.
     */
    public String getName() 
    {
        return name;
    }

    /**
     * Sets a new complete name for the user
     * @param name the new complete name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtains the screen name for the user
     * @return the screen name for the user
     */
    public String getScreenName() {
        return screenName;
    }

    /**
     * Sets a new screen name for the user
     * @param screenName the new screen name for the user
     */
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    /**
     * Gets the public description of the user
     * @return the public description of the user
     */
    public String getDescription() 
    {
        return description;
    }

    /**
     * Sets a public description for the user.
     * @param description the public description for the user
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the location of the user
     * @return the location of the user
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets a new location for the user
     * @param location the new location for the user
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the creation date of the user
     * @return the creation date of the user
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Sets a new creation date for the user
     * @param created the new creation date
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * Checks if the user is verified
     * @return 1 if it is verified, 0 if it is not.
     */
    public Short getVerified() {
        return verified;
    }

    /**
     * Sets the verified value of an user
     * @param verified 1 if it is verified, 0 if it is not.
     */
    public void setVerified(Short verified) {
        this.verified = verified;
    }

    /**
     * Gets the number of followers of the user
     * @return the number of followers of the user
     */
    public Integer getNumFollowers() {
        return numFollowers;
    }

    /**
     * Sets the number of followers of the user
     * @param numFollowers the number of followers
     */
    public void setNumFollowers(Integer numFollowers) {
        this.numFollowers = numFollowers;
    }

    /**
     * Gets the number of followees of the user.
     * @return the number of followees of the user
     */
    public Integer getNumFriends() {
        return numFriends;
    }

    /**
     * Sets the number of followees of the user.
     * @param numFriends the number of followees of the user.
     */
    public void setNumFriends(Integer numFriends) {
        this.numFriends = numFriends;
    }

    /**
     * Gets the number of lists this user is included in
     * @return the number of lists this user is included in
     */
    public Integer getNumListed() {
        return numListed;
    }

    /**
     * Sets the number of lists this user is included in
     * @param numListed the number of lists this number is included in
     */
    public void setNumListed(Integer numListed) {
        this.numListed = numListed;
    }
    
    /**
     * Gets the number of published tweets
     * @return the number of published tweets
     */
    public Integer getNumTweets() {
        return numTweets;
    }

    /**
     * Sets the number of published tweets
     * @param numTweets the number of published tweets
     */    
    public void setNumTweets(Integer numTweets) {
        this.numTweets = numTweets;
    }

    /**
     * Checks if the user is going to be visited or not.
     * @return true if the user has been visited, false if it is not.
     */
    public Boolean isVisited() 
    {
        return visited;
    }
    
    public Boolean isWantedToVisit()
    {
        return wantedToVisit;
    }
    
    public void setWantedToVisit(Boolean wantedToVisit)
    {
        this.wantedToVisit = wantedToVisit;
    }

     /**
     * Sets the value for checking if the user has been visited-
     * @param visited 0 if the user has not been visited (and we do not want to), 1 if it has not been visited (but we want to)
     * and 2 if it has been visited.
     */
    public void setVisited(Boolean visited) 
    {
        this.visited = visited;
    }
    
    /**
     * Gets the retrieval level.
     * @return the retrieval level.
     */
    public Integer getLevel()
    {
        return this.level;
    }

    /**
     * Sets the retrieval level.
     * @param level the retrieval level.
     */
    public void setLevel(Integer level)
    {
        this.level = level;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.uam.eps.ir.twittercollector.database.User[ userId=" + userId + " ]";
    }
    
}
