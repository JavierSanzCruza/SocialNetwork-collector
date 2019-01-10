/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.database.dao.data;

import es.uam.eps.ir.tumblrcollector.database.data.Photo;
import es.uam.eps.ir.tumblrcollector.database.data.Post;
import es.uam.eps.ir.database.dao.exceptions.DAOException;
import java.util.List;

/**
 *
 * @author Javier
 */
public interface PhotoDAO 
{
    /**
     * Finds a photo in the database by its identifier.
     * @param photoId Photo identifier.
     * @return The photo if exists.
     * @throws DAOException if something fails at database level.
     */
    public Photo find(Long photoId) throws DAOException;
    /**
     * Finds a photo in the database by the URL.
     * @param url Photo URL.
     * @return The photo if exists.
     * @throws DAOException if something fails at database level.
     */
    public Photo find(String url) throws DAOException;
    /**
     * Finds every photo in the database.
     * @return A list with all the photos in the database. If there is none, an empty list will be returned.
     * @throws DAOException if something fails at database level.
     */
    public List<Photo> list() throws DAOException;
    /**
     * Finds every photo in the database posted in a given post.
     * @param postId Identifier of the post.
     * @return The list of photos in that post. If there is none, an empty list will be returned.
     * @throws DAOException if something fails at database level.
     */
    public List<Photo> list(Long postId) throws DAOException;
    /**
     * Creates a photo in the database.
     * @param photo Photo to create.
     * @throws DAOException if something fails at database level.
     */
    public void create(Photo photo) throws DAOException;
    
    /**
     * Updates a photo in the database.
     * @param photo Photo to update.
     * @throws DAOException  if something fails at database level.
     */
    public void update(Photo photo) throws DAOException;
    
    /**
     * Deletes a photo in the database.
     * @param photo Photo to delete.
     * @throws DAOException if something fails at database level.
     */
    public void delete(Photo photo) throws DAOException;
    
    /**
     * Associates a photo to a post.
     * @param photo Photo to associate.
     * @param post Post that contains the photo.
     */
    public void associateToPost(Photo photo, Post post) throws DAOException;
}
