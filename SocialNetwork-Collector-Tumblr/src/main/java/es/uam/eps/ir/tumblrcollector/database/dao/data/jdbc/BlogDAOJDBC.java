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
import es.uam.eps.ir.tumblrcollector.database.dao.data.BlogDAO;
import es.uam.eps.ir.tumblrcollector.database.data.Blog;
import es.uam.eps.ir.database.dao.exceptions.DAOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Javier
 */
public class BlogDAOJDBC extends TumblrDAOJDBC<Blog> implements BlogDAO
{ 

    public BlogDAOJDBC(TumblrDAOFactory daoFactory) 
    {
        super(daoFactory);
    }
    
    @Override
    public Blog find(Long blogId) throws DAOException 
    {
        return this.find(SQL_FIND, blogId);
    }

    @Override
    public Blog find(String name) throws DAOException 
    {
        return this.find(SQL_FIND_BY_NAME, name);
    }

    @Override
    public List<Blog> list() throws DAOException 
    {
        return this.list(SQL_LIST);
    }
    
    @Override
    public List<Blog> list(Integer level) throws DAOException 
    {
        return this.list(SQL_LIST_BY_LEVEL, level);
    }

    @Override
    public List<Blog> listNotVisited(Integer level) throws DAOException 
    {
        Object[] values = 
        {
            level,
            false
        };
        return this.list(SQL_LIST_NOT_VISITED, values);
    }

    @Override
    public void create(Blog blog) throws DAOException 
    {
        if(blog == null)
            throw new IllegalArgumentException("Blog cannot be null");
        
        Object[] values =
        {
            blog.getName(),
            blog.getUrl(),
            blog.getTitle(),
            blog.getDescription(),
            blog.getNumPosts(),
            false,
            true,
            blog.getNumLikes(),
            blog.getIsVisited() ? 1 : 0,
            blog.getLevel()
        };
        
        ResultSet generatedKeys = this.create(SQL_CREATE, true, true, values);
        try 
        {
            blog.setBlogId(generatedKeys.getLong(1));
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
    public void update(Blog blog) throws DAOException 
    {
        if(blog == null)
            throw new IllegalArgumentException("Blog cannot be null");
        
        Object[] values =
        {
            blog.getBlogId(),
            blog.getName(),
            blog.getUrl(),
            blog.getTitle(),
            blog.getDescription(),
            blog.getNumPosts(),
            blog.getIsNFSW() ? 1 : 0,
            blog.getShareLikes() ? 1 : 0,
            blog.getNumLikes(),
            blog.getIsVisited() ? 1 : 0,
            blog.getLevel(),
            blog.getBlogId()
        };
        
        this.update(SQL_UPDATE, false, values);
    }

    @Override
    public void delete(Blog blog) throws DAOException 
    {
        if(blog == null)
            throw new IllegalArgumentException("Blog cannot be null");
        this.delete(SQL_DELETE, true, blog.getBlogId());
    }
    
    @Override
    protected Blog map(ResultSet resultSet) throws SQLException 
    {
        Blog blog = new Blog();
        blog.setBlogId(resultSet.getLong("blogId"));
        blog.setName(resultSet.getString("name"));
        blog.setUrl(resultSet.getString("url"));
        blog.setTitle(resultSet.getString("title"));
        blog.setDescription(resultSet.getString("description"));
        blog.setNumPosts(resultSet.getInt("numPosts"));
        blog.setIsNFSW(resultSet.getBoolean("isNFSW"));
        blog.setShareLikes(resultSet.getBoolean("shareLikes"));
        blog.setNumLikes(resultSet.getInt("numLikes"));
        blog.setIsVisited(resultSet.getBoolean("visited"));
        blog.setLevel(resultSet.getInt("level"));
        
        return blog;
    }
    
    private static final String SQL_FIND = 
            "SELECT blogId, "
            + "name, "
            + "url, "
            + "title, "
            + "description, "
            + "numPosts, "
            + "isNFSW, "
            + "shareLikes, "
            + "numLikes, "
            + "visited, "
            + "level "
            + "FROM Blog "
            + "WHERE blogId = ?";
    private static final String SQL_FIND_BY_NAME = 
            "SELECT blogId, "
            + "name, "
            + "url, "
            + "title, "
            + "description, "
            + "numPosts, "
            + "isNFSW, "
            + "shareLikes, "
            + "numLikes, "
            + "visited, "
            + "level "
            + "FROM Blog "
            + "WHERE name = ?";
    private static final String SQL_LIST = 
            "SELECT blogId, "
            + "name, "
            + "url, "
            + "title, "
            + "description, "
            + "numPosts, "
            + "isNFSW, "
            + "shareLikes, "
            + "numLikes, "
            + "visited, "
            + "level "
            + "FROM Blog";
    private static final String SQL_LIST_BY_LEVEL = 
             "SELECT blogId, "
            + "name, "
            + "url, "
            + "title, "
            + "description, "
            + "numPosts, "
            + "isNFSW, "
            + "shareLikes, "
            + "numLikes, "
            + "visited, "
            + "level "
            + "FROM Blog "
            + "WHERE level = ?";
    private static final String SQL_LIST_NOT_VISITED = 
            "SELECT blogId, "
            + "name, "
            + "url, "
            + "title, "
            + "description, "
            + "numPosts, "
            + "isNFSW, "
            + "shareLikes, "
            + "numLikes, "
            + "visited, "
            + "level "
            + "FROM Blog "
            + "WHERE level = ? "
            + "AND visited = ?";
    private static final String SQL_CREATE =
            "INSERT INTO Blog ("
            + "name, "
            + "url, "
            + "title, "
            + "description, "
            + "numPosts, "
            + "isNFSW, "
            + "shareLikes, "
            + "numLikes, "
            + "visited, "
            + "level) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
            "UPDATE Blog "
            + "SET blogId = ?, "
            + "name = ?, "
            + "url = ?, "
            + "title = ?, "
            + "description = ?, "
            + "numPosts = ?, "
            + "isNFSW = ?, "
            + "shareLikes = ?, "
            + "numLikes = ?, "
            + "visited = ?, "
            + "level = ? "
            + "WHERE blogId = ?";
    private static final String SQL_DELETE = 
            "DELETE FROM Blog "
            + "WHERE blogId = ?";

    
}
