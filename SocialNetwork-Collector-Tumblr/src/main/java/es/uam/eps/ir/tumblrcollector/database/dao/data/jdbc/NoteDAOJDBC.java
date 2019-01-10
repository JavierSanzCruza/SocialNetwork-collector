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
import es.uam.eps.ir.tumblrcollector.database.dao.data.NoteDAO;
import es.uam.eps.ir.tumblrcollector.database.data.Note;
import es.uam.eps.ir.tumblrcollector.util.Direction;
import es.uam.eps.ir.database.dao.exceptions.DAOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
public class NoteDAOJDBC extends TumblrDAOJDBC<Note> implements NoteDAO
{

    public NoteDAOJDBC(TumblrDAOFactory daoFactory) 
    {
        super(daoFactory);
    }    

    @Override
    public Note find(Note note) throws DAOException
    {
        Object[] values = 
        {
            note.getOriginalBlog(),
            note.getInteractingBlog(),
            note.getTimestamp()
        };
        
        return this.find(SQL_FIND, values);
    }
    
    @Override
    public List<Note> list() throws DAOException 
    {
        return this.list(SQL_LIST); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Note> listByPost(Long postId) throws DAOException 
    {
        return this.list(SQL_LIST_BY_POST, postId);
    }

    @Override
    public List<Note> listByBlog(Long blogId, Direction dir) throws DAOException 
    {
        List<Note> list = new ArrayList<>();
        if(dir != Direction.BACKWARD)
        {
            list.addAll(this.list(SQL_LIST_BY_BLOG_FORWARD, blogId));
        }
        if(dir != Direction.FORWARD)
        {
            list.addAll(this.list(SQL_LIST_BY_BLOG_BACKWARD, blogId));
        }
        return list;
    }

    @Override
    public void create(Note note) throws DAOException 
    {
        if(note == null)
        {
            throw new IllegalArgumentException("Note cannot be null");
        }
        Object[] values = {
            note.getOriginalPost(),
            note.getOriginalBlog(),
            note.getType(),
            note.getTimestamp(),
            note.getRebloggingPost(),
            note.getInteractingBlog()
        };
        
        this.create(SQL_CREATE, false, true, values);
    }

    @Override
    public void update(Note note) throws DAOException 
    {
        if(note == null)
        {
            throw new IllegalArgumentException("Note cannot be null");
        }
        
        Object[] values = {
            note.getOriginalPost(),
            note.getOriginalBlog(),
            note.getType(),
            note.getTimestamp(),
            note.getRebloggingPost(),
            note.getInteractingBlog(),
            note.getOriginalBlog(),
            note.getInteractingBlog(),
            note.getTimestamp()
        };
        
        this.update(SQL_UPDATE, true, values);
    }

    @Override
    public void delete(Note note) throws DAOException 
    {
        if(note == null)
        {
            throw new IllegalArgumentException("Note cannot be null");
        }
        
        Object[] values = {
            note.getOriginalBlog(),
            note.getInteractingBlog(),
            note.getTimestamp()
        };
        
        this.delete(SQL_DELETE, true, values);
    }
    
    @Override
    protected Note map(ResultSet resultSet) throws SQLException 
    {
        Note note = new Note();
        
        note.setOriginalPost(resultSet.getLong("originalPost"));
        note.setOriginalBlog(resultSet.getLong("originalBlog"));
        note.setInteractingBlog(resultSet.getLong("interactingBlog"));
        note.setRebloggingPost(resultSet.getLong("rebloggingPost"));
        note.setType(resultSet.getString("type"));
        note.setTimestamp(resultSet.getLong("timestamp"));
        return note;
    }
    
    private static final String SQL_FIND = 
        "SELECT originalPost, "
            + "originalBlog, "
            + "type, "
            + "rebloggingPost, "
            + "interactingBlog, "
            + "timestamp "
            + "FROM Note "
            + "WHERE originalBlog = ? AND "
            + "interactingBlog = ? AND "
            + "timestamp = ?";
    
    private static final String SQL_LIST =
        "SELECT originalPost, "
            + "originalBlog, "
            + "type, "
            + "timestamp, "
            + "rebloggingPost, "
            + "interactingBlog "
            + "FROM Note ";
    private static final String SQL_LIST_BY_POST = 
        "SELECT originalPost, "
            + "originalBlog, "
            + "type, "
            + "timestamp, "
            + "rebloggingPost, "
            + "interactingBlog "
            + "FROM Note "
            + "WHERE originalPost = ?";
    private static final String SQL_LIST_BY_BLOG_BACKWARD = 
        "SELECT originalPost, "
            + "originalBlog, "
            + "type, "
            + "timestamp, "
            + "rebloggingPost, "
            + "interactingBlog "
            + "FROM Note "
            + "WHERE originalBlog = ? "
            + "ORDER BY interactingBlog";
    private static final String SQL_LIST_BY_BLOG_FORWARD = 
            "SELECT originalPost, "
            + "originalBlog, "
            + "type, "
            + "timestamp, "
            + "rebloggingPost, "
            + "interactingBlog "
            + "FROM Note "
            + "WHERE interactingBlog = ? "
            + "ORDER BY originalBlog";
    private static final String SQL_CREATE = 
        "INSERT INTO Note ("
            + "originalPost, "
            + "originalBlog, "
            + "type, "
            + "timestamp, "
            + "rebloggingPost, "
            + "interactingBlog) "
            + "VALUES (?,?,?,?,?,?)";
    private static final String SQL_UPDATE = 
        "UPDATE Note"
            + "SET originalPost = ?, "
            + "originalBlog = ?, "
            + "type = ?, "
            + "timestamp = ?, "
            + "rebloggingPost = ?,"
            + "interactingBlog = ? "
            + "WHERE originalBlog = ? "
            + "AND interactingBlog = ? "
            + "AND timestamp = ?";
    private static final String SQL_DELETE = 
        "DELETE FROM Note "
            + "WHERE originalBlog = ? "
            + "AND interactingBlog = ? "
            + "AND timestamp = ?";
    
}
