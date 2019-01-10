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
import es.uam.eps.ir.twittercollector.database.data.Interaction;
import es.uam.eps.ir.twittercollector.database.data.InteractionType;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import es.uam.eps.ir.twittercollector.database.data.User;
import java.sql.ResultSet;
import java.util.List;



/**
 * This interface represents a contract for a DAO for the {@link User} model.
 * Note that all methods which returns the {@link User} from the DB, will not
 * fill the model with the password, due to security reasons.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public interface UserInteractionDAO 
{

    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the user from the database matching the given ID, otherwise null.
     * @param user
     * @param interactedUser
     * @param timestamp
     * @param it
     * @param tweet
     * @return The user from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public Interaction find(User user, User interactedUser, Long timestamp, InteractionType it, Tweet tweet) throws DAOException;

    /**
     * Returns a list of all interactions from the database ordered by timestamp. The list is never null and
     * is empty when the database does not contain any user.
     * @return A list of all interactions from the database ordered by timestamp.
     * @throws DAOException If something fails at database level.
     */
    public List<Interaction> list() throws DAOException;

    /**
     * Returns a list of Interactions made by some user, ordered by interactedUser identifier.
     * @param user User identifier.
     * @return A list of interactions made by user.
     * @throws DAOException if something fails at database level.
     */
    public List<Interaction> list(Long user) throws DAOException;
    
    /**
     * Returns a list of Interactions made by some user, with a expansion link types (Expansion and Completion_Expansion), ordered by interactedUser identifier.
     * @param user User identifier.
     * @return a list fulfilling that conditions.
     * @throws DAOException 
     */
    public List<Interaction> listExpansion(Long user) throws DAOException;
    
        
    /**
     * Create the given interaction in the database.
     * @param interaction Interaction to create.
     * @throws IllegalArgumentException if some of the fields are null.
     * @throws DAOException If something fails at database level.
     */
    public void create(Interaction interaction) throws IllegalArgumentException, DAOException;

    /**
     * Update the given interaction in the database. This function only changes the link type of the interaction
     * identified by the users identifiers and the timestamp.
     * @param interaction Interaction to update.
     * @throws IllegalArgumentException If any of the fields is null.
     * @throws DAOException If something fails at database level.
     */
    public void update(Interaction interaction) throws IllegalArgumentException, DAOException;

    /**
     * Delete the given interaction from the database.
     * @param interaction Interaction to remove.
     * @throws DAOException If something fails at database level.
     */
    public void delete(Interaction interaction) throws DAOException;
    
    public ResultSet listDataset();
}