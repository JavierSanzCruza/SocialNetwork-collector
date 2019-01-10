/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.interactionsfilter;

import es.uam.eps.ir.twittercollector.database.data.Interaction;
import es.uam.eps.ir.twittercollector.userexplorer.UserExplorer;
import java.util.List;
import java.util.Set;
import twitter4j.Status;

/**
 * Interface for the different mechanisms for updating the interactions in the
 * database, as well as selecting the set of users which can be visited.
 * @author Javier Sanz-Cruzado Puig
 */
public interface InteractionFilter 
{
    /**
     * Updates the interactions of a user and selects the set of users which can be visited
     * Filters the collection of tweets.
     * @param explorer the exploration protocol.
     * @param interactions the set of interactions.
     * @param status the list of tweets.
     * @return the filtered interactions
     */
    public Set<Long> filter(UserExplorer explorer, List<Interaction> interactions, List<Status> status);
}
