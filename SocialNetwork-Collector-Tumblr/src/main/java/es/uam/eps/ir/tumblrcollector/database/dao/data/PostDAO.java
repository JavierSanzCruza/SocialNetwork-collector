/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.database.dao.data;

import es.uam.eps.ir.tumblrcollector.database.data.Post;
import es.uam.eps.ir.tumblrcollector.database.data.TumblrType;
import es.uam.eps.ir.database.dao.exceptions.DAOException;
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
public interface PostDAO 
{
    /**
     * Finds a determined post in the database.
     * @param id Identifier of the post.
     * @return The post.
     * @throws DAOException If something fails at database level. 
     */
    public Post find(Long id) throws DAOException;
    /**
     * Finds every post in the database.
     * @return A list of posts. The list will be empty if there are no posts in the database.
     * @throws DAOException If something fails at database level.
     */
    public List<Post> list() throws DAOException;
    /**
     * Finds every post published in a determined blog.
     * @param blogId Identifier of the blog.
     * @return A list of posts published in the given blog. If no posts are found, the list will be empty.
     * @throws DAOException If something fails at database level.
     */
    public List<Post> list(Long blogId) throws DAOException;
    /**
     * Finds every post of a determined type.
     * @param type Type to recover.
     * @return A list of posts. If no posts are found, the list will be empty.
     * @throws DAOException  If something fails at database level.
     */
    public List<Post> list(TumblrType type) throws DAOException;
    
    /**
     * Finds every post published in a determined blog, filtered by the type of the post.
     * @param blogId Identifier of the blog.
     * @param type Type to recover.
     * @return A list of post fulfilling the previous conditions. If no posts are found, the list will be empty.
     * @throws DAOException If something fails at database level.
     */
    public List<Post> list(Long blogId, TumblrType type) throws DAOException;
    
    /**
     * Creates a post in the database.
     * @param post Post to create.
     * @throws DAOException If something fails at database level.
     */
    public void create(Post post) throws DAOException;
    
    /**
     * Updates a post in the database.
     * @param post Post to update.
     * @throws DAOException if something fails at database level.
     */
    public void update(Post post) throws DAOException;
    
    /**
     * Delete a post in the database
     * @param post Post to delete.
     * @throws DAOException if something fails at database level.
     */
    public void delete(Post post) throws DAOException;
    
    /**
     * Obtains a list of reblogs of a given post.
     * @param postId Original post.
     * @return The list of posts that reblog the corresponding post. If post is not a reblog, or no reblogs are found in the database,
     * the list will be empty.
     * @throws DAOException if something fails at database level. 
     */
    public List<Post> listReblogs(Long postId) throws DAOException;
}
