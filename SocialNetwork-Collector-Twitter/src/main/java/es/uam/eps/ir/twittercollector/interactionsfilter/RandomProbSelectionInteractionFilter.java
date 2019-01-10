/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.interactionsfilter;

import es.uam.eps.ir.twittercollector.database.TwitterDB;
import es.uam.eps.ir.twittercollector.database.dao.data.UserInteractionDAO;
import es.uam.eps.ir.twittercollector.database.data.Interaction;
import es.uam.eps.ir.twittercollector.database.data.LinkType;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import es.uam.eps.ir.twittercollector.userexplorer.UserExplorer;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.Status;

/**
 * Randomly selects a fixed number of users.
 * @author Javier Sanz-Cruzado Puig
 */
public class RandomProbSelectionInteractionFilter implements InteractionFilter
{
    /**
     * Probability of adding an interaction to the expansion list.
     */
    private final double prob;
    
    /**
     * Random number generator.
     */
    private final Random rng;

    /**
     * Constructor.
     * @param prob probability of selecting the interaction for expansion.
     */
    public RandomProbSelectionInteractionFilter(double prob)
    {
        this.prob = prob;
        this.rng = new Random();
    }
    
    @Override
    public Set<Long> filter(UserExplorer explorer, List<Interaction> interactions, List<Status> status) 
    {
        Set<Long> users = new HashSet<>();

        try 
        {
            UserInteractionDAO interdao = TwitterDB.getInstance().getUserInteractionDAO();
            
            for(Interaction inter : interactions)
            {
                long u = inter.getInteractedUserId();
                if(rng.nextDouble() < prob)
                {
                    if(explorer.hasBeenVisited(u))
                    {
                        inter.setLinkType(LinkType.COMPLETION_COMPL);
                        interdao.update(inter);
                    }
                    else if(explorer.isAboutToBeVisited(u))
                    {
                        inter.setLinkType(LinkType.COMPLETION_EXP);
                        interdao.update(inter);
                        users.add(u);
                    }
                    else
                    {
                        inter.setLinkType(LinkType.EXPANSION);
                        interdao.update(inter);
                        users.add(u);
                    }
                }
                else
                {
                    if(explorer.hasBeenVisited(u) || explorer.isAboutToBeVisited(u))
                    {
                        inter.setLinkType(LinkType.COMPLETION_COMPL);
                        interdao.update(inter);
                    }
                }
            }
        } catch (TwitterCollectorException ex) 
        {
            Logger.getLogger(RandomProbSelectionInteractionFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return users;
    }
    
    
    
    
    
}
