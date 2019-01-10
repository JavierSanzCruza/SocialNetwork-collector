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
import es.uam.eps.ir.twittercollector.database.dao.data.TwitterKeyDAO;
import es.uam.eps.ir.twittercollector.database.dao.exceptions.DAOException;
import es.uam.eps.ir.twittercollector.database.managing.TwitterKey;
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
public class TwitterKeyDAOJDBC extends TwitterDAOJDBC<TwitterKey> implements TwitterKeyDAO {

    // Constants ----------------------------------------------------------------------------------

    
    // Vars ---------------------------------------------------------------------------------------


    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an User DAO for the given DAOFactory. Package private so that it can be constructed
     * inside the DAO package only.
     * @param daoFactory The DAOFactory to construct this User DAO for.
     */
    public TwitterKeyDAOJDBC(TwitterDAOFactory daoFactory) {
        super(daoFactory);
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public TwitterKey find(Integer id) throws DAOException 
    {
        return find(SQL_FIND_BY_ID, id);
    }

    @Override
    public List<TwitterKey> list() throws DAOException 
    {
        return list(SQL_LIST_ORDER_BY_ID);
    }  

    // Helpers ------------------------------------------------------------------------------------

    /**
     * Map the current row of the given ResultSet to an User.
     * @param resultSet The ResultSet of which the current row is to be mapped to an User.
     * @return The mapped User from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    @Override
    protected TwitterKey map(ResultSet resultSet) throws SQLException {
        TwitterKey key = new TwitterKey();
        key.setKeyId(resultSet.getInt("keyId"));
        key.setConsumerSecret(resultSet.getString("consumerSecret"));
        key.setConsumerKey(resultSet.getString("consumerKey"));
        key.setAccessToken(resultSet.getString("accessToken"));
        key.setAccessTokenSecret(resultSet.getString("accessTokenSecret"));
              
        return key;
    }

    private static final String SQL_FIND_BY_ID =
        "SELECT keyId, "
            + "consumerKey, "
            + "consumerSecret, "
            + "accessToken, "
            + "accessTokenSecret "
            + "FROM TwitterKey "
            + "WHERE keyId = ?";
    
    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT keyId, "
            + "consumerKey, "
            + "consumerSecret, "
            + "accessToken, "
            + "accessTokenSecret "
            + "FROM TwitterKey "
            + "ORDER BY keyId";
    

}
