/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.collector;

import es.uam.eps.ir.twittercollector.database.TwitterDB;
import es.uam.eps.ir.twittercollector.database.dao.data.UserDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UserInteractionDAO;
import es.uam.eps.ir.twittercollector.database.data.Interaction;
import es.uam.eps.ir.twittercollector.database.data.User;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import es.uam.eps.ir.twittercollector.manager.NewTwitterConfiguration;
import es.uam.eps.ir.twittercollector.manager.TwitterStore;
import es.uam.eps.ir.twittercollector.stopcondition.StopCondition;
import es.uam.eps.ir.twittercollector.tweetcollector.TweetCollector;
import es.uam.eps.ir.twittercollector.interactionsfilter.CompletionInteractionFilter;
import es.uam.eps.ir.twittercollector.interactionsfilter.InteractionFilter;
import es.uam.eps.ir.twittercollector.userexplorer.UserExplorer;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.Status;

/**
 *
 * @author Javier
 */
public class DatasetCollector 
{
    private boolean initialized = false;
    private NewTwitterConfiguration conf;
    private StopCondition stop;
    private UserExplorer userSel;
    private TweetCollector status;
    private InteractionFilter filter;
    /**
     * Constructor for the dataset collector
     */
    public DatasetCollector()
    {
    }
    
    /**
     * Configures the collector
     * @param conf a configuration (already initialized)
     * @return true if everything went OK, false if not.
     */
    public boolean configure(NewTwitterConfiguration conf)
    {
        this.conf = conf;
        TwitterDB.startDB(conf.getDatabase());
       
        this.stop = this.conf.getStop();
        this.userSel = this.conf.getExplorer();
        this.status = this.conf.getCollector();
        this.filter = this.conf.getFilter();
        
        this.initialized = true;
        return true;
    }
    
    /**
     * Collects data from Twitter
     * @return true if everything went Ok, false otherwise
     * @throws java.io.UnsupportedEncodingException
     */
    public boolean collect() throws UnsupportedEncodingException
    {
        if(!initialized)
            return false;
        
        long initialTimestamp = System.currentTimeMillis();
        try
        {
            TwitterStore ts = new TwitterStore();

            this.userSel.addSelectableUsers(conf.getSeedUsers().stream());
            
            // Users already visited.
            Set<Long> visited = new HashSet<>();
            
            UserDAO userdao = TwitterDB.getInstance().getUserDAO();
            UserInteractionDAO interdao = TwitterDB.getInstance().getUserInteractionDAO();
        
            StatusCollector sc = new StatusCollector();
            
            /* INITIAL PHASE: Retrieve users until the database has conf.getNumMinUsers() users */
            System.out.println("INITIAL PHASE");
            
            while(userSel.hasNextUser() && !stop.stopInitialPhase(visited.size(), userSel.numRemaining(),  userSel.getExplorationLevel(), System.currentTimeMillis() - initialTimestamp))
            {
                // Retrieve the next user to explore
                Long next = userSel.getNextUser();
                if(visited.contains(next))
                {
                    continue;
                }

                // If there is not a next user... (it should never arrive here)
                if(next == null)
                {
                    break;
                }

                /* Set the node as visited */
                System.out.println("User: " + next);
                visited.add(next);
                
                boolean cont = false;
                
                User usC = userdao.find(next);
                if(usC != null && usC.isVisited()) cont = true;
                
                if(cont) // If the user has already been visited, obtain the following nodes (allow for stops).
                {
                    List<Interaction> lsInter = interdao.listExpansion(next);
                    Set<Long> current = new HashSet<>();
                    
                    for(Interaction inter : lsInter)
                    {
                        long userId = inter.getInteractedUserId();
                        if(!current.contains(userId))
                        {
                            current.add(userId);
                        }
                    }
                    
                    userSel.addSelectableUsers(current.stream());
                    continue;
                }
                
                // In case the user has not been visited yet, obtain the corresponding tweets.
                List<Status> statuses = this.status.getTimeline(next);
                
                // Store the tweets in the database.
                if(statuses != null)
                {
                    for(Status st : statuses)
                    {
                        if(status.equals(statuses.get(0)))
                        {
                            ts.storeTweet(st, true, true, true);
                        }
                        else
                        {
                            ts.storeTweet(st, true);
                        }
                    }
                }
                
                List<Interaction> interactions = TwitterDB.getInstance().getUserInteractionDAO().list(next);
                Set<Long> filteredusers = this.filter.filter(userSel, interactions, statuses);
                this.userSel.addSelectableUsers(filteredusers.stream());

                if(usC != null && !cont)
                {
                    userdao.setVisited(usC, userSel.getExplorationLevel());
                }
                
                usC = userdao.find(next);
                if(usC != null) userdao.setVisited(usC, userSel.getExplorationLevel());
            }
            
            
            // COMPLETION PHASE: Complete the graph, finish the sample.
            System.out.println("COMPLETION PHASE: Number of users already visited: " + this.userSel.numVisited() + ". Remaining: " + this.userSel.numRemaining());
            while(this.userSel.numRemaining() > 0)
            {
                // Retrieve the next user to explore
                Long next = userSel.getNextUser();
                if(visited.contains(next))
                {
                    continue;
                }

                // If there is not a next user... (it should never arrive here)
                if(next == null)
                {
                    break;
                }
                
                System.out.println("User: " + next);

                
                boolean cont = false;
                
                User usC = userdao.find(next);
                if(usC != null && usC.isVisited()) cont = true;
                
                if(cont)
                {
                    continue;
                }
                
                // In case the user has not been visited yet, obtain the corresponding tweets.
                List<Status> statuses = this.status.getTimeline(next);
                
                // Store the tweets in the database.
                if(statuses != null)
                {
                    for(Status st : statuses)
                    {
                        if(status.equals(statuses.get(0)))
                        {
                            ts.storeTweet(st, true, true, true);
                        }
                        else
                        {
                            ts.storeTweet(st, true);
                        }
                    }
                }
                
                if(usC != null) userdao.setVisited(usC, userSel.getExplorationLevel());

                List<Interaction> interactions = TwitterDB.getInstance().getUserInteractionDAO().list(next);
                InteractionFilter aux = new CompletionInteractionFilter();
                Set<Long> filteredusers = this.filter.filter(userSel, interactions, statuses);
            }
            
            
            
            
            return true;
        } 
        catch (TwitterCollectorException ex) 
        {
            System.err.println("Something failed while retrieving the tweets");
            Logger.getLogger(DatasetCollector.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
    }
}
