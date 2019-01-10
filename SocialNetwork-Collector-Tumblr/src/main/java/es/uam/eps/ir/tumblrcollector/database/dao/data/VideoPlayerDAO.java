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
import es.uam.eps.ir.tumblrcollector.database.data.VideoPlayer;
import es.uam.eps.ir.database.dao.exceptions.DAOException;
import java.util.List;

/**
 *
 * @author Javier
 */
public interface VideoPlayerDAO 
{
    /**
     * Finds a video player in the database.
     * @param videoId Identifier of the video player.
     * @return The video player if exists.
     * @throws DAOException if there is a problem at database level.
     */
    public VideoPlayer find(Long videoId) throws DAOException;
    /**
     * Finds a video player in the database by the embed code.
     * @param embed Embedded code for the player.
     * @return The video player if exists.
     * @throws DAOException if there is a problem at database level.
     */
    public VideoPlayer find(String embed) throws DAOException;
    /**
     * Finds a list of all the existing video players at the database.
     * @return A list of VideoPlayers. If there is none, an empty list will be returned.
     * @throws DAOException if there is a problem at database level.
     */
    public List<VideoPlayer> list() throws DAOException;
    /**
     * Finds a list of video players existing at one post.
     * @param postId Post identifier.
     * @return A list of video players. If there is none satisfying the condition, an empty list will be returned. 
     * @throws DAOException if there is a problem at database level.
     */
    public List<VideoPlayer> list(Long postId) throws DAOException;
    /**
     * Creates a video player at the database.
     * @param player Video player to create.
     * @throws DAOException if something fails at database level.
     */
    public void create(VideoPlayer player) throws DAOException;
    /**
     * Updates a video player at the database.
     * @param player Video player to create.
     * @throws DAOException if something fails at database level.
     */
    public void update(VideoPlayer player) throws DAOException;
    /**
     * Removes a video player from the database.
     * @param player Video player to create.
     * @throws DAOException if something fails at database level.
     */
    public void delete(VideoPlayer player) throws DAOException;
    /**
     * Associates a video player to a post.
     * @param player Video player to associate.
     * @param post Post that contains the player.
     * @throws DAOException if something fails at database level.
     */
    public void associateToPost(VideoPlayer player, Post post) throws DAOException;
}
