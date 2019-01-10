/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.database.dao.data;

import es.uam.eps.ir.twittercollector.database.dao.exceptions.DAOException;
import es.uam.eps.ir.twittercollector.database.data.Hashtag;
import es.uam.eps.ir.twittercollector.database.data.Media;
import es.uam.eps.ir.twittercollector.database.data.Retweet;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import es.uam.eps.ir.twittercollector.database.data.Url;
import es.uam.eps.ir.twittercollector.database.data.User;
import es.uam.eps.ir.utils.Par;
import java.sql.ResultSet;
import java.util.List;



/**
 * This interface represents a contract for a DAO for the {@link Tweet} model.
 *
 * @author JavierSanzCruza
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public interface TweetDAO 
{

    // Actions ------------------------------------------------------------------------------------

    //-------------------------------------------------------ONLY TWEET----------------------------
    /**
     * Returns the tweet from the database matching the given ID, otherwise null.
     * @param id The ID of the tweet to be returned.
     * @return The tweet from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public Tweet find(Long id) throws DAOException;

    /**
     * Returns a list of all tweets from the database ordered by tweet ID. The list is never null and
     * is empty when the database does not contain any tweet.
     * @return A list of all users from the database ordered by tweet ID.
     * @throws DAOException If something fails at database level.
     */
    public List<Tweet> list() throws DAOException;

    /**
     * Returns a list of tweets created by a certain user.
     * @param user The creator of the tweets.
     * @return The list of tweets.
     * @throws DAOException If something fails at database level.
     */
    public List<Tweet> listUser(User user) throws DAOException;
    
    /**
     * Create the given tweet in the database. After creating, the DAO will set the obtained ID in the given user.
     * @param tweet The tweet to be created in the database.
     * @throws IllegalArgumentException If the user ID is not null.
     * @throws DAOException If something fails at database level.
     */
    public void create(Tweet tweet) throws IllegalArgumentException, DAOException;

    /**
     * Update the given user in the database. The user ID must not be null, otherwise it will throw
     * IllegalArgumentException. Note: the password will NOT be updated. Use changePassword() instead.
     * @param tweet The tweet to be created in the database.
     * @throws IllegalArgumentException If the user ID is null.
     * @throws DAOException If something fails at database level.
     */
    public void update(Tweet tweet) throws IllegalArgumentException, DAOException;

    /**
     * Delete the given tweet from the database. After deleting, the DAO will set the ID of the given
     * tweet to null.
     * @param tweet The tweet to be deleted in the database.
     * @throws DAOException If something fails at database level.
     */
    public void delete(Tweet tweet) throws DAOException;
    
    /**
     * Checks if a retweet already exists in the database
     * @param original Original tweet
     * @param retweet Retweeted tweet
     * @return True if it already exists, False if not
     */
    public boolean existsRetweet(Tweet original, Tweet retweet);

    /**
     * Creates a retweet relation in the database.
     * @param original Original tweet
     * @param Retweet Retweeted tweet
     */
    public void createRetweet(Tweet original, Tweet Retweet);
    
    /**
     * Gets a list of retweets.
     * @return A list of retweets ordered by the identifier of the original tweet.
     */
    public List<Retweet> getRetweetList();

    /**
     * Checks if a hashtag is already assigned to a tweet
     * @param tweet Tweet
     * @param hashtag Hashtag to check if it is associated
     * @return True if the hashtag is associated, false if not
     */
    public boolean isAssociatedHashtag(Tweet tweet, Hashtag hashtag);

    /**
     * Associates a hashtag to a tweet
     * @param tweet Tweet
     * @param hashtag Hashtag to associate
     */
    public void associateHashtag(Tweet tweet, Hashtag hashtag);
      
    /**
     * Associates a url to a tweet
     * @param tweet Tweet
     * @param url Url to associate
     */
    public void associateUrl(Tweet tweet, Url url);

    /**
     * Associates a media to a tweet
     * @param tweet Tweet
     * @param media Media to associate
     */
    public void associateMedia(Tweet tweet, Media media);

    /**
     * Associates an original reply to a tweet
     * @param originalTweet the original tweet
     * @param reply the reply
     */
    public void associateReply(Tweet originalTweet, Tweet reply);
    /**
     * Associates a mention to a tweet
     * @param tweet Tweet
     * @param us Mentioned user
     */
    public void associateMention(Tweet tweet, User us);
    
    /**
     * Investigates if a tweet is a retweet or not.
     * @param tweet Tweet to check
     * @return The original tweet if exists, null if not
     */
    public Tweet isRetweet(Tweet tweet);

    /**
     * Investigates if a tweet has mentions or not.
     * @param tweet Tweet to check
     * @return A list with the mentioned users. If it has no mentions, the list is empty
     */
    public List<Long> isMention(Tweet tweet); 

    /**
     * Returns the number of tweets in the database
     * @return The number of tweets in the database
     */
    public Integer countTweets();
    
    /**
     * Obtains the tweets in a given interval of rows
     * @param offset Row of the first tweet
     * @param count Number of tweets
     * @return The list of tweets
     */
    public List<Tweet> getIntervalTweets(int offset, int count);

    /**
     * Obtains a result set containing the identifier of the tweet, the timestamp, and the text.
     * @return the result set
     */
    public ResultSet listUNED();
    
    /**
     * Obtains a result set containing the identifier of the tweet, the timestamp, and the text.
     * @return the result set
     */
    public ResultSet listDataset();
    
    public ResultSet listDatasetHashtags();
    
    public ResultSet listDatasetUrls();
    public ResultSet listTweetInfo(Long tweetId);
    public ResultSet listDatasetMentions();
    public ResultSet listDatasetRetweets();

    public List<Par<Long,Long>> listRetweetsTimes(Long tweetPresent, Long tweetFuture);

    public List<Tweet> listNotRetweets(Long tweetId);
    public ResultSet listDatasetSimulation();

    public ResultSet listDatasetHashtagsSimulation();
}