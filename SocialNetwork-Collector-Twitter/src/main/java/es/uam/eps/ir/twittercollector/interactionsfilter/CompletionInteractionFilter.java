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
import twitter4j.Status;

/**
 * Interaction filter for performing the closing of the graph.
 * @author Javier Sanz-Cruzado Puig
 */
public class CompletionInteractionFilter implements InteractionFilter
{

    @Override
    public Set<Long> filter(UserExplorer explorer, List<Interaction> interactions, List<Status> status) 
    {
        Set<Long> set = new HashSet<>();

        try 
        {
            UserInteractionDAO interdao = TwitterDB.getInstance().getUserInteractionDAO();
            interactions.forEach(inter ->
            {
                long u = inter.getInteractedUserId();
                if(explorer.hasBeenVisited(u) || explorer.isAboutToBeVisited(u))
                {
                    inter.setLinkType(LinkType.COMPLETION_COMPL);
                    interdao.update(inter);
                }
            });
        } 
        catch (TwitterCollectorException ex) 
        {
        }
    
        return set;
    }
    
}
