/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.database.dao.data.jdbc;

import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOFactory;
import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOJDBC;
import es.uam.eps.ir.twittercollector.database.dao.data.UsersRetweetDAO;
import es.uam.eps.ir.twittercollector.database.dao.exceptions.DAOException;
import es.uam.eps.ir.twittercollector.database.data.User;
import es.uam.eps.ir.twittercollector.database.data.UsersRetweet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This class represents a concrete JDBC implementation of the {@link UserDAO} interface.
 *
 * @author Javier
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public class UsersRetweetDAOJDBC extends TwitterDAOJDBC<UsersRetweet> implements UsersRetweetDAO {

    // Constants ----------------------------------------------------------------------------------

    private static final String SQL_FIND_BY_ID =
        "SELECT creator, retweeter, frequency FROM UsersRetweet WHERE creator = ? AND retweeter = ?";
    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT creator, retweeter, frequency FROM UsersRetweet ORDER BY creator, retweeter";
    private static final String SQL_LIST_ORDER_BY_ID_FILTERED = 
        "SELECT creator, retweeter, frequency FROM UsersRetweet WHERE retweeter = ? ORDER BY creator, retweeter";
    private static final String SQL_INSERT =
        "INSERT INTO UsersRetweet (creator, retweeter, frequency) VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE frequency = frequency + 1";
    private static final String SQL_UPDATE =
        "UPDATE UsersRetweet SET creator = ?, retweeter = ?, frequency = ? WHERE creator = ? AND retweeter = ?";
    private static final String SQL_DELETE =
        "DELETE FROM UsersRetweet WHERE creator = ? AND retweeter = ?";

    // Vars ---------------------------------------------------------------------------------------


    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an User DAO for the given DAOFactory. Package private so that it can be constructed
     * inside the DAO package only.
     * @param daoFactory The DAOFactory to construct this User DAO for.
     */
    public UsersRetweetDAOJDBC(TwitterDAOFactory daoFactory) 
    {
        super(daoFactory);
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public UsersRetweet find(User creator, User retweeter) throws DAOException 
    {
        return find(SQL_FIND_BY_ID, creator, retweeter);
    }
    
    

    @Override
    public List<UsersRetweet> list() throws DAOException {
        return list(SQL_LIST_ORDER_BY_ID);
    }
    
    @Override
    public List<UsersRetweet> mentioned(Long retweeter) 
    {
        return list(SQL_LIST_ORDER_BY_ID_FILTERED, retweeter);
    }

    @Override
    public void create(User creator, User retweeter) throws IllegalArgumentException, DAOException 
    {
        if(creator == null || retweeter == null)
            throw new IllegalArgumentException("users cannot be null");
        
        Object[] values = 
        {
            creator.getUserId(),
            retweeter.getUserId(),
        };

        create(SQL_INSERT, false, true, values);
    }

    @Override
    public void update(UsersRetweet user) throws DAOException 
    {
        if (user == null || user.getCreator() == null || user.getRetweeter() == null || user.getFrequency() <= 0) {
            throw new IllegalArgumentException("User is not created yet, the users ID are null or the frequency is invalid.");
        }

        Object[] values = {
            user.getCreator(),
            user.getRetweeter(),
            user.getFrequency(),
            user.getCreator(),
            user.getRetweeter(),
        };

        update(SQL_UPDATE, true, values);
    }

    @Override
    public void delete(UsersRetweet user) throws DAOException 
    {
        if (user == null) 
        {
            throw new IllegalArgumentException("User cannot be null.");
        }
        Object[] values = 
        { 
            user.getCreator(),
            user.getRetweeter()
        };

        delete(SQL_DELETE, true, values);
        user.setCreator(null);
        user.setRetweeter(null);
    }

   
    // Helpers ------------------------------------------------------------------------------------

    /**
     * Map the current row of the given ResultSet to an UsersRetweet.
     * @param resultSet The ResultSet of which the current row is to be mapped to an User.
     * @return The mapped UsersRetweet from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    @Override
    protected UsersRetweet map(ResultSet resultSet) throws SQLException 
    {
        UsersRetweet user = new UsersRetweet();
        user.setCreator(resultSet.getLong("creator"));
        user.setRetweeter(resultSet.getLong("retweeter"));
        user.setFrequency(resultSet.getInt("frequency"));
        return user;
    }

    
}
