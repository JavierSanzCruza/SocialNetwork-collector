/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.manager;

import com.tumblr.jumblr.JumblrClient;

/**
 * Class that manages the Tumblr Crawler.
 * @author Javier
 */
public class TumblrManager 
{
    private JumblrClient tumblr;
    private static TumblrManager instance = null;
    private String database;
    
    /**
     * Constructor. Generates a new client to connect with Tumblr.
     */
    private TumblrManager()
    {
        this.tumblr = new JumblrClient("key1", "key2");
        this.tumblr.setToken
        (
            "token1",
            "token2"
        );
    }
    
    /**
     * Gets an instance of TumblrManager.
     * @return The new instance.
     */
    public static TumblrManager getInstance()
    {
        if(instance == null)
            instance = new TumblrManager();
        return instance;
    }
    
    /**
     * Obtains the Tumblr client.
     * @return The client.
     */
    public JumblrClient getTumblr()
    {
        return tumblr;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
    
    public static void resetTumblr()
    {
        instance = new TumblrManager();
    }
    
}
