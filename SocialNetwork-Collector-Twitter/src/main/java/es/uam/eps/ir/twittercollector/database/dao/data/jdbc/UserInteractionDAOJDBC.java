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
import es.uam.eps.ir.twittercollector.database.dao.data.UserInteractionDAO;
import es.uam.eps.ir.twittercollector.database.dao.exceptions.DAOException;
import es.uam.eps.ir.twittercollector.database.data.Interaction;
import es.uam.eps.ir.twittercollector.database.data.InteractionType;
import es.uam.eps.ir.twittercollector.database.data.LinkType;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import es.uam.eps.ir.twittercollector.database.data.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Javier
 */
public class UserInteractionDAOJDBC extends TwitterDAOJDBC<Interaction> implements UserInteractionDAO
{

    public UserInteractionDAOJDBC(TwitterDAOFactory daoFactory) 
    {
        super(daoFactory);
    }
    

    @Override
    public Interaction find(User user, User interactedUser, Long timestamp, InteractionType it, Tweet tw) throws DAOException 
    {
        Object[] values = 
        {
            user.getUserId(),
            interactedUser.getUserId(),
            timestamp,
            it.toString(),
            tw.getTweetId()
        };   
        return this.find(SQL_FIND, values);
    } //To change body of generated methods, choose Tools | Templates.

    @Override
    public List<Interaction> list() throws DAOException 
    {
        return this.list(SQL_LIST);
    }

    @Override
    public List<Interaction> list(Long user) throws DAOException 
    {
        return this.list(SQL_LIST_BY_USER, user);
    }
    
    @Override
    public List<Interaction> listExpansion(Long user) throws DAOException
    {
        Object[] values = 
        {
            user,
        };
        
        return this.list(SQL_LIST_EXPANSION, values);
    }

    @Override
    public void create(Interaction interaction) throws IllegalArgumentException, DAOException 
    {
        if(interaction == null)
        {
            throw new IllegalArgumentException("Interaction cannot be null");
        }
        
        Object[] values =
        {
            interaction.getUserId(),
            interaction.getInteractedUserId(),
            interaction.getTimestamp(),
            interaction.getInteraction().toString(),
            interaction.getLinkType().toString(),
            interaction.getTweetId()
        };
        
        this.create(SQL_CREATE, false, true, values);
    }

    @Override
    public void update(Interaction interaction) throws IllegalArgumentException, DAOException 
    {
        if(interaction == null)
        {
            throw new IllegalArgumentException("Interaction cannot be null");
        }
        
        Object[] values = 
        {
            interaction.getLinkType().toString(),
            interaction.getUserId(),
            interaction.getInteractedUserId(),
            interaction.getTimestamp(),
            interaction.getInteraction().toString(),
            interaction.getTweetId()
        };
        
        this.update(SQL_UPDATE, true, values);
    }

    @Override
    public void delete(Interaction interaction) throws DAOException 
    {
        Object[] values = 
        {
            interaction.getUserId(),
            interaction.getInteractedUserId(),
            interaction.getInteraction().toString(),
            interaction.getTimestamp(),
            interaction.getTweetId()
        };
        
        if(interaction == null)
        {
            throw new IllegalArgumentException("Interaction cannot be null");
        }
        
        this.delete(SQL_DELETE, true, values);
    }
    
    @Override
    protected Interaction map(ResultSet resultSet) throws SQLException 
    {
        Interaction interaction = new Interaction();
        interaction.setUserId(resultSet.getLong("userId"));
        interaction.setInteractedUserId(resultSet.getLong("interactedUserId"));
        interaction.setTimestamp(resultSet.getLong("timestamp"));
        interaction.setInteraction(InteractionType.fromString(resultSet.getString("interaction")));
        interaction.setLinkType(LinkType.fromString(resultSet.getString("linkType")));
        interaction.setTweetId(resultSet.getLong("tweetId"));
        return interaction;
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
            statement = prepareStatement(connection, SQL_LIST_DATASET, false);
            resultSet = statement.executeQuery();
        
            return resultSet;            
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
    }
    
    
    private final static String SQL_LIST_DATASET = 
            "SELECT userId, "
            + "interactedUserId, "
            + "interaction, "
            + "timestamp "
            + "FROM UserInteraction "
            + "ORDER BY timestamp";
    private final static String SQL_FIND =
            "SELECT userId, "
            + "interactedUserId, "
            + "interaction, "
            + "linkType, "
            + "timestamp, "
            + "tweetId "
            + "FROM UserInteraction "
            + "WHERE userId = ? AND "
            + "interactedUserId = ? AND "
            + "timestamp = ? AND "
            + "interaction = ? AND "
            + "tweetId = ?";
    private final static String SQL_LIST = 
            "SELECT userId, "
            + "interactedUserId, "
            + "interaction, "
            + "linkType, "
            + "timestamp, "
            + "tweetId, "
            + "FROM UserInteraction "
            + "ORDER BY timestamp";
    private final static String SQL_LIST_BY_USER = 
            "SELECT userId, "
            + "interactedUserId, "
            + "interaction, "
            + "linkType, "
            + "timestamp, "
            + "tweetId "
            + "FROM UserInteraction "
            + "WHERE userId = ? "
            + "ORDER BY interactedUserId";
    private final static String SQL_LIST_EXPANSION = 
            "SELECT userId, "
            + "interactedUserId, "
            + "interaction, "
            + "linkType, "
            + "timestamp, "
            + "tweetId "
            + "FROM UserInteraction "
            + "WHERE userId = ? AND (linkType = 'expansion' OR linkType = 'completionExp') "
            + "ORDER BY interactedUserId";
    private final static String SQL_CREATE =
            "INSERT INTO UserInteraction ("
            + "userId, "
            + "interactedUserId, "
            + "timestamp, "
            + "interaction, "
            + "linkType, "
            + "tweetId ) "
            + "VALUES (?,?,?,?,?,?)";
    private final static String SQL_UPDATE =
            "UPDATE UserInteraction "
            + "SET linkType = ? "
            + "WHERE userId = ? AND "
            + "interactedUserId = ? AND "
            + "timestamp = ? AND "
            + "interaction = ? AND "
            + "tweetId = ?";
    private final static String SQL_DELETE = 
            "DELETE FROM UserInteraction "
            + "WHERE userId = ? "
            + "AND interactedUserId = ? AND "
            + "timestamp = ? AND "
            + "interaction = ? AND "
            + "tweetId = ?";
}
