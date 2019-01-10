/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.database.dao.data.jdbc;

import es.uam.eps.ir.tumblrcollector.database.dao.TumblrDAOFactory;
import es.uam.eps.ir.tumblrcollector.database.dao.TumblrDAOJDBC;
import es.uam.eps.ir.tumblrcollector.database.dao.data.VideoPlayerDAO;
import es.uam.eps.ir.tumblrcollector.database.data.Post;
import es.uam.eps.ir.tumblrcollector.database.data.VideoPlayer;
import es.uam.eps.ir.database.dao.exceptions.DAOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Javier
 */
public class VideoPlayerDAOJDBC extends TumblrDAOJDBC<VideoPlayer> implements VideoPlayerDAO
{

    public VideoPlayerDAOJDBC(TumblrDAOFactory daoFactory) 
    {
        super(daoFactory);
    }

    @Override
    public VideoPlayer find(Long videoId) throws DAOException 
    {
        return this.find(SQL_FIND, videoId);
    }

    @Override
    public VideoPlayer find(String embed) throws DAOException 
    {
        return this.find(SQL_FIND_BY_CODE, embed);
    }

    @Override
    public List<VideoPlayer> list() throws DAOException 
    {
        return this.list(SQL_LIST);
    }

    @Override
    public List<VideoPlayer> list(Long postId) throws DAOException {
        return this.list(SQL_LIST_BY_POST, postId);
    }

    @Override
    public void create(VideoPlayer player) throws DAOException 
    {
        if(player == null)
        {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        Object[] values = {
            player.getWidth(),
            player.getEmbedCode()
        };
        
        ResultSet generatedKeys = this.create(SQL_CREATE, true, true, values);
        try 
        {
            player.setPlayerId(generatedKeys.getLong(1));
        } 
        catch (SQLException ex) 
        {
            throw new DAOException(ex);
            //throw new DAOException("Hashtag creation error. Generated keys are not created");
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
    public void update(VideoPlayer player) throws DAOException 
    {
        if(player == null)
        {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        Object[] values = {
            player.getPlayerId(),
            player.getWidth(),
            player.getEmbedCode(),
            player.getPlayerId()
        };
        
        this.update(SQL_UPDATE, true, values);
    }

    @Override
    public void delete(VideoPlayer player) throws DAOException 
    {
        if(player == null)
        {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        this.delete(SQL_DELETE, true, player.getPlayerId());

    }

    @Override
    public void associateToPost(VideoPlayer player, Post post) throws DAOException 
    {
        if(player == null || post == null)
        {
            throw new IllegalArgumentException("Player and Post cannot be null");
        }
        
        Object[] values = {
            post.getPostId(),
            player.getPlayerId()
        };
        
        this.create(SQL_ASSOCIATE, false, true, values);
    }
    
    @Override
    protected VideoPlayer map(ResultSet resultSet) throws SQLException 
    {
        VideoPlayer vp = new VideoPlayer();
        vp.setPlayerId(resultSet.getLong("playerId"));
        vp.setEmbedCode(resultSet.getString("embedCode"));
        vp.setWidth(resultSet.getInt("width"));
        return vp;
    }
    
    private static final String SQL_FIND = 
        "SELECT playerId, "
            + "width, "
            + "embedCode "
            + "FROM VideoPlayer "
            + "WHERE playerId = ?";
    private static final String SQL_FIND_BY_CODE = 
        "SELECT playerId, "
            + "width, "
            + "embedCode "
            + "FROM VideoPlayer "
            + "WHERE embedCode = ?";
    private static final String SQL_LIST = 
        "SELECT playerId, "
            + "width, "
            + "embedCode "
            + "FROM VideoPlayer";
    private static final String SQL_LIST_BY_POST = 
        "SELECT vp.playerId, "
            + "vp.width, "
            + "vp.embedCode "
            + "FROM VideoPlayer AS vp INNER JOIN PostedVideoPlayer AS pvp "
            + "ON vp.playerId = pvp.playerId "
            + "WHERE pvp.postId = ?";
    private static final String SQL_CREATE = 
        "INSERT INTO VideoPlayer ("
            + "width, "
            + "embedCode) "
            + "VALUES (?,?)";
    private static final String SQL_UPDATE = 
        "UPDATE VideoPlayer "
            + "SET playerId = ?, "
            + "width = ?, "
            + "embedCode = ? "
            + "WHERE playerId = ?";
    private static final String SQL_DELETE = 
        "DELETE FROM VideoPlayer "
            + "WHERE playerId = ?";
    private static final String SQL_ASSOCIATE = 
        "INSERT INTO PostedVideoPlayer ("
            + "postId, "
            + "playerId) "
            + "VALUES (?,?)";
            
}
