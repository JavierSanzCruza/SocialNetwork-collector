/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.database.dao.data.jdbc;

import static es.uam.eps.ir.database.dao.DAOUtil.prepareStatement;
import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOFactory;
import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOJDBC;
import es.uam.eps.ir.twittercollector.database.dao.data.UrlDAO;
import es.uam.eps.ir.twittercollector.database.data.Url;
import es.uam.eps.ir.twittercollector.database.dao.exceptions.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
public class UrlDAOJDBC extends TwitterDAOJDBC<Url> implements UrlDAO 
{
    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an User DAO for the given DAOFactory. Package private so that it can be constructed
     * inside the DAO package only.
     * @param daoFactory The DAOFactory to construct this User DAO for.
     */
    public UrlDAOJDBC(TwitterDAOFactory daoFactory) 
    {
        super(daoFactory);
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public Url find(Long id) throws DAOException {
        return find(SQL_FIND_BY_ID, id);
    }

    @Override
    public Url find(String text) throws DAOException {
        return find(SQL_FIND_BY_URL, text); //To change body of generated methods, choose Tools | Templates.
    }
   
    @Override
    public List<Url> list() throws DAOException {
        return list(SQL_LIST_ORDER_BY_ID);
    }

    @Override
    public void create(Url url) throws IllegalArgumentException, DAOException 
    {
        if(url == null)
            throw new IllegalArgumentException("Url cannot be null");
        
        Object[] values = {
            url.getUrl(),
            url.getExpandedUrl(),
            url.getDisplayUrl()
        };

        ResultSet generatedKeys = create(SQL_INSERT, true, true, values);
        try
        {
            url.setUrlId(generatedKeys.getLong(1));
        }
        catch(SQLException ex)
        {
            throw new DAOException(ex);
        }
        finally
        {
            if(generatedKeys != null)
            {
                try
                {
                    generatedKeys.close();
                }
                catch(SQLException ex)
                {
                    throw new DAOException(ex);
                }
            }
        }
    }

    @Override
    public void update(Url url) throws DAOException 
    {
        if (url == null) 
        {
            throw new IllegalArgumentException("Url cannot be null.");
        }

        Object[] values = 
        {
            url.getUrlId(),
            url.getUrl(),
            url.getExpandedUrl(),
            url.getDisplayUrl()
        };

        update(SQL_UPDATE, true, values);
    }

    @Override
    public void delete(Url url) throws DAOException 
    {
        if (url == null) 
        {
            throw new IllegalArgumentException("Url cannot be null.");
        }
        
        delete(SQL_DELETE, true, url.getUrlId());
        
    }
    
    @Override
    public ResultSet listDataset() 
    {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false);
            resultSet = statement.executeQuery();
        
            return resultSet;            
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
    }

    // Helpers ------------------------------------------------------------------------------------

    /**
     * Map the current row of the given ResultSet to an User.
     * @param resultSet The ResultSet of which the current row is to be mapped to an User.
     * @return The mapped User from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    @Override
    protected Url map(ResultSet resultSet) throws SQLException {
        Url url = new Url();
        url.setUrlId(resultSet.getLong("urlId"));
        url.setUrl(resultSet.getString("url"));
        url.setDisplayUrl(resultSet.getString("displayURL"));
        url.setDisplayUrl(resultSet.getString("expandedURL"));
 
        return url;
    }

    private static final String SQL_FIND_BY_ID =
        "SELECT urlId, "
            + "url, "
            + "expandedUrl, "
            + "displayUrl "
            + "FROM Url "
            + "WHERE urlId = ?";

    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT urlId, "
            + "url, "
            + "expandedUrl, "
            + "displayUrl "
            + "FROM Url "
            + "ORDER BY urlId";
    
    private static final String SQL_FIND_BY_URL = 
        "SELECT urlId, "
            + "url, "
            + "expandedUrl, "
            + "displayUrl "
            + "FROM Url "
            + "WHERE url = ?";
    
    private static final String SQL_INSERT =
        "INSERT INTO Url (url, "
            + "expandedUrl, "
            + "displayUrl) "
            + "VALUES (?,?,?)";
    
    private static final String SQL_UPDATE =
        "UPDATE Url "
            + "SET urlId = ?, "
            + "url = ?, "
            + "expandedUrl = ?, "
            + "displayUrl = ? "
            + "WHERE urlId = ?";
    
    private static final String SQL_DELETE =
        "DELETE FROM Url "
            + "WHERE urlId = ?";
    
    private static final String SQL_EXIST_URL =
        "SELECT urlId "
            + "FROM Url WHERE urlId  = ?";  
}
