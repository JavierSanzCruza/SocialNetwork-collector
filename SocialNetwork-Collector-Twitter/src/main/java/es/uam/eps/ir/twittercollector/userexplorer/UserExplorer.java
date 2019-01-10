/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.userexplorer;

import java.util.stream.Stream;

/**
 * Interface for user selectors. A user selector establishes the order of retrieving
 * users in a crawling.
 * @author Javier Sanz-Cruzado Puig
 */
public interface UserExplorer 
{
    /**
     * Obtains the next user to propagate.
     * @return the next user if exists, null if not.
     */
    public Long getNextUser();
    
    /**
     * Adds a group of selectable users to the crawling.
     * @param users the set of users.
     * @return boolean true if the users are added correctly, false if they are not. 
     */
    public boolean addSelectableUsers(Stream<Long> users);

    /**
     * Indicates if there is a next user to propagate.
     * @return true if it the next user exists, false if not.
     */
    public boolean hasNextUser();
    
    /**
     * For user crawling methodologies which maintain several levels, obtains the current one.
     * Otherwise, it returns 0;
     * @return the current crawling level.
     */
    public int getExplorationLevel();
    
    /**
     * Checks if the user is going to be visited during the crawling or not.
     * @param user the user to check.
     * @return true if the user is about to be visited, false if it is not.
     */
    public boolean isAboutToBeVisited(long user);
    /**
     * Checks if the user has been already visited or not.
     * @param user the user to check.
     * @return true if the user has been visited, false if it is not.
     */
    public boolean hasBeenVisited(long user);
    
    /**
     * Obtains how many visitable users have not been explored yet.
     * @return the number of visitable users which have not been explored yet.
     */
    public int numRemaining();
    
    /**
     * Obtains how many users have been visited
     * @return the number of users which have been visited.
     */
    public int numVisited();
}
