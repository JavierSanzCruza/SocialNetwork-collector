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
import es.uam.eps.ir.tumblrcollector.database.data.Tag;
import es.uam.eps.ir.database.dao.exceptions.DAOException;
import java.util.List;

/**
 *
 * @author Javier
 */
public interface TagDAO 
{
    /**
     * Finds a tag in the database by its identifier.
     * @param tagId Tag identifier.
     * @return The tag if exists.
     * @throws DAOException if something fails at database level.
     */
    public Tag find(Long tagId) throws DAOException;
    /**
     * Finds a tag in the database by its text.
     * @param text Tag text.
     * @return The tag if exists.
     * @throws DAOException if something fails at database level.
     */
    public Tag find(String text) throws DAOException;
    /**
     * Finds a list of all the existing tags.
     * @return A list containing the tags. If there is none, an empty list will be returned.
     * @throws DAOException if something fails at database level.
     */
    public List<Tag> list() throws DAOException;
    /**
     * Finds a list of all the tags for a post.
     * @param postId The post identifier.
     * @return A list containing all the tags. If there is none, an empty list will be returned.
     * @throws DAOException if something fails at database level.
     */
    public List<Tag> list(Long postId) throws DAOException;
    /**
     * Creates a tag in the database.
     * @param tag The tag to create.
     * @throws DAOException if something fails at database level.
     */
    public void create(Tag tag) throws DAOException;
    /**
     * Updates a tag in the database.
     * @param tag The tag to update.
     * @throws DAOException if something fails at database level.
     */
    public void update(Tag tag) throws DAOException;
    /**
     * Removes a tag from the database.
     * @param tag The tag to delete.
     * @throws DAOException if something fails at database level.
     */
    public void delete(Tag tag) throws DAOException;
    /**
     * Associates a tag to a post.
     * @param tag The tag to associate.
     * @param post The post that contains the tag.
     * @throws DAOException if something fails at database level.
     */
    public void associateToPost(Tag tag, Post post) throws DAOException;
}
