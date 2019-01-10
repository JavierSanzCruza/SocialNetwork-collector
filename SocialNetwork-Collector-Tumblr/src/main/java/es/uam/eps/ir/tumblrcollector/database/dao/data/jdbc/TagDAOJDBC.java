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
import es.uam.eps.ir.tumblrcollector.database.dao.data.TagDAO;
import es.uam.eps.ir.tumblrcollector.database.data.Post;
import es.uam.eps.ir.tumblrcollector.database.data.Tag;
import es.uam.eps.ir.database.dao.exceptions.DAOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Javier
 */
public class TagDAOJDBC extends TumblrDAOJDBC<Tag> implements TagDAO
{

    public TagDAOJDBC(TumblrDAOFactory daoFactory) 
    {
        super(daoFactory);
    }

    @Override
    public Tag find(Long tagId) throws DAOException 
    {
        return this.find(SQL_FIND, tagId);
    }

    @Override
    public Tag find(String text) throws DAOException 
    {
        return this.find(SQL_FIND_BY_TEXT, text);
    }

    @Override
    public List<Tag> list() throws DAOException 
    {
        return this.list(SQL_LIST);
    }

    @Override
    public List<Tag> list(Long postId) throws DAOException 
    {
        return this.list(SQL_LIST_BY_POST, postId);
    }

    @Override
    public void create(Tag tag) throws DAOException 
    {
        if(tag == null)
        {
            throw new IllegalArgumentException("Tag cannot be null");
        }
        
        ResultSet generatedKeys = this.create(SQL_CREATE, true, true, tag.getText());
        try 
        {
            tag.setTagId(generatedKeys.getLong(1));
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
    public void update(Tag tag) throws DAOException 
    {
        if(tag == null)
            throw new IllegalArgumentException("Tag cannot be null");
        Object[] values =
        {
            tag.getTagId(),
            tag.getText(),
            tag.getTagId()
        };
        this.update(SQL_UPDATE, true, values);
    }

    @Override
    public void delete(Tag tag) throws DAOException 
    {
        if(tag == null)
            throw new IllegalArgumentException("Tag cannot be null");
        this.delete(SQL_DELETE,true, tag.getTagId());
    }

    @Override
    public void associateToPost(Tag tag, Post post) throws DAOException 
    {
        if(tag == null || post == null)
            throw new IllegalArgumentException("Tag and post cannot be null");
        
        Object[] values = 
        {
            tag.getTagId(),
            post.getPostId()
        };
        this.create(SQL_ASSOCIATE, false, true, values);
    }
    
    @Override
    protected Tag map(ResultSet resultSet) throws SQLException 
    {
        Tag tag = new Tag();
        tag.setTagId(resultSet.getLong("tagId"));
        tag.setText(resultSet.getString("text"));
        return tag;
    }
    
    private static final String SQL_FIND = 
        "SELECT tagId, "
            + "text "
            + "FROM Tag "
            + "WHERE tagId = ?";
    private static final String SQL_FIND_BY_TEXT = 
        "SELECT tagId, "
            + "text "
            + "FROM Tag "
            + "WHERE text = ?";
    private static final String SQL_LIST = 
        "SELECT tagId, "
            + "text "
            + "FROM Tag";
    private static final String SQL_LIST_BY_POST = 
        "SELECT t.tagId, "
            + "t.text "
            + "FROM Tag AS t INNER JOIN PostTag AS pt "
            + "ON t.tagId = pt.tagId "
            + "WHERE pt.postId = ?";
    private static final String SQL_CREATE = 
        "INSERT INTO Tag (text) VALUES (?)";
    private static final String SQL_UPDATE = 
        "UPDATE Tag "
            + "SET tagId = ?, "
            + "text = ? "
            + "WHERE tagId = ?";
    private static final String SQL_DELETE = 
        "DELETE FROM Tag "
            + "WHERE tagId = ?";
    private static final String SQL_ASSOCIATE = 
        "INSERT INTO PostTag("
            + "tagId, "
            + "postId) "
            + "VALUES (?,?)";
            
    
}
