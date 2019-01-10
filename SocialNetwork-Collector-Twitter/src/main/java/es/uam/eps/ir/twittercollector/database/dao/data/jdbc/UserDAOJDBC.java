/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.database.dao.data.jdbc;

import es.uam.eps.ir.database.dao.DAOUtil;
import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOFactory;
import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOJDBC;
import es.uam.eps.ir.twittercollector.database.dao.data.UserDAO;
import es.uam.eps.ir.twittercollector.database.data.User;
import static es.uam.eps.ir.database.dao.DAOUtil.prepareStatement;
import static es.uam.eps.ir.database.dao.DAOUtil.toSqlDate;
import es.uam.eps.ir.twittercollector.database.dao.exceptions.DAOException;
import es.uam.eps.ir.utils.TextCleaner;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a concrete JDBC implementation of the {@link UserDAO} interface.
 *
 * @author Javier
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public class UserDAOJDBC extends TwitterDAOJDBC<User> implements UserDAO 
{

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an User DAO for the given DAOFactory. Package private so that it can be constructed
     * inside the DAO package only.
     * @param daoFactory The DAOFactory to construct this User DAO for.
     */
    public UserDAOJDBC(TwitterDAOFactory daoFactory) 
    {
        super(daoFactory);
    }

    // Actions ------------------------------------------------------------------------------------
    
    // CREATION
    @Override
    public void create(User user) throws IllegalArgumentException, DAOException 
    {
        try {
            if(user == null)
            {
                throw new IllegalArgumentException("User cannot be null");
            }
            
            short visited = (user.isVisited() ? (short) 2 : (user.isWantedToVisit() ? (short)1 : (short)0));
            
            
            Object[] values = {
                user.getUserId(),
                TextCleaner.cleanText(user.getName()),
                user.getScreenName(),
                TextCleaner.cleanText(user.getDescription()),
                TextCleaner.cleanText(user.getLocation()),
                toSqlDate(user.getCreated()),
                user.getVerified(),
                user.getNumFollowers(),
                user.getNumFriends(),
                user.getNumListed(),
                user.getNumTweets(),
                visited,
                user.getLevel()
            };
            
            create(SQL_INSERT, false, true, values);
        } 
        catch (UnsupportedEncodingException ex) 
        {
            Logger.getLogger(UserDAOJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // UPDATE
    @Override
    public void update(User user) throws DAOException 
    {
        try {
            if (user == null)
            {
                throw new IllegalArgumentException("User cannot be null.");
            }
            
            short visited = (user.isVisited() ? (short) 2 : (user.isWantedToVisit() ? (short)1 : (short)0));
            
            
            Object[] values =
            {
                user.getUserId(),
                TextCleaner.cleanText(user.getName()),
                user.getScreenName(),
                TextCleaner.cleanText(user.getDescription()),
                TextCleaner.cleanText(user.getLocation()),
                toSqlDate(user.getCreated()),
                user.getVerified(),
                user.getNumFollowers(),
                user.getNumFriends(),
                user.getNumListed(),
                user.getNumTweets(),
                visited,
                user.getLevel(),
                user.getUserId(),
            };
            
            update(SQL_UPDATE, true, values);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserDAOJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void setVisited(User user, Integer level) 
    {
        if(user == null)
        {
            throw new IllegalArgumentException("User cannot be null.");
        }

        Object[] values =
        {
            level,
            user.getUserId()
            
        };
        
        update(SQL_UPDATE_VISITED, true, values);
    }
    
    // DELETION
    
    @Override
    public void delete(User user) throws DAOException 
    {
        if (user == null) 
        {
            throw new IllegalArgumentException("User cannot be null.");
        }
        
        delete(SQL_DELETE, true, user.getUserId());
    }
    
    // ASSOCIATION
    @Override
    public void addFollowRelation(User follower, User friend)
    {
        if(follower == null || friend == null)
            throw new IllegalArgumentException("Users to associate cannot be null");
        
        Object[] values = 
        {
            follower.getUserId(),
            friend.getUserId(),
        };
        
        
        create(SQL_ADD_FOLLOW_RELATION, false, true, values);       
    }
    
    @Override
    public void addOrUpdateRetweetRelation(User original, User retweeter) 
    {
        if(original == null || retweeter == null)
            throw new IllegalArgumentException("Users to associate cannot be null");
        
        Integer frequency = getRetweetRelation(original, retweeter);
        if(frequency == null) //La relacion no existe --> Crear
        {
            addRetweetRelation(original, retweeter);
        }
        else //Actualizar
        {
            frequency += 1;
            updateRetweetRelation(original, retweeter, frequency);
        }
    }
    
    private void updateRetweetRelation(User original, User retweeter, Integer frequency)
    {
        Object[] values = {
            original.getUserId(),
            original.getUserId(),
            frequency
        };

        update(SQL_UPDATE_RETWEET_RELATION, false, values);
    }

    private void addRetweetRelation(User original, User retweeter) 
    {
        Object[] values = {
            original.getUserId(),
            retweeter.getUserId(),
            1
        };

        create(SQL_CREATE_RETWEET_RELATION, false, true, values);
    }
    
    // SEARCH
    @Override
    public User find(Long id) throws DAOException 
    {
        return find(SQL_FIND_BY_ID, id);
    }

    @Override
    public List<User> list() throws DAOException 
    {
        return list(SQL_LIST_ORDER_BY_ID);
    }
    
    /**
     * Obtains the frequency of a Retweet relation.
     * @param original User who posted the original tweet
     * @param retweeter User who retweeted the tweet
     * @return The number of retweets the retweeted has made to the creator, null if none
     */
    private Integer getRetweetRelation(User original, User retweeter)
    {
        if(original == null || retweeter == null)
        {
            return null;
        }
        
        Object[] values = {
            original.getUserId(),
            retweeter.getUserId(),
        };
        
        Integer freq = null;
        Connection connection;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_GET_RETWEET_RELATION, false, values);
            resultSet = statement.executeQuery();

            if (resultSet.next()) 
            {
                freq = resultSet.getInt("frequency");
            }
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
        finally
        {
            if(statement != null)
            {
                try
                {
                    statement.close();
                }
                catch(SQLException ex)
                {
                    throw new DAOException(ex);
                }
            }
            if(resultSet != null)
            {
                try
                {
                    resultSet.close();
                }
                catch(SQLException ex)
                {
                    throw new DAOException(ex);
                }
            }            
        }
        
        return freq;
    }
    
    //OTHER QUERIES
    @Override
    public Integer count() throws DAOException {
        List<User> users = new ArrayList<>();

        Connection connection;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try
        {
            connection = daoFactory.getConnection();
            statement = connection.prepareStatement(SQL_COUNT);
            resultSet = statement.executeQuery();
        
            while (resultSet.next()) 
            {
                return resultSet.getInt("count");
            }
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
        finally
        {
            if(statement != null)
            {
                try
                {
                    statement.close();
                }
                catch(SQLException ex)
                {
                    throw new DAOException(ex);
                }
            }
            if(resultSet != null)
            {
                try
                {
                    resultSet.close();
                }
                catch(SQLException ex)
                {
                    throw new DAOException(ex);
                }
            }            
        }

        return null;
    }
    
    @Override
    public List<Long> getFollowing(Long followerId)
    {
        List<Long> followed = new ArrayList<Long>();
        Long freq = null;
        Connection connection;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_GET_FOLLOWED, false, followerId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) 
            {
                freq = resultSet.getLong("friend");
                followed.add(freq);
            }
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
        finally
        {
            if(statement != null)
            {
                try
                {
                    statement.close();
                }
                catch(SQLException ex)
                {
                    throw new DAOException(ex);
                }
            }
            if(resultSet != null)
            {
                try
                {
                    resultSet.close();
                }
                catch(SQLException ex)
                {
                    throw new DAOException(ex);
                }
            }            
        }
        
        return followed;
    }
    
    @Override
    public HashMap<Long, User> hashMap() throws DAOException
    {
        HashMap<Long, User> map = new HashMap<>();
        Connection connection = null;
        try
        {
            connection = this.daoFactory.getConnection();
            PreparedStatement preparedStatement = DAOUtil.prepareStatement(connection, SQL_LIST_ORDER_BY_ID, false);
            ResultSet resultSet = preparedStatement.executeQuery();
            // Urls
            while (resultSet.next()) 
            {
                User us = this.map(resultSet);
                map.put(us.getUserId(), us);
            }
            return map;
        }
        catch(SQLException e)
        {
            throw(new DAOException(e));
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
    protected User map(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getLong("userId"));
        user.setName(resultSet.getString("name"));
        user.setScreenName(resultSet.getString("screenName"));
        user.setDescription(resultSet.getString("description"));
        user.setLocation(resultSet.getString("location"));
        user.setCreated(resultSet.getDate("created"));
        user.setVerified(resultSet.getShort("verified"));
        user.setNumFollowers(resultSet.getInt("numFollowers"));
        user.setNumFriends(resultSet.getInt("numFriends"));
        user.setNumListed(resultSet.getInt("numListed"));
        user.setNumTweets(resultSet.getInt("numTweets"));
        short visited = resultSet.getShort("visited");
        user.setVisited(visited == 2);
        user.setWantedToVisit(visited > 0);
        user.setLevel(resultSet.getInt("level"));
        return user;
    }

    
    private static final String SQL_FIND_BY_ID =
        "SELECT userId, "
            + "name, "
            + "screenName, "
            + "description, "
            + "location, "
            + "created, "
            + "verified, "
            + "numFollowers, "
            + "numFriends, "
            + "numListed, "
            + "numTweets, "
            + "visited, "
            + "level "
            + "FROM User "
            + "WHERE userId = ?";
    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT userId, "
            + "name, "
            + "screenName, "
            + "description, "
            + "location, "
            + "created, "
            + "verified, "
            + "numFollowers, "
            + "numFriends, "
            + "numListed, "
            + "numTweets, "
            + "visited, "
            + "level "
            + "FROM User "
            + "ORDER BY userId";
    
    private static final String SQL_INSERT =
        "INSERT INTO User (userId, "
            + "name, "
            + "screenName, "
            + "description, "
            + "location, "
            + "created, "
            + "verified, "
            + "numFollowers, "
            + "numFriends, "
            + "numListed, "
            + "numTweets, "
            + "visited, "
            + "level) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
    
    private static final String SQL_UPDATE =
        "UPDATE User "
            + "SET userId = ?, "
            + "name = ?, "
            + "screenName = ?, "
            + "description = ?, "
            + "location = ?, "
            + "created = ?, "
            + "verified = ?, "
            + "numFollowers = ?, "
            + "numFriends = ?, "
            + "numListed = ?, "
            + "numTweets = ?, "
            + "visited = ?,"
            + "level = ? "
            + "WHERE userId = ?";
    
    private static final String SQL_DELETE =
        "DELETE FROM User "
            + "WHERE userId = ?";
    
    private static final String SQL_EXIST_USER =
        "SELECT userId "
            + "FROM User WHERE userId  = ?";
    
    private static final String SQL_ADD_FOLLOW_RELATION =
        "INSERT INTO Follows (follower, "
            + "friend) "
            + "VALUES (?,?) "
            + "ON DUPLICATE KEY UPDATE follower = follower";
    
    private static final String SQL_GET_RETWEET_RELATION =
        "SELECT creator, "
            + "retweeter, "
            + "frequency "
            + "FROM UsersRetweet "
            + "WHERE creator = ? "
            + "AND retweeter = ?";
    
    private static final String SQL_CREATE_RETWEET_RELATION =
        "INSERT INTO UsersRetweet (creator, "
            + "retweeter, "
            + "frequency) "
            + "VALUES (?,?,?)";
    
    private static final String SQL_UPDATE_RETWEET_RELATION =
        "UPDATE UsersRetweet "
            + "SET frequency = ? "
            + "WHERE creator = ? "
            + "AND retweeter = ?";
    
    private static final String SQL_LIST_RETWEET_RELATION =
        "SELECT creator, "
            + "retweeter, "
            + "frequency "
            + "FROM UsersRetweet "
            + "ORDER BY retweeter, creator";
    
    private static final String SQL_COUNT =
         "SELECT count(*) "
            + "AS count "
            + "FROM User";
    
    private static final String SQL_UPDATE_VISITED =
            "UPDATE User "
            + "SET visited = 2, "
            + "level = ? "
            + "WHERE userId = ?";
    
    private static final String SQL_GET_FOLLOWED =
            "SELECT friend "
            + "FROM Follows "
            + "WHERE follower = ?";

    @Override
    public ResultSet listAuthorInfo() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_LIST_AUTHORINFO, false);
            resultSet = statement.executeQuery();
        
            return resultSet;            
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
        
        
    }
    
    @Override
    public ResultSet listDataset() {
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
    
    @Override
    public ResultSet listDatasetFollows() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_LIST_FOLLOWS, false);
            resultSet = statement.executeQuery();
        
            return resultSet;            
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
    }
    
    private static final String SQL_LIST_FOLLOWS = 
        "SELECT follower, friend "
            + "FROM Follows";
    
    private static final String SQL_LIST_AUTHORINFO =
        "SELECT userId as screenname, "
            + "screenName as url, "
            + "description, "
            + "verified as verified_account, "
            + "numTweets, "
            + "numFriends as following, "
            + "numFollowers as followers, "
            + "location, "
            + "'' as homepage "
            + "FROM User";
    
    private static final String SQL_LIST_DATASET =
        "SELECT userId, "
            + "name, "
            + "screenName, "
            + "description, "
            + "location, "
            + "created, "
            + "verified, "
            + "numFollowers, "
            + "numFriends, "
            + "numListed, "
            + "numTweets "
            + "FROM User "
            + "ORDER BY userId";
}
