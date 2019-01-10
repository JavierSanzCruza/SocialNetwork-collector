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
import es.uam.eps.ir.twittercollector.database.data.Url;
import es.uam.eps.ir.twittercollector.database.managing.TwitterKey;
import java.util.List;

/**
 * Interface for accessing Twitter Key objects in the database
 * @author Javier Sanz-Cruzado
 */
public interface TwitterKeyDAO 
{
    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the TwitterKey from the database matching the given ID, otherwise null.
     * @param id The ID of the user to be returned.
     * @return The Twitter API Key from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public TwitterKey find(Integer id) throws DAOException;
/**
     * Returns a list of all Twitter Keys from the database ordered by user ID. The list is never null and
     * is empty when the database does not contain any user.
     * @return A list of all Twitter Keys from the database ordered by user ID.
     * @throws DAOException If something fails at database level.
     */
    public List<TwitterKey> list() throws DAOException;
}
