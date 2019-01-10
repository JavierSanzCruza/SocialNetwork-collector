/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.database.dao.data;
import es.uam.eps.ir.twittercollector.database.data.Hashtag;
import es.uam.eps.ir.twittercollector.database.dao.exceptions.DAOException;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import java.sql.ResultSet;
import java.util.List;



/**
 * This interface represents a contract for a DAO for the {@link Hashtag} model.
 * @author BalusC
 * @author Javier Sanz-Cruzado
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public interface HashtagDAO 
{

    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the hashtag from the database matching the given ID, otherwise null.
     * @param id The ID of the hashtag to be returned.
     * @return The hashtag from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public Hashtag find(Long id) throws DAOException;

    /**
     * Returns the Hashtag from the database matching the given text, otherwise null.
     * @param text The text of the hashtag to be returned.
     * @return The user from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public Hashtag find(String text) throws DAOException;    

    /**
     * Returns a list of all hashtags from the database ordered by user ID. The list is never null and
     * is empty when the database does not contain any user.
     * @return A list of all hashtag from the database ordered by hashtag ID.
     * @throws DAOException If something fails at database level.
     */
    public List<Hashtag> list() throws DAOException;

    /**
     * Create the given hashtag in the database. After creating, the DAO will set the obtained ID in the given user.
     * @param hashtag The hashtag to be created in the database.
     * @throws IllegalArgumentException If the user ID is not null.
     * @throws DAOException If something fails at database level.
     */
    public void create(Hashtag hashtag) throws IllegalArgumentException, DAOException;

    /**
     * Update the given hashtag in the database. The hashtag ID must not be null, otherwise it will throw
     * IllegalArgumentException.
     * @param hashtag The hashtag to be updated in the database.
     * @throws IllegalArgumentException If the hashtag ID is null.
     * @throws DAOException If something fails at database level.
     */
    public void update(Hashtag hashtag) throws IllegalArgumentException, DAOException;

    /**
     * Delete the given hashtag from the database.
     * @param hashtag The hashtag to be deleted from the database.
     * @throws DAOException If something fails at database level.
     */
    public void delete(Hashtag hashtag) throws DAOException;
    
    /**
     * Gets the list of hashtags associated to a tweet.
     * @param tweet the tweet.
     * @return the list of hashtags associated to the tweet.
     * @throws DAOException If something fails at database level.
     */
    public List<Hashtag> getAssociatedHashtags(Tweet tweet) throws DAOException;

    
    public ResultSet listDataset() throws DAOException;
}