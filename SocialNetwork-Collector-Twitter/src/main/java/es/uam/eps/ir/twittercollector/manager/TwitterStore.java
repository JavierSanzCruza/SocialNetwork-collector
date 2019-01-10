/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.manager;
import es.uam.eps.ir.twittercollector.database.TwitterDB;
import es.uam.eps.ir.twittercollector.database.dao.data.HashtagDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.MediaDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.TweetDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UrlDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UserDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UserInteractionDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UsersRetweetDAO;
import es.uam.eps.ir.twittercollector.database.data.Hashtag;
import es.uam.eps.ir.twittercollector.database.data.Interaction;
import es.uam.eps.ir.twittercollector.database.data.InteractionType;
import es.uam.eps.ir.twittercollector.database.data.LinkType;
import es.uam.eps.ir.twittercollector.database.data.Media;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import es.uam.eps.ir.twittercollector.database.data.Url;
import es.uam.eps.ir.twittercollector.database.data.User;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import es.uam.eps.ir.utils.TextCleaner;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Level;
import twitter4j.*;

/**
 * Class for storing tweets in the database and the JSON Repository
 * @author Javier Sanz-Cruzado Puig
 */
public class TwitterStore 
{
    TwitterDB tdb;
    
    public TwitterStore()
    {
        try {
            this.tdb = TwitterDB.getInstance();
        } catch (TwitterCollectorException ex) {
            java.util.logging.Logger.getLogger(TwitterStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Stores in the database and the JSON Repository the contents of a Status
     * @param status Status to store
     * @param storeRetweet Indicates if the retweet must be stored if the retweeted userent does not exist in database
     */
    public void storeTweet(Status status, Boolean storeRetweet)
    {
        try {
            storeTweet(status, false, storeRetweet, false);
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(TwitterStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Stores in the database the contents of a Status
     * @param status Status to store
     * @param updateUser Indicates if the userent must be updated if exists
     * @param storeRetweet Indicates if the retweet must be stored if the retweeted userent does not exist in database
     * @param visitedUser
     * @throws java.io.UnsupportedEncodingException if something fails at cleaning the text in the status.
     */
    public void storeTweet(Status status, Boolean updateUser, Boolean storeRetweet, Boolean visitedUser) throws UnsupportedEncodingException
    {
        if(status == null)
        {
            System.err.println("ERROR: Empty Status");
            return;
        }

        // Store the userent that tweeted the status
        User tweeter = this.storeUser(status.getUser(), updateUser, visitedUser);
        
        // Store the tweet
        TweetDAO tweetdao = tdb.getTweetDAO();
        UserDAO userdao = tdb.getUserDAO();
        UsersRetweetDAO usersRTdao = tdb.getUsersRTDAO();
        
        Tweet tweet = tweetdao.find(status.getId());
        if(tweet == null) //If it does not exist in database, store the tweet
        {
            tweet = new Tweet();
            tweet.setTweetId(status.getId());
            tweet.setUserId(status.getUser().getId());          
            tweet.setText(TextCleaner.cleanText(status.getText()));
            tweet.setRetweetCount(status.getRetweetCount());
            tweet.setFavoriteCount(status.getFavoriteCount());
            tweet.setCreated(status.getCreatedAt());
            if(status.isTruncated())
                tweet.setTruncated((short) 1);
            else
                tweet.setTruncated((short) 0);
            tweetdao.create(tweet);
            
            // Store the retweet
            if(status.isRetweet())
            {
                User retweeted = userdao.find(status.getRetweetedStatus().getUser().getId());
                if(retweeted != null || storeRetweet == true)
                {
                    this.storeTweet(status.getRetweetedStatus(), true, storeRetweet, false);
                    retweeted = userdao.find(status.getRetweetedStatus().getUser().getId());
                    usersRTdao.create(retweeted, tweeter);
                    Tweet retweet = tweetdao.find(status.getRetweetedStatus().getId());
                    tweetdao.createRetweet(retweet, tweet);
                    // Storing the interaction
                    this.storeInteraction(tweeter, retweeted, InteractionType.RETWEET, tweet.getCreated(), tweet);
                }
            }
            
            // Store the hashtag
            storeHashtags(tweet, status.getHashtagEntities());
            // Store the URLS
            storeURLs(tweet, status.getURLEntities());
            // Store the Media
            storeMedia(tweet, status.getMediaEntities());
            // Store the Mentions
            if(status.isRetweet() == false)
            {   
                storeMentions(tweet, tweeter, status.getUserMentionEntities(), storeRetweet);
                
                if(status.getInReplyToUserId() > 0)
                {
                    this.storeReply(tweeter, status, storeRetweet, tweet);
                }
            }
        }
        
    }
    
    /**
     * Stores a reply in the database.
     * @param tweeter Creator of the reply.
     * @param tweet Reply tweet.
     * @param storeReply indicates if the reply must be stored if the replied user does not exist.
     * @param tw
     */
    public void storeReply(User tweeter, Status tweet, Boolean storeReply, Tweet tw)
    {
        if(tweeter == null || tweet == null)
        {
            return;
        }
        
        UserDAO userdao = tdb.getUserDAO();
        UsersRetweetDAO usersRTdao = tdb.getUsersRTDAO();
        User us = userdao.find(tweet.getInReplyToUserId());
        if(us == null && storeReply == true)
        {
            us = new User();
            us.setUserId(tweet.getInReplyToUserId());
            us.setScreenName(tweet.getInReplyToScreenName());
            userdao.create(us);
        }
        
        if(us != null)
        {
            usersRTdao.create(us, tweeter);
            long tweetId = tweet.getInReplyToStatusId();
            Tweet replied = new Tweet(tweetId);
            tdb.getTweetDAO().associateReply(replied, tw);
            // Store the interaction
            this.storeInteraction(tweeter, us, InteractionType.REPLY, tweet.getCreatedAt(), tw);
        }
    }
    
    /**
     * Stores an userent in the database
     * @param user User to store.
     * @param update Indicates if the userent must be updated if exists
     * @return The stored userent.
     */
    public User storeUser(twitter4j.User user, Boolean update)
    {
        return storeUser(user, update, false);
    }
    /**
     * Stores a userent in the database and JSON Repository.
     * @param user User User to store.
     * @param update Indicates if the userent must be updated if exists.
     * @param visited Sets the is visited param.
     * @return The stored userent
     */
    public User storeUser(twitter4j.User user, Boolean update, Boolean visited)
    {
        if(user == null)
        {
            return null;
        }
        
        UserDAO userdao = tdb.getUserDAO();
        
        User us = userdao.find(user.getId());
        Integer level = null;
        if(us != null)
            level = us.getLevel();
        
        if(us == null || update == true)
        {
            Boolean aux = false;
            if(us == null)
                update = false;
            if(us != null)
            {
                aux = us.isVisited();
            }
            us = new User();
            us.setUserId(user.getId());
            us.setScreenName(user.getScreenName());
            us.setName(user.getName());

            us.setDescription(user.getDescription());
            us.setLocation(user.getLocation());
            us.setCreated(user.getCreatedAt());
            if(user.isVerified() == true)
                us.setVerified((short) 1);
            else
                us.setVerified((short) 0);
            us.setNumFollowers(user.getFollowersCount());
            us.setNumFriends(user.getFriendsCount());
            us.setNumListed(user.getListedCount());
            us.setNumTweets(user.getStatusesCount());
            
            if(aux == false)
            {
                us.setVisited(visited);
            }
            else
            {
                us.setVisited(true);
            }
            
            if(update == false)
            {
                userdao.create(us);
            }
            else
            {
                us.setLevel(level);
                userdao.update(us);
            }
        }
        return us;
    }
    
    /**
     * Stores the hashtags in the database and associates them to the corresponding tweet
     * @param tweet Tweet to associate
     * @param hashtags HashtagEntity list
     */
    public void storeHashtags(Tweet tweet, HashtagEntity[] hashtags)
    {
        HashtagDAO hashdao = tdb.getHashtagDAO();
        TweetDAO tweetdao = tdb.getTweetDAO();
        for(HashtagEntity hashtag : hashtags)
        {
            Hashtag hasht = hashdao.find(hashtag.getText());
            if(hasht == null)
            {
                hasht = new Hashtag();
                hasht.setText(hashtag.getText());
                hashdao.create(hasht);
            }
            tweetdao.associateHashtag(tweet, hasht);
        }
    }
    
    /**
     * Stores the urls in the database and associates them to the corresponding tweet
     * @param tweet Tweet to associate
     * @param urls HashtagEntity list
     */
    public void storeURLs(Tweet tweet, URLEntity[] urls)
    {
        UrlDAO urldao = tdb.getUrlDAO();
        TweetDAO tweetdao = tdb.getTweetDAO();
        for(URLEntity urlent : urls)
        {
            Url url = urldao.find(urlent.getURL());
            if(url == null)
            {
                url = new Url();
                url.setUrl(urlent.getURL());
                url.setDisplayUrl(urlent.getDisplayURL());
                url.setExpandedUrl(urlent.getExpandedURL());
                urldao.create(url);
            }
            tweetdao.associateUrl(tweet, url);
        }
    }
    
    /**
     * Stores the urls in the database and associates them to the corresponding tweet
     * @param tweet Tweet to associate
     * @param medias MediaEntity list
     */
    public void storeMedia(Tweet tweet, MediaEntity[] medias)
    {
        MediaDAO mediadao = tdb.getMediaDAO();
        TweetDAO tweetdao = tdb.getTweetDAO();
        for(MediaEntity mediaent : medias)
        {
            Media media = mediadao.find(mediaent.getId());
            if(media == null)
            {
                media = new Media();
                media.setMediaId(mediaent.getId());
                media.setType(mediaent.getType());
                media.setUrl(mediaent.getURL());
                media.setDisplayUrl(mediaent.getDisplayURL());
                media.setExtendedUrl(mediaent.getExpandedURL());
                media.setMediaUrl(mediaent.getMediaURL());
                
                mediadao.create(media);
            }
            tweetdao.associateMedia(tweet, media);
        }
    }
    
    
    /**
     * Stores the mentions in the database
     * @param tweet Tweet to associate
     * @param tweeter Creator of the tweet.
     * @param mentioned Mentioned users
     * @param storeMention Indicates if the mention has to be inserted in the database if the user does not exist
     */
    public void storeMentions(Tweet tweet, User tweeter, UserMentionEntity[] mentioned,Boolean storeMention)
    {
        if(tweet == null)
        {
            return;
        }
        
        UserDAO userdao = tdb.getUserDAO();
        TweetDAO tweetdao = tdb.getTweetDAO();
        UsersRetweetDAO usersRTdao = tdb.getUsersRTDAO();
        
        for(UserMentionEntity userent : mentioned)
        {
            User us = userdao.find(userent.getId());
            if(us == null && storeMention == true)
            {
                us = new User();
                us.setUserId(userent.getId());
                us.setName(userent.getName());
                us.setScreenName(userent.getScreenName());
                userdao.create(us);
            }
            
            if(us != null)
            {
                usersRTdao.create(us, tweeter);
                tweetdao.associateMention(tweet, us);
                //Store the interaction
                this.storeInteraction(tweeter, us, InteractionType.MENTION, tweet.getCreated(), tweet);
            }
        }
        
    }
    
    /**
     * Stores an interaction in the database
     * @param tweeter Creator of the interaction tweet.
     * @param interacted Interacted user.
     * @param type Type of the interaction.
     * @param timestamp Time of creation of the tweet.
     */
    public void storeInteraction(User tweeter, User interacted, InteractionType type, Date timestamp, Tweet tweet)
    {
        Interaction inter = new Interaction();
        Interaction aux;
        UserInteractionDAO interDB = tdb.getUserInteractionDAO();

        inter.setUserId(tweeter.getUserId());
        inter.setInteractedUserId(interacted.getUserId());
        inter.setInteraction(type);
        inter.setLinkType(LinkType.NONE);
        inter.setTimestamp(timestamp.getTime());
        inter.setTweetId(tweet.getTweetId());
        
        aux = interDB.find(tweeter, interacted, timestamp.getTime(), type, tweet);
        if(aux == null)
        {
            interDB.create(inter);
        }
    }

    
}
