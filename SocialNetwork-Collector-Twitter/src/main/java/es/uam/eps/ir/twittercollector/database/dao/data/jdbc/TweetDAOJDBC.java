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
import es.uam.eps.ir.twittercollector.database.dao.data.TweetDAO;
import static es.uam.eps.ir.database.dao.DAOUtil.*;
import es.uam.eps.ir.twittercollector.database.dao.exceptions.DAOException;
import es.uam.eps.ir.twittercollector.database.data.Hashtag;
import es.uam.eps.ir.twittercollector.database.data.Media;
import es.uam.eps.ir.twittercollector.database.data.Retweet;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import es.uam.eps.ir.twittercollector.database.data.Url;
import es.uam.eps.ir.twittercollector.database.data.User;
import es.uam.eps.ir.utils.Par;
import java.sql.*;
import java.util.*;

/**
 * This class represents a concrete JDBC implementation of the {@link TweetDAO} interface.
 *
 * @author Javier
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public class TweetDAOJDBC extends TwitterDAOJDBC<Tweet> implements TweetDAO {
  
    // Constructors -------------------------------------------------------------------------------
    /**
     * Construct an User DAO for the given DAOFactory. Package private so that it can be constructed
     * inside the DAO package only.
     * @param daoFactory The DAOFactory to construct this User DAO for.
     */
    public TweetDAOJDBC(TwitterDAOFactory daoFactory) 
    {
        super(daoFactory);
    }

    // Actions ------------------------------------------------------------------------------------

    // CREATION
    @Override
    public void create(Tweet tweet) throws IllegalArgumentException, DAOException 
    {
        if(tweet == null)
        {
            throw new IllegalArgumentException("Tweet cannot be null");
        }
        
        Object[] values = 
        {
            tweet.getTweetId(),
            tweet.getUserId(),
            tweet.getText(),
            tweet.getRetweetCount(),
            tweet.getFavoriteCount(),
            toSqlDate(tweet.getCreated()),
            tweet.getTruncated()
        };

        this.create(SQL_INSERT, false, true, values);
    }
    
    
    
    // UPDATING
    @Override
    public void update(Tweet tweet) throws DAOException 
    {
        if (tweet.getTweetId() == null) 
        {
            throw new IllegalArgumentException("Tweet is not created yet, the tweet ID is null.");
        }

        Object[] values = 
        {
            tweet.getTweetId(),
            tweet.getUserId(),
            tweet.getText(),
            tweet.getRetweetCount(),
            tweet.getFavoriteCount(),
            toSqlDate(tweet.getCreated()),
            tweet.getTruncated(),
            tweet.getTweetId()
        };

        this.update(SQL_UPDATE, true, values);
    } 
    
    // DELETING
    @Override
    public void delete(Tweet tweet) throws DAOException 
    {
        this.delete(SQL_DELETE, true, tweet.getTweetId());
    }
    
    // ASSOCIATION
    @Override
    public void createRetweet(Tweet original, Tweet retweet) 
    {
        if(original == null || retweet == null)
        {
            throw new IllegalArgumentException("Tweets cannot be null");
        }
        Object[] values = 
        {
            original.getTweetId(),
            retweet.getTweetId(),
        };

        this.create(SQL_CREATE_RETWEET, false, false, values);  
    }
    
    @Override
    public void associateHashtag(Tweet tweet, Hashtag hashtag) 
    {
        if(tweet == null || hashtag == null)
        {
            throw new IllegalArgumentException("Tweets and hashtags cannot be null");
        }
        
        Object[] values = {
            tweet.getTweetId(),
            hashtag.getHashtagId(),
        };

        this.create(SQL_ASSOCIATE_HASHTAG, false, true, values);
    }
    
    @Override
    public void associateUrl(Tweet tweet, Url url) 
    {
        if(tweet == null || url == null)
        {
            throw new IllegalArgumentException("Tweet and url cannot be null");
        }
        
        Object[] values = 
        {
            tweet.getTweetId(),
            url.getUrlId(),
        };

        this.create(SQL_ASSOCIATE_URL, false, false, values);
    } 
    
    @Override
    public void associateMention(Tweet tweet, User user) 
    {
        Object[] values = 
        {
            tweet.getTweetId(),
            user.getUserId(),
        };

        this.create(SQL_ASSOCIATE_MENTION, false, false, values);
    } 

    @Override
    public void associateMedia(Tweet tweet, Media media) 
    {
        if(tweet == null || media == null)
        {
            throw new IllegalArgumentException("Tweet and media cannot be null");
        }
        
        Object[] values = 
        {
            tweet.getTweetId(),
            media.getMediaId(),
        };

        this.create(SQL_ASSOCIATE_MEDIA, false, false, values);
    }
    
    
    @Override
    public void associateReply(Tweet originalTweet, Tweet reply) 
    {
        if(reply == null || originalTweet == null)
        {
            throw new IllegalArgumentException("Tweets cannot be null");
        }
        
        Object[] values = 
        {
            originalTweet.getTweetId(),
            reply.getTweetId()
        };
        
        this.create(SQL_ASSOCIATE_REPLY, false, false, values);
    }
    
    // FINDING
    @Override
    public Tweet find(Long id) throws DAOException 
    {
        return find(SQL_FIND_BY_ID, id);
    }

    @Override
    public List<Tweet> list() throws DAOException 
    {
        return list(SQL_LIST_ORDER_BY_ID);
    }

    @Override
    public List<Tweet> listUser(User user) throws DAOException 
    {
        if(user == null)
        {
            throw new IllegalArgumentException("User cannot be null");
        }
        return list(SQL_LIST_BY_USER, user.getUserId());
    }
    
    @Override
    public List<Tweet> getIntervalTweets(int offset, int count) 
    {
        List<Tweet> tweets = new ArrayList<>();

        Object[] values =
        {
            offset,
            count
        };
        
        return list(SQL_LIST_CREATED_INTERVAL, values);
    }
    
    // EXISTENCE
    @Override
    public boolean existsRetweet(Tweet original, Tweet retweet) 
    {
        Object[] values = 
        {
            original.getTweetId(),
            retweet.getTweetId(),
        };
        
        return exists(SQL_EXIST_RETWEET, values);
    }

    @Override
    public boolean isAssociatedHashtag(Tweet tweet,Hashtag hashtag) 
    {
        Object[] values = 
        {
            tweet.getTweetId(),
            hashtag.getHashtagId(),
        };
        
        return exists(SQL_EXIST_ASSOC_HASHTAG, values);
    }    

    // Retweets
    @Override
    public Tweet isRetweet(Tweet tweet) 
    {
        Object[] values = 
        {
            tweet.getTweetId(),
        };
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try 
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_IS_RETWEET, false, values);
            resultSet = statement.executeQuery();
        
            if (resultSet.next()) 
            {
                return find(resultSet.getLong("originalTweet"));
            }
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
        finally
        {
            if(statement != null)
                try
                {
                    statement.close();
                }
                catch(SQLException e)
                {
                    throw new DAOException(e);
                }
            if(resultSet != null)
                try
                {
                    resultSet.close();
                }
                catch(SQLException e)
                {
                    throw new DAOException(e);
                }
        }
        return null;
    }
    
    @Override
    public List<Retweet> getRetweetList()
    {
        List<Retweet> tweets = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try 
        {
            conn = daoFactory.getConnection();
            statement = conn.prepareStatement(TweetDAOJDBC.SQL_GET_RETWEET_LIST);
            resultSet = statement.executeQuery();
            while (resultSet.next()) 
            {
                tweets.add(mapRetweet(resultSet));
            }
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
        finally
        {
            if(statement != null)
                try
                {
                    statement.close();
                }
                catch(SQLException e)
                {
                    throw new DAOException(e);
                }
            if(resultSet != null)
                try
                {
                    resultSet.close();
                }
                catch(SQLException e)
                {
                    throw new DAOException(e);
                }
        }

        return tweets;
    }
    
    
    
    // OTHER QUERIES (count, ...)
    @Override
    public Integer countTweets() 
    {
        Integer numTweets = 0;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try
        {
            connection = daoFactory.getConnection();
            statement = connection.prepareStatement(SQL_COUNT);
            resultSet = statement.executeQuery();
        
            if (resultSet.next()) 
            {
                numTweets = resultSet.getInt("c");
            }
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
        finally
        {
            if(statement != null)
                try
                {
                    statement.close();
                }
                catch(SQLException e)
                {
                    throw new DAOException(e);
                }
            if(resultSet != null)
                try
                {
                    resultSet.close();
                }
                catch(SQLException e)
                {
                    throw new DAOException(e);
                }
        }

        return numTweets;    
    }
    
    @Override
    public List<Long> isMention(Tweet tweet) throws DAOException 
    {
        List<Long> isMention = new ArrayList<>();
        Object[] values = 
        {
            tweet.getTweetId(),
        };
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_IS_MENTION, false, values);
            resultSet = statement.executeQuery();
        
            while (resultSet.next()) 
            {
                isMention.add(resultSet.getLong("userId"));
            }
            
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
        finally
        {
            if(statement != null)
                try
                {
                    statement.close();
                }
                catch(SQLException e)
                {
                    throw new DAOException(e);
                }
            if(resultSet != null)
                try
                {
                    resultSet.close();
                }
                catch(SQLException e)
                {
                    throw new DAOException(e);
                }
        }
        return isMention;
        
    }
    // Helpers ------------------------------------------------------------------------------------

    /**
     * Map the current row of the given ResultSet to an User.
     * @param resultSet The ResultSet of which the current row is to be mapped to an User.
     * @return The mapped User from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    @Override
    protected Tweet map(ResultSet resultSet) throws SQLException 
    {
        Tweet tweet = new Tweet();
        tweet.setTweetId(resultSet.getLong("tweetId"));
        tweet.setUserId(resultSet.getLong("userId"));
        tweet.setText(resultSet.getString("text"));
        tweet.setRetweetCount(resultSet.getInt("retweetCount"));
        tweet.setFavoriteCount(resultSet.getInt("favoriteCount"));
        tweet.setTruncated(resultSet.getShort("truncated"));
        tweet.setCreated(toUtilDate(resultSet.getString("created")));
        return tweet;
    }

    /**
     * Maps the current row of the given ResultSet to a Retweet.
     * @param resultSet The ResultSet of which the current row is to be mapped to a Retweet.
     * @return The mapped Retweet from the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    private Retweet mapRetweet(ResultSet resultSet) throws SQLException
    {
        Retweet retweet = new Retweet();
        retweet.setOriginalTweet(resultSet.getLong("originalTweet"));
        retweet.setRetweet(resultSet.getLong("retweet"));
        return retweet;
    }   
    
    
    /**
     * Maps the current row of the given ResultSet to a Retweet.
     * @param resultSet The ResultSet of which the current row is to be mapped to a Retweet.
     * @return The mapped Retweet from the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    private Par<Long, Long> mapCounter(ResultSet resultSet) throws SQLException
    {
        Par<Long,Long> counter = new Par<>(resultSet.getLong("tweetId"),resultSet.getLong("counter") );
        
        return counter;
    }
    

    
    
    

    

    
    
    
    // Constants ----------------------------------------------------------------------------------

    private static final String SQL_FIND_BY_ID =
        "SELECT tweetId, "
            + "userId, "
            + "text, "
            + "retweetCount, "
            + "favoriteCount, "
            + "created, "
            + "truncated "
            + "FROM Tweet "
            + "WHERE tweetId = ?";
 
    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT tweetId, "
            + "userId, "
            + "text, "
            + "retweetCount, "
            + "favoriteCount, "
            + "created, "
            + "truncated "
            + "FROM Tweet "
            + "ORDER BY tweetId";
    
    private static final String SQL_LIST_BY_USER = 
        "SELECT tweetId, "
            + "userId, "
            + "text, "
            + "retweetCount, "
            + "favoriteCount, "
            + "created, "
            + "truncated "
            + "FROM Tweet "
            + "WHERE userId = ? "
            + "ORDER BY tweetId";
    
    private static final String SQL_INSERT =
        "INSERT INTO Tweet (tweetId, "
            + "userId, "
            + "text, "
            + "retweetCount, "
            + "favoriteCount, "
            + "created, "
            + "truncated) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_UPDATE =
        "UPDATE Tweet "
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
            + "numTweets = ? "
            + "WHERE userId = ?";
    
    private static final String SQL_DELETE =
        "DELETE FROM Tweet "
            + "WHERE tweetId = ?";
    
    private static final String SQL_EXIST_USER =
        "SELECT tweetId "
            + "FROM Tweet "
            + "WHERE tweetId  = ?";
    
    private static final String SQL_CREATE_RETWEET =
        "INSERT INTO Retweet (originalTweet, "
            + "retweet) "
            + "VALUES (?,?) "
            + "ON DUPLICATE KEY UPDATE "
            + "originalTweet = retweet";
    
    private static final String SQL_EXIST_RETWEET = 
        "SELECT originalTweet, "
            + "retweet FROM Retweet "
            + "WHERE originalTweet = ? "
            + "AND retweet = ?";
    
    private static final String SQL_ASSOCIATE_HASHTAG =
        "INSERT INTO HashtagTweet (tweetId, "
            + "hashtagId) "
            + "VALUES (?,?) "
            + "ON DUPLICATE KEY UPDATE tweetId = tweetId";
    
    private static final String SQL_EXIST_ASSOC_HASHTAG =
        "SELECT tweetID "
            + "FROM HashtagTweet "
            + "WHERE tweetId = ? "
            + "AND hashtagId = ?";
    
    private static final String SQL_ASSOCIATE_URL =
        "INSERT INTO UrlTweet (tweetId, "
            + "urlId) "
            + "VALUES (?,?) "
            + "ON DUPLICATE KEY UPDATE tweetId = tweetId";
    
    private static final String SQL_ASSOCIATE_MEDIA =
        "INSERT INTO MediaTweet (tweetId, "
            + "mediaId) "
            + "VALUES (?,?) "
            + "ON DUPLICATE KEY UPDATE tweetId = tweetId";
    
    private static final String SQL_ASSOCIATE_MENTION =
        "INSERT INTO Mentions (tweetId, "
            + "userId) "
            + "VALUES (?,?) "
            + "ON DUPLICATE KEY UPDATE tweetId = tweetId";
    
    private static final String SQL_ASSOCIATE_REPLY = 
        "INSERT INTO Reply (originalTweet, reply) "
            + "VALUES (?, ?) "
            + "ON DUPLICATE KEY UPDATE originalTweet = originalTweet";
            
    
    private static final String SQL_GET_RETWEET_LIST =
        "SELECT originalTweet, "
            + "retweet "
            + "FROM Retweet "
            + "ORDER BY originalTweet";
    
    private static final String SQL_IS_RETWEET =
        "SELECT originalTweet "
            + "FROM Retweet "
            + "WHERE retweet = ?";
    
    private static final String SQL_IS_MENTION =
        "SELECT userId "
            + "FROM Mentions "
            + "WHERE tweetId = ?";
    
    private static final String SQL_COUNT =
        "SELECT count(*) AS c "
            + "FROM Tweet";
    
    private static final String SQL_LIST_CREATED_INTERVAL =
        "SELECT tweetId, "
            + "userId, "
            + "text, "
            + "retweetCount, "
            + "favoriteCount, "
            + "created, "
            + "truncated "
            + "FROM Tweet "
            + "ORDER BY created "
            + "LIMIT ?,?";

    @Override
    public ResultSet listUNED() 
    {
                
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_LIST_UNED, false);
            resultSet = statement.executeQuery();
        
            return resultSet;            
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
    }
    
    @Override
    public ResultSet listDataset() 
    {
                
        Connection connection;
        PreparedStatement statement;
        ResultSet resultSet;
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
    
    @Override
    public ResultSet listDatasetSimulation() 
    {
                
        Connection connection;
        PreparedStatement statement;
        ResultSet resultSet;
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_DATASET_SIMULATIONS, false);
            resultSet = statement.executeQuery();
        
            return resultSet;            
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
    }
    
    @Override
    public ResultSet listDatasetHashtags() 
    {
                
        Connection connection;
        PreparedStatement statement;
        ResultSet resultSet;
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_LIST_HASHTAGS_ORDER_BY_ID, false);
            resultSet = statement.executeQuery();
        
            return resultSet;            
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
    }
    
    @Override
    public ResultSet listDatasetUrls() 
    {
                
        Connection connection;
        PreparedStatement statement;
        ResultSet resultSet;
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_LIST_URLS_ORDER_BY_ID, false);
            resultSet = statement.executeQuery();
        
            return resultSet;            
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
    }
    
    @Override
    public ResultSet listDatasetRetweets() 
    {
                
        Connection connection;
        PreparedStatement statement;
        ResultSet resultSet;
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_GET_RETWEET_LIST, false);
            resultSet = statement.executeQuery();
        
            return resultSet;            
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
    }
    
    @Override
    public ResultSet listDatasetMentions() 
    {
                
        Connection connection;
        PreparedStatement statement;
        ResultSet resultSet;
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_LIST_MENTIONS_ORDER_BY_ID, false);
            resultSet = statement.executeQuery();
        
            return resultSet;            
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
    }
    @Override
    public ResultSet listTweetInfo(Long tweetId) 
    {
        Object[] values = { tweetId };
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_LIST_TWEETINFO, false);
            resultSet = statement.executeQuery();
        
            return resultSet;            
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
    }
    
    private static final String SQL_LIST_UNED =
            "SELECT tweetId, UNIX_TIMESTAMP(created) AS timestamp, text  "
            + "FROM Tweet "
            + "ORDER BY tweetId";
    
    private static final String SQL_LIST_TWEETINFO =
            "SELECT tweetId AS tweet_id, " +
            "userId AS author, " + 
            "'database' AS entity_id, " +
            "'tweet_url' AS tweet_url, " +
            "'es' AS language, " +
            "UNIX_TIMESTAMP(created)*1000 AS timestamp, " +
            "'' AS urls, " +
            "'' AS extended_urls, " +
            "'' AS md5_extended_urls " +
            "FROM Tweet " +
            "ORDER BY tweetId";

    public List<Par<Long,Long>> listRetweetsTimes(Long tweetPresent, Long tweetFuture)
    {
        List<Par<Long,Long>> tweets = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        Object[] values = new Object[]
        {
            tweetPresent,
            tweetFuture,
            tweetPresent
        };
        try 
        {
            conn = daoFactory.getConnection();
            statement = prepareStatement(conn, SQL_LIST_RETWEETS_BETWEEN_TIMES, false, values);
            
            resultSet = statement.executeQuery();
            while (resultSet.next()) 
            {
                tweets.add(mapCounter(resultSet));
            }
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
        finally
        {
            if(statement != null)
                try
                {
                    statement.close();
                }
                catch(SQLException e)
                {
                    throw new DAOException(e);
                }
            if(resultSet != null)
                try
                {
                    resultSet.close();
                }
                catch(SQLException e)
                {
                    throw new DAOException(e);
                }
        }

        return tweets;
    }
    
    private static final String SQL_LIST_RETWEETS_BETWEEN_TIMES =
            "SELECT tweetId, count(*) AS counter FROM "
            + "Tweet INNER JOIN Retweet ON "
            + "Tweet.tweetId = Retweet.originalTweet "
            + "WHERE Retweet.retweet > ? "
            + "AND Retweet.retweet <= ? "
            + "AND Tweet.tweetId < ? "
            + "GROUP BY Tweet.tweetId";

    @Override
    public List<Tweet> listNotRetweets(Long tweetId) 
    {
        Object[] values = new Object[]
        {
            tweetId,
            tweetId,
        };
        
        return this.list(SQL_LIST_NOT_RETWEETS, values);
    }
    
    
    
    private static final String SQL_LIST_NOT_RETWEETS = 
            "SELECT tweetId, "
            + "userId, "
            + "text, "
            + "retweetCount, "
            + "favoriteCount, "
            + "created, "
            + "truncated "
            + "FROM Tweet "
            + "WHERE tweetId < ? "
            + "AND tweetId NOT IN (SELECT retweet AS tweetId "
            + "FROM Retweet "
            + "WHERE retweet < ? )";
    
    private static final String SQL_LIST_HASHTAGS_ORDER_BY_ID =
            "SELECT tweetId, hashtagId "
            + "FROM HashtagTweet "
            + "ORDER BY tweetId";
    
    private static final String SQL_LIST_URLS_ORDER_BY_ID =
            "SELECT tweetId, urlId "
            + "FROM UrlTweet "
            + "ORDER BY tweetId";
    
    private static final String SQL_LIST_MENTIONS_ORDER_BY_ID =
        "SELECT tweetId, userId "
            + "FROM Mentions "
            + "ORDER BY tweetId";
    
    private static final String SQL_DATASET_SIMULATIONS = 
        "SELECT tweetId, userId,unix_timestamp(created) "
            + "FROM Tweet WHERE "
            + " tweetId NOT IN (SELECT retweet AS tweetId "
            + " FROM Retweet)"; 

    @Override
    public ResultSet listDatasetHashtagsSimulation() {
        Connection connection;
        PreparedStatement statement;
        ResultSet resultSet;
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, SQL_HASHTAG_TWEET, false);
            resultSet = statement.executeQuery();
        
            return resultSet;            
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
    }
    
    private static final String SQL_HASHTAG_TWEET = 
        "SELECT tweetId,hashtagId "
        + "FROM HashtagTweet "
        + "WHERE tweetId NOT IN "
        + "(SELECT retweet as tweetId "
        + "FROM Retweet)";


    
    
}
