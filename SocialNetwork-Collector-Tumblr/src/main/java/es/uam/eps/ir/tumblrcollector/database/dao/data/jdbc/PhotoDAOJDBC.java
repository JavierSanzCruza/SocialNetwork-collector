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
import es.uam.eps.ir.tumblrcollector.database.dao.data.PhotoDAO;
import es.uam.eps.ir.tumblrcollector.database.data.Photo;
import es.uam.eps.ir.tumblrcollector.database.data.Post;
import es.uam.eps.ir.database.dao.exceptions.DAOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Javier
 */
public class PhotoDAOJDBC extends TumblrDAOJDBC<Photo> implements PhotoDAO
{

    public PhotoDAOJDBC(TumblrDAOFactory daoFactory) 
    {
        super(daoFactory);
    }  

    @Override
    public Photo find(Long photoId) throws DAOException 
    {
        return this.find(SQL_FIND, photoId); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Photo find(String url) throws DAOException 
    {
        return this.find(SQL_FIND_BY_URL, url);
    }

    @Override
    public List<Photo> list() throws DAOException 
    {
        return this.list(SQL_LIST);
    }

    @Override
    public List<Photo> list(Long postId) throws DAOException 
    {
        return this.list(SQL_LIST_BY_POST, postId); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create(Photo photo) throws DAOException 
    {
        if(photo == null)
        {
            throw new IllegalArgumentException("Photo cannot be null");
        }
        
        Object[] values = {
            photo.getCaption(),
            photo.getWidth(),
            photo.getHeight(),
            photo.getUrl()
        };
        
        ResultSet generatedKeys = this.create(SQL_CREATE, true, true, values);
        try 
        {
            photo.setPhotoId(generatedKeys.getLong(1));
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
    public void update(Photo photo) throws DAOException 
    {
        if(photo == null)
        {
            throw new IllegalArgumentException("Photo cannot be null");
        }
        
        Object[] values = {
            photo.getPhotoId(),
            photo.getCaption(),
            photo.getWidth(),
            photo.getHeight(),
            photo.getUrl(),
            photo.getPhotoId()
        };
        
        this.update(SQL_UPDATE, true, values);
    }

    @Override
    public void delete(Photo photo) throws DAOException 
    {
        if(photo == null)
        {
            throw new IllegalArgumentException("Photo cannot be null");
        }
        
        this.delete(SQL_DELETE, true, photo.getPhotoId());
    }

    @Override
    public void associateToPost(Photo photo, Post post) throws DAOException 
    {
        if(photo == null || post == null)
        {
            throw new IllegalArgumentException("Photo and post cannot be null");
        }
        
        Object[] values = {
            post.getPostId(),
            photo.getPhotoId()
        };
        this.create(SQL_ASSOCIATE, false, true, values);

    }
    
    @Override
    protected Photo map(ResultSet resultSet) throws SQLException 
    {
        Photo photo = new Photo();
        photo.setPhotoId(resultSet.getLong("photoId"));
        photo.setCaption(resultSet.getString("caption"));
        photo.setHeight(resultSet.getInt("height"));
        photo.setWidth(resultSet.getInt("width"));
        photo.setUrl(resultSet.getString("url"));
        return photo;
    }
    
    private static final String SQL_FIND =
            "SELECT photoId, "
            + "caption, "
            + "width, "
            + "height, "
            + "url "
            + "FROM Photo "
            + "WHERE photoId = ?";
    private static final String SQL_FIND_BY_URL = 
            "SELECT photoId, "
            + "caption, "
            + "width, "
            + "height, "
            + "url "
            + "FROM Photo "
            + "WHERE url = ?";
    private static final String SQL_LIST =
            "SELECT photoId, "
            + "caption, "
            + "width, "
            + "height, "
            + "url "
            + "FROM Photo";
    private static final String SQL_LIST_BY_POST =
            "SELECT ph.photoId, "
            + "ph.caption, "
            + "ph.width, "
            + "ph.height, "
            + "ph.url "
            + "FROM Photo ph INNER JOIN PostedPhoto pp"
            + "ON ph.photoId = pp.photoId "
            + "WHERE pp.postId = ?";
    private static final String SQL_CREATE =
            "INSERT INTO Photo("
            + "caption, "
            + "width, "
            + "height, "
            + "url) "
            + "VALUES (?,?,?,?)";
    private static final String SQL_ASSOCIATE = 
            "INSERT INTO PostedPhoto ("
            + "postId, "
            + "photoId) "
            + "VALUES (?,?)";
    private static final String SQL_UPDATE =
            "UPDATE Photo "
            + "SET photoId = ?, "
            + "caption = ?, "
            + "width = ?, "
            + "height = ?, "
            + "url = ? "
            + "WHERE photoId = ?";
    private static final String SQL_DELETE = 
            "DELETE FROM Photo "
            + "WHERE photoId = ?";
    
}
