/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.database.dao.data;

import es.uam.eps.ir.twittercollector.database.data.Media;
import es.uam.eps.ir.twittercollector.database.data.User;
import es.uam.eps.ir.twittercollector.database.dao.exceptions.DAOException;
import java.util.List;



/**
 * This interface represents a contract for a DAO for the {@link User} model.
 * Note that all methods which returns the {@link User} from the DB, will not
 * fill the model with the password, due to security reasons.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public interface MediaDAO 
{

    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the user from the database matching the given ID, otherwise null.
     * @param id The ID of the user to be returned.
     * @return The user from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public Media find(Long id) throws DAOException;

    /**
     * Returns the user from the database matching the given ID, otherwise null.
     * @param text The text of the user to be returned.
     * @return The user from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public Media find(String url) throws DAOException;    
    
    /**
     * Returns the user from the database matching the given email and password, otherwise null.
     * @param email The email of the user to be returned.
     * @param password The password of the user to be returned.
     * @return The user from the database matching the given email and password, otherwise null.
     * @throws DAOException If something fails at database level.
     */
   // public User find(String email, String password) throws DAOException;

    /**
     * Returns a list of all users from the database ordered by user ID. The list is never null and
     * is empty when the database does not contain any user.
     * @return A list of all users from the database ordered by user ID.
     * @throws DAOException If something fails at database level.
     */
    public List<Media> list() throws DAOException;

    /**
     * Create the given user in the database. The user ID must be null, otherwise it will throw
     * IllegalArgumentException. After creating, the DAO will set the obtained ID in the given user.
     * @param user The user to be created in the database.
     * @throws IllegalArgumentException If the user ID is not null.
     * @throws DAOException If something fails at database level.
     */
    public void create(Media media) throws IllegalArgumentException, DAOException;

    /**
     * Update the given user in the database. The user ID must not be null, otherwise it will throw
     * IllegalArgumentException. Note: the password will NOT be updated. Use changePassword() instead.
     * @param user The user to be updated in the database.
     * @throws IllegalArgumentException If the user ID is null.
     * @throws DAOException If something fails at database level.
     */
    public void update(Media media) throws IllegalArgumentException, DAOException;

    /**
     * Delete the given user from the database. After deleting, the DAO will set the ID of the given
     * user to null.
     * @param user The user to be deleted from the database.
     * @throws DAOException If something fails at database level.
     */
    public void delete(Media media) throws DAOException;
}