/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.database.dao.data;

import es.uam.eps.ir.tumblrcollector.database.data.Blog;
import es.uam.eps.ir.database.dao.exceptions.DAOException;
import java.util.List;

/**
 *
 * @author Javier
 */
public interface BlogDAO 
{
    /**
     * Finds a blog in the database, given the identifier of the blog.
     * @param blogId Identifier of the blog
     * @return The blog if exists.
     * @throws DAOException if something fails at database level
     */
    public Blog find(Long blogId) throws DAOException;
    /**
     * Finds a blog in the database, given its name
     * @param name Name of the blog
     * @return The blog if exists.
     * @throws DAOException if something fails at database level.
     */
    public Blog find(String name)throws DAOException;
    /**
     * Finds every blog in the database.
     * @return The list of blogs. An empty list if there is none.
     * @throws DAOException if something fails at database level.
     */
    public List<Blog> list() throws DAOException;
    
    /**
     * Finds every post in a given level of the sampling tree.
     * @param level Level of the sampling tree.
     * @return The list of blogs. An empty list if there is none.
     * @throws DAOException if something fails at database level.
     */
    public List<Blog> list(Integer level) throws DAOException;
    /**
     * Finds every post in a given level of the sampling tree. Blogs cannot be visited yet.
     * @param level Level of the sampling tree.
     * @return The list of not visited blogs in the given level. An empty list if there is none.
     * @throws DAOException if something fails at database level.
     */
    public List<Blog> listNotVisited(Integer level) throws DAOException;
    /**
     * Creates a blog in the database.
     * @param blog The blog to create.
     * @throws DAOException if something fails at database level.
     */
    public void create(Blog blog) throws DAOException;
    /**
     * Updates a blog in the database.
     * @param blog The blog to update.
     * @throws DAOException if something fails at database level.
     */
    public void update(Blog blog) throws DAOException;
    /**
     * Deletes a blog in the database.
     * @param blog The blog to delete.
     * @throws DAOException if something fails at database level.
     */
    public void delete(Blog blog) throws DAOException;
}
