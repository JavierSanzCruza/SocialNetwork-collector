/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.manager;

import es.uam.eps.ir.twittercollector.grid.collector.InteractionsCollectorGrid;
import es.uam.eps.ir.twittercollector.grid.collector.interactionsfilter.InteractionFilterSelector;
import es.uam.eps.ir.twittercollector.grid.collector.stopcondition.StopConditionSelector;
import es.uam.eps.ir.twittercollector.grid.collector.tweetcollector.TweetCollectorSelector;
import es.uam.eps.ir.twittercollector.grid.collector.userexplorer.UserExplorerSelector;
import es.uam.eps.ir.twittercollector.stopcondition.StopCondition;
import es.uam.eps.ir.twittercollector.tweetcollector.TweetCollector;
import es.uam.eps.ir.twittercollector.interactionsfilter.InteractionFilter;
import es.uam.eps.ir.twittercollector.userexplorer.UserExplorer;
import java.util.Set;

/**
 * Configuration for a Twitter collector.
 * @author Javier Sanz-Cruzado Puig
 */
public class NewTwitterConfiguration 
{
    /**
     * The name of the database in which to store the sampling.
     */
    private final String database;
    /**
     * An interaction filter which updates the interactions and selects the possible users to propagate.
     */
    private final InteractionFilter filter;
    /**
     * Collects the different tweets from the API.
     */
    private final TweetCollector collector;
    /**
     * Stop condition for the sampling
     */
    private final StopCondition stop;
    /**
     * Set of seed users for the sampling
     */
    private final Set<Long> seedUsers;
    /**
     * Indicates the order of exploration of the different users
     */
    private final UserExplorer explorer;

    /**
     * Constructor.
     * @param grid The read grid containing the different parameters.
     */
    public NewTwitterConfiguration(InteractionsCollectorGrid grid) 
    {
        this.database = grid.getDatabase();
        this.seedUsers = grid.getSeedUsers();
        
        UserExplorerSelector explorerSel = new UserExplorerSelector();
        this.explorer = explorerSel.select(grid.getExplorerParams());
        
        InteractionFilterSelector interFilterSel = new InteractionFilterSelector();
        this.filter = interFilterSel.select(grid.getFilterParams());
        
        TweetCollectorSelector tweetCollSel = new TweetCollectorSelector();
        this.collector = tweetCollSel.select(grid.getCollectorParams());
        
        StopConditionSelector stopSel = new StopConditionSelector();
        this.stop = stopSel.select(grid.getStopParams());

    }

    /**
     * Gets the interaction filter
     * @return the interactionf filter
     */
    public InteractionFilter getFilter() 
    {
        return filter;
    }

    /**
     * Gets the tweet collector
     * @return the tweet collector
     */
    public TweetCollector getCollector() 
    {
        return collector;
    }

    /**
     * Gets the stop condition
     * @return the stop condition
     */
    public StopCondition getStop() 
    {
        return stop;
    }

    /**
     * Gets the set of seed users
     * @return the set of seed users
     */
    public Set<Long> getSeedUsers() 
    {
        return seedUsers;
    }

    public boolean containsSeedUser(long u)
    {
        return this.seedUsers.contains(u);
    }
    
    /**
     * Gets the exploration protocol
     * @return the exploration protocol
     */
    public UserExplorer getExplorer() 
    {
        return explorer;
    }

    public String getDatabase() {
        return database;
    }
    
    
}
