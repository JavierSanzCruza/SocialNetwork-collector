/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.database.dao;

import es.uam.eps.ir.tumblrcollector.database.dao.TumblrDAOFactory;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class TumblrDAOHandler 
{
    /**
     * Set of DAOFactory identified by the database name.
     */
    private HashMap<String, TumblrDAOFactory> factories;
    /**
     * Instance of the handler.
     */
    private static TumblrDAOHandler instance = null;
    
    /**
     * Gets an instance of the DAOHandler. Only one for program. If an instance does not exists,
     * this method creates one.
     * @return A previously created instance if exists, a new one if not.
     */
    public static TumblrDAOHandler getInstance()
    {
        if(instance == null)
        {
            instance = new TumblrDAOHandler();
        }
        return instance;
    }
   
    private TumblrDAOHandler()
    {
        factories = new HashMap<>();
    }
    
    /**
     * Adds a DAOFactory to the DAOHandler.
     * @param database Database name as established in dao.properties file.
     * @return true if the factory was correctly added to the handler, false if not (already exists).
     */
    public boolean addFactory(String database)
    {
        if(this.factories.containsKey(database))
        {
            return false;
        }
      
        this.factories.put(database, TumblrDAOFactory.getInstance(database));   
        return true;
    }
    
    /**
     * Obtains a DAOFactory from the handler.
     * @param database Database to recover.
     * @return The DAOFactory or null in case it does not exist.
     */
    public TumblrDAOFactory getFactory(String database)
    {
        return this.factories.get(database);
    }
    
    /**
     * Closes every connection in the DAOFactories.
     */
    public void close()
    {
        for(TumblrDAOFactory fact : this.factories.values())
        {
            try 
            {
                fact.close();
            } 
            catch (SQLException ex) 
            {
                Logger.getLogger(TumblrDAOHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
