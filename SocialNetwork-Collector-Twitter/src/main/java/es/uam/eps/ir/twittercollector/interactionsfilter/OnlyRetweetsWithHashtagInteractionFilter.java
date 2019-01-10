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
import es.uam.eps.ir.twittercollector.database.dao.data.HashtagDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UserInteractionDAO;
import es.uam.eps.ir.twittercollector.database.data.Interaction;
import es.uam.eps.ir.twittercollector.database.data.InteractionType;
import es.uam.eps.ir.twittercollector.database.data.LinkType;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import es.uam.eps.ir.twittercollector.userexplorer.UserExplorer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import twitter4j.Status;

/**
 * Selects as visitable every user obtain via a retweet.
 * @author Javier Sanz-Cruzado Puig
 */
public class OnlyRetweetsWithHashtagInteractionFilter implements InteractionFilter
{
    @Override
    public Set<Long> filter(UserExplorer explorer, List<Interaction> interactions, List<Status> status) 
    {
        Set<Long> users = new HashSet<>();
        try 
        {
            UserInteractionDAO interdao = TwitterDB.getInstance().getUserInteractionDAO();
            HashtagDAO htdao = TwitterDB.getInstance().getHashtagDAO();
            
            
            interactions.forEach(inter ->
            {
                Tweet tw = new Tweet(inter.getTweetId());

                long u = inter.getInteractedUserId();
                if(inter.getInteraction().equals(InteractionType.RETWEET) && !htdao.getAssociatedHashtags(tw).isEmpty())
                {
                    
                    if(explorer.hasBeenVisited(u))
                    {
                        inter.setLinkType(LinkType.COMPLETION_COMPL);
                        interdao.update(inter);
                        //users.add(u);
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
            });
        } 
        catch (TwitterCollectorException ex) 
        {
        }
        
        return users;
    }
}
