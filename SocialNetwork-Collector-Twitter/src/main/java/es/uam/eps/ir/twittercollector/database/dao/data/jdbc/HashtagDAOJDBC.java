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
import es.uam.eps.ir.twittercollector.database.dao.data.HashtagDAO;
import es.uam.eps.ir.twittercollector.database.data.Hashtag;
import es.uam.eps.ir.twittercollector.database.dao.exceptions.DAOException;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import es.uam.eps.ir.utils.TextCleaner;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a concrete JDBC implementation of the {@link HashtagDAO} interface.
 * @author Javier Sanz-Cruzado
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public class HashtagDAOJDBC extends TwitterDAOJDBC<Hashtag> implements HashtagDAO {

    // Constants ----------------------------------------------------------------------------------
   
    // Vars ---------------------------------------------------------------------------------------

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an Hashtag DAO for the given DAOFactory.
     * @param daoFactory The DAOFactory to construct this Hashtag DAO for.
     */
    public HashtagDAOJDBC(TwitterDAOFactory daoFactory) 
    {
        super(daoFactory);
    }

    // Actions ------------------------------------------------------------------------------------

    // CREATION
    @Override
    public void create(Hashtag hashtag) throws IllegalArgumentException, DAOException 
    {
        try 
        {
            if(hashtag == null)
                throw new IllegalArgumentException("Hashtag cannot be null");
            
            ResultSet generatedKeys = create(SQL_INSERT, true, false, TextCleaner.cleanText(hashtag.getText()));
            try
            {
                hashtag.setHashtagId(generatedKeys.getLong(1));
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
        catch (UnsupportedEncodingException ex) 
        {
            Logger.getLogger(HashtagDAOJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    // UPDATE
    @Override
    public void update(Hashtag hashtag) throws DAOException {
        if (hashtag == null) {
            throw new IllegalArgumentException("Hashtag cannot be null.");
        }

        Object[] values = {
            hashtag.getHashtagId(),
            hashtag.getText()
        };

        update(SQL_UPDATE, true, values);
    }
    
    // DELETE
    @Override
    public void delete(Hashtag hashtag) throws DAOException 
    {
        if(hashtag == null)
        {
            throw new IllegalArgumentException("Hashtag cannot be null");
        }

        delete(SQL_DELETE, true, hashtag.getHashtagId());
        hashtag.setHashtagId(null);
    }
    
    @Override
    public Hashtag find(Long id) throws DAOException 
    {
        return find(SQL_FIND_BY_ID, id);
    }

    @Override
    public Hashtag find(String text) throws DAOException 
    {
        return find(SQL_FIND_BY_TEXT, text);
    }

    @Override
    public List<Hashtag> list() throws DAOException 
    {
        return list(SQL_LIST_ORDER_BY_ID);
    } 
     
    // Helpers ------------------------------------------------------------------------------------

    /**
     * Map the current row of the given ResultSet to a Hashtag.
     * @param resultSet The ResultSet of which the current row is to be mapped to a Hashtag.
     * @return The mapped Hashtag from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    @Override
    protected Hashtag map(ResultSet resultSet) throws SQLException {
        Hashtag hashtag = new Hashtag();
        hashtag.setHashtagId(resultSet.getLong("hashtagId"));
        hashtag.setText(resultSet.getString("text"));
 
        
        return hashtag;
    }

    @Override
    public ResultSet listDataset() {
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
    
    @Override
    public List<Hashtag> getAssociatedHashtags(Tweet tweet) 
    {
        Object[] values = 
        {
            tweet.getTweetId()
        };
        return this.list(SQL_GET_HASHTAGS, values);
    }
    
    /**
     * Query for finding a hashtag by its database identifier.
     */
    private static final String SQL_FIND_BY_ID =
        "SELECT hashtagId, "
            + "text "
            + "FROM Hashtag "
            + "WHERE hashtagId = ?";
    /**
     * Query for finding a list of hashtags ordered by their database identifier.
     */
    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT hashtagId, "
            + "text "
            + "FROM "
            + "Hashtag "
            + "ORDER BY hashtagId";
    /**
     * Query for finding a hashtag by its text.
     */
    private static final String SQL_FIND_BY_TEXT = 
        "SELECT hashtagId, "
            + "text "
            + "FROM Hashtag "
            + "WHERE text = ?";
    /**
     * Query for inserting a new hashtag in the database. Identifier automatically assigned.
     */
    private static final String SQL_INSERT =
        "INSERT INTO Hashtag (text) "
            + "VALUES (?)";
    /**
     * Query for updating a hashtag given its identifier.
     */
    private static final String SQL_UPDATE =
        "UPDATE Hashtag "
            + "SET hashtagId = ?, "
            + "text = ? "
            + "WHERE hashtagId = ?";
    /**
     * Query for deleting a hashtag given its identifier.
     */
    private static final String SQL_DELETE =
        "DELETE FROM Hashtag "
            + "WHERE hashtagId = ?";
    /**
     * Query for finding if a hashtag exists. Returns the hashtag identifier.
     */
    private static final String SQL_EXIST_HASHTAG =
        "SELECT hashtagId "
            + "FROM Hashtag "
            + "WHERE hashtagId  = ?";

    private static final String SQL_GET_HASHTAGS =
        "SELECT Hashtag.hashtagId, text "
        + "FROM HashtagTweet INNER JOIN Hashtag "
        + "ON HashtagTweet.hashtagId = Hashtag.hashtagId "
        + "WHERE tweetId = ? ";

   

}
