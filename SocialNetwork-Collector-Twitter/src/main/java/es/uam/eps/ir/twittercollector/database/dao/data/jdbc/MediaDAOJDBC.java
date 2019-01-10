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
import es.uam.eps.ir.twittercollector.database.dao.data.MediaDAO;
import es.uam.eps.ir.twittercollector.database.data.Media;
import es.uam.eps.ir.twittercollector.database.dao.exceptions.DAOException;
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
public class MediaDAOJDBC extends TwitterDAOJDBC<Media> implements MediaDAO 
{

    // Constants ----------------------------------------------------------------------------------


   

    // Vars ---------------------------------------------------------------------------------------

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an User DAO for the given DAOFactory. Package private so that it can be constructed
     * inside the DAO package only.
     * @param daoFactory The DAOFactory to construct this User DAO for.
     */
    public MediaDAOJDBC(TwitterDAOFactory daoFactory) 
    {
        super(daoFactory);
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public Media find(Long id) throws DAOException 
    {
        return find(SQL_FIND_BY_ID, id);
    }

    @Override
    public Media find(String text) throws DAOException 
    {
        return find(SQL_FIND_BY_TEXT, text); 
    }
 

    @Override
    public List<Media> list() throws DAOException 
    {
        return list(SQL_LIST_ORDER_BY_ID);
        
    }

    @Override
    public void create(Media media) throws IllegalArgumentException, DAOException {
        Object[] values = {
            media.getMediaId(),
            media.getType(),
            media.getMediaUrl(),
            media.getUrl(),
            media.getExtendedUrl(),
            media.getDisplayUrl()
        };

        create(SQL_INSERT, false, true, values);
        
    }

    @Override
    public void update(Media media) throws DAOException {
        if (media == null) {
            throw new IllegalArgumentException("Media cannot be null.");
        }

        Object[] values = {
            media.getMediaId(),
            media.getType(),
            media.getMediaUrl(),
            media.getUrl(),
            media.getExtendedUrl(),
            media.getDisplayUrl()
        };

        update(SQL_UPDATE, true, values);
    }

    @Override
    public void delete(Media media) throws DAOException {
        if (media == null) 
        {
            throw new IllegalArgumentException("Media cannot be null.");
        }
        
        delete(SQL_UPDATE, true, media.getMediaId());
    }

    // Helpers ------------------------------------------------------------------------------------

    /**
     * Map the current row of the given ResultSet to an User.
     * @param resultSet The ResultSet of which the current row is to be mapped to an User.
     * @return The mapped User from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    @Override
    protected Media map(ResultSet resultSet) throws SQLException {
        Media media = new Media();
        media.setMediaId(resultSet.getLong("mediaId"));
        media.setUrl(resultSet.getString("url"));
        media.setDisplayUrl(resultSet.getString("displayURL"));
        media.setExtendedUrl(resultSet.getString("expandedURL"));
        media.setMediaUrl(resultSet.getString("mediaURL"));
        
        return media;
    }


    private static final String SQL_FIND_BY_ID =
        "SELECT mediaId, "
            + "type, "
            + "mediaUrl, "
            + "url, "
            + "expandedUrl, "
            + "displayUrl "
            + "FROM Media "
            + "WHERE mediaId = ?";
    
    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT mediaId, "
            + "type, "
            + "mediaUrl, "
            + "url, "
            + "expandedUrl, "
            + "displayUrl "
            + "FROM Media "
            + "ORDER BY mediaId = ?";
    
    private static final String SQL_FIND_BY_TEXT = 
        "SELECT mediaId, "
            + "type, "
            + "mediaUrl, "
            + "url, "
            + "expandedUrl, "
            + "displayUrl "
            + "FROM Media "
            + "WHERE url = ?";
    
    private static final String SQL_INSERT =
        "INSERT INTO Media (mediaId, "
            + "type, "
            + "mediaUrl, "
            + "url, "
            + "expandedUrl, "
            + "displayUrl) "
            + "VALUES (?,?,?,?,?,?)";
    
    private static final String SQL_UPDATE =
        "UPDATE Media "
            + "SET mediaId = ?, "
            + "type = ?, "
            + "mediaUrl = ?, "
            + "url = ?, "
            + "expandedUrl = ?, "
            + "displayUrl = ? "
            + "WHERE mediaId = ?";
    
    private static final String SQL_DELETE =
        "DELETE FROM Media "
            + "WHERE mediaId = ?";
    
    private static final String SQL_EXIST_MEDIA =
        "SELECT mediaId "
            + "FROM Media "
            + "WHERE mediaId  = ?";
}
