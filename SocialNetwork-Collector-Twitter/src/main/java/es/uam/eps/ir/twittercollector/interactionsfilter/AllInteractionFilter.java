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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.Status;

/**
 * Selects as visitable every user obtain via a retweet.
 * @author Javier Sanz-Cruzado Puig
 */
public class AllInteractionFilter implements InteractionFilter
{
    @Override
    public Set<Long> filter(UserExplorer explorer, List<Interaction> interactions, List<Status> status)
    {
        Set<Long> users = new HashSet<>();
        interactions.stream().forEach(inter -> 
        {
            try 
            {
                long u = inter.getInteractedUserId();
                UserInteractionDAO interdao = TwitterDB.getInstance().getUserInteractionDAO();
                
                if(explorer.hasBeenVisited(u)) // If the user has already been visited, then, not explore it again.
                {
                    inter.setLinkType(LinkType.COMPLETION_COMPL);
                }
                else if(explorer.isAboutToBeVisited(u)) // If it has not been explored, but it is already known
                {
                    inter.setLinkType(LinkType.COMPLETION_EXP);
                    users.add(u);
                }
                else // Otherwise.
                {
                    inter.setLinkType(LinkType.EXPANSION);
                    users.add(u);
                }
                interdao.update(inter);               
            } 
            catch (TwitterCollectorException ex) 
            {
                Logger.getLogger(AllInteractionFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        return users;
    }
}
