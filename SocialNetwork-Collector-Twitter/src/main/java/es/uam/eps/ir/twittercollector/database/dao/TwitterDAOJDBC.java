/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.database.dao;

import static es.uam.eps.ir.database.dao.DAOUtil.prepareStatement;
import es.uam.eps.ir.twittercollector.database.dao.exceptions.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic class for interacting with databases.
 * @author Javier
 * @param <T>
 */
public abstract class TwitterDAOJDBC<T> 
{
    protected TwitterDAOFactory daoFactory;
    
    /**
     * Constructor.
     * @param daoFactory Factory
     */
    public TwitterDAOJDBC(TwitterDAOFactory daoFactory)
    {
        this.daoFactory = daoFactory;
    }
    
    /**
     * Creates an object in the database.
     * @param sql SQL query.
     * @param generatedKeys Checks if keys have been generated. Set to false in case of update.
     * @param needModify Indicates if the query must affect rows in the database.
     * @param values Values for the query.
     * @return The generated keys for the created object if generatedKeys is true. null if not 
     */
    protected ResultSet create(String sql, boolean generatedKeys, boolean needModify, Object ... values)
    {
        ResultSet resSet = null;
        PreparedStatement statement = null;
        try 
        {
            Connection connection = daoFactory.getConnection();
            statement = prepareStatement(connection, sql, generatedKeys, values);
        
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0 && needModify) 
            {
                throw new DAOException("Creating failed, no rows affected.");
            }
            
            if(generatedKeys)
            {
                resSet = statement.getGeneratedKeys();
                if(!resSet.next())
                {
                    throw new DAOException("Creating failed. Keys were not generated ");
                }
            }
            
            return resSet;
        } 
        catch (SQLException e) 
        {
            throw new DAOException(e);
        }
        finally
        {
            if(statement != null && generatedKeys == false)
            try 
            {
                statement.close();
            } 
            catch (SQLException ex) 
            {
                throw new DAOException(ex);
            }
        }
    }
    
    /**
     * Updates an object in the database.
     * @param sql SQL query.
     * @param needModify
     * @param values Values for the query.
     */
    protected void update(String sql, boolean needModify, Object ... values)
    {
        try
        {
            create(sql, false, needModify, values);
        }
        catch (DAOException ex)
        {
            throw new DAOException("Updating failed, no rows affected");
        }
    }
    
    /**
     * Deletes objects from the database.
     * @param sql SQL query.
     * @param values Values for the query.
     */
    protected void delete(String sql, boolean needModify, Object ... values)
    {
        try
        {
            create(sql, false, needModify, values);
        }
        catch(DAOException ex)
        {
            throw new DAOException("Deleting failed, no rows affected");
        }
    }
    
    /**
     * Returns the object from the database matching the given SQL query with the given values.
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return The object from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    protected T find(String sql, Object... values) throws DAOException 
    {
        T t = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try 
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, sql, false, values);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                t = map(resultSet);
            }
        } catch (SQLException e) {
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
                catch (SQLException ex) 
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
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
        }
        return t;
    }
    
    /**
     * Returns a list of objects in the database
     * @param sql SQL Query
     * @param values Aditional params for the query.
     * @return The list of objects. This list will never be null. It will be an empty list if there are no results.
     * @throws DAOException If something fails at database level.
     */
    protected List<T> list(String sql, Object... values) throws DAOException
    {
        List<T> objects = new ArrayList<>();
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, sql, false, values);
            resultSet = statement.executeQuery();
            
            int i = 0;
            while(resultSet.next())
            {
                T object = map(resultSet);
                objects.add(object);
                
                if(++i % 10000 == 0)
                {
                    System.out.println("Listed : " + i);
                }
            }
        }
        catch(SQLException e)
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
                catch (SQLException ex) 
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
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
        }
        
        return objects;
    }
    
    /**
     * Checks if an object exists in the database.
     * @param sql SQL Query.
     * @param values Aditional params for the query.
     * @return True if the object(s) exist(s), false if not.
     * @throws DAOException If something fails at database level.
     */
    protected boolean exists(String sql, Object ... values) throws DAOException
    {
        Connection connection;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, sql, false, values);
            resultSet = statement.executeQuery();
            
            return resultSet.next();            
        }
        catch(SQLException e)
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
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            
            if(resultSet != null)
                try 
                {
                    resultSet.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
        }
    }
    
    
    
    
    
    protected TwitterDAOFactory getFactory()
    {
        return this.daoFactory;
    }
    
    protected abstract T map(ResultSet resultSet) throws SQLException;
}
