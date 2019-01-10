/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.database;

import es.uam.eps.ir.twittercollector.database.dao.data.UsersRetweetDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.TweetDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UserDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.MediaDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.HashtagDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.TwitterKeyDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UrlDAO;
import es.uam.eps.ir.twittercollector.database.dao.*;
import es.uam.eps.ir.twittercollector.database.dao.data.UserInteractionDAO;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import es.uam.eps.ir.twittercollector.exception.TwitterDatabaseUninitializedException;

/**
 * Class that represents the communication between the program and the database
 * @author Javier Sanz-Cruzado
 */
public class TwitterDB 
{
    /**
     * Instance of the class
     */
    private static TwitterDB instance = null;
    /*
     * Interfaces
     */
    private final TwitterDAOFactory daoFactory;
    
    
    public static void startDB(String database)
    {
        instance = new TwitterDB(database);
    }
    
    /**
     * Gets an instance
     * @return A new instance of TwitterDB if it did not exist previously, a previously created instance in other case
     * @throws A TwitterDatabaseUninitializedException if it is called previously to a initialization of the database
     */
    public static TwitterDB getInstance() throws TwitterCollectorException
    {
        if(instance == null)
        {
            throw new TwitterDatabaseUninitializedException();
        }
        
        return instance;
    }
    
    private TwitterDB(String database)
    {
        TwitterDAOHandler dh = TwitterDAOHandler.getInstance();
        dh.addFactory(database);
        this.daoFactory = dh.getFactory(database);
    }

    public UserDAO getUserDAO() {
        return daoFactory.getUserDAO();
    }

    public TweetDAO getTweetDAO() {
        return daoFactory.getTweetDAO();
    }

    public HashtagDAO getHashtagDAO() {
        return daoFactory.getHashtagDAO();
    }

    public UrlDAO getUrlDAO() {
        return daoFactory.getUrlDAO();
    }

    public MediaDAO getMediaDAO() {
        return daoFactory.getMediaDAO();
    }
    
    public UsersRetweetDAO getUsersRTDAO()
    {
        return daoFactory.getUsersRetweetDAO();
    }
    public TwitterKeyDAO getTwitterKeyDAO() {
        return daoFactory.getTwitterKeyDAO();
    }
    
    public UserInteractionDAO getUserInteractionDAO() {
        return daoFactory.getUserInteractionDAO();
    }
    
}
