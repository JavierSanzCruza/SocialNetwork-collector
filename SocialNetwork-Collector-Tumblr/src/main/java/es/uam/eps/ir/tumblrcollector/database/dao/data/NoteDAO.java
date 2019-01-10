/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.database.dao.data;

import es.uam.eps.ir.tumblrcollector.util.Direction;
import es.uam.eps.ir.tumblrcollector.database.data.Note;
import es.uam.eps.ir.database.dao.exceptions.DAOException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Javier
 */
public interface NoteDAO 
{
    /**
     * Finds a note in the database.
     * @param note Note to find.
     * @return The note if exists, null if not.
     * @throws DAOException if something fails at database level
     */
    public Note find(Note note) throws DAOException;

    /**
     * Finds a list of all the notes in the database.
     * @return The list of notes. If there is none, an empty list is returned.
     * @throws DAOException if something fails at database level.
     */
    public List<Note> list() throws DAOException;
    /**
     * Finds a list of all the notes for a given post.
     * @param postId Post identifier.
     * @return The list of notes. If there is none, an empty list is returned.
     * @throws DAOException if something fails at database level.
     */
    public List<Note> listByPost(Long postId) throws DAOException;
    /**
     * Finds a list of all the notes created by a post.
     * @param blogId Blog identifier.
     * @param dir The direction of the notes.
     * @return The list of notes. If there is none, an empty list is returned.
     * @throws DAOException if something fails at database level.
     */
    public List<Note> listByBlog(Long blogId, Direction dir) throws DAOException;
    
    /**
     * Creates a note in the database.
     * @param note The note to create.
     * @throws DAOException if something fails at database level.
     */
    public void create(Note note) throws DAOException;
    /**
     * Updates a note in the database.
     * @param note The note to update.
     * @throws DAOException if something fails at database level.
     */
    public void update(Note note) throws DAOException;
    
    /**
     * Removes a note from the database.
     * @param note The note to delete.
     * @throws DAOException if something fails at database level.
     */
    public void delete(Note note)throws DAOException;
            
}
