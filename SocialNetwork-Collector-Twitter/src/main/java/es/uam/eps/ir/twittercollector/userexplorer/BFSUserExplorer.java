/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.userexplorer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Breadth.first search user sampler.
 * @author Javier Sanz-Cruzado Puig
 */
public class BFSUserExplorer implements UserExplorer
{
    private final Set<Long> visited;
    /**
     * Current queue
     */
    private Queue<Long> queue;
    /**
     * Next queue
     */
    private Queue<Long> nextQueue;
    /**
     * Current exploration level
     */
    private int level;
    
    /**
     * Constructor.
     */
    public BFSUserExplorer()
    {
        queue = new LinkedList<>();
        nextQueue = new LinkedList<>();
        visited = new HashSet<>();
        level = 1;
    }
    
    @Override
    public Long getNextUser() 
    {
        if(queue.isEmpty() && nextQueue.isEmpty())
        {
            return null;
        }
        else if(queue.isEmpty())
        {
            queue = nextQueue;
            nextQueue = new LinkedList<>();
            level++;
        }
        
        long user = queue.poll();
        this.visited.add(user);
        return user;
    }

    @Override
    public boolean addSelectableUsers(Stream<Long> users) 
    {
        users.forEach(u -> 
        {
            if(!this.visited.contains(u) && !this.queue.contains(u) && !this.nextQueue.contains(u))
            {
                this.nextQueue.add(u);
            }
        });
        return true;
    }

    @Override
    public boolean hasNextUser() 
    {
        return !(queue.isEmpty() && nextQueue.isEmpty());
    }

    @Override
    public int getExplorationLevel() 
    {
        return this.level;
    }

    @Override
    public boolean isAboutToBeVisited(long user) 
    {
        return queue.contains(user) || nextQueue.contains(user);
    }

    @Override
    public boolean hasBeenVisited(long user) 
    {
        return this.visited.contains(user);
    }

    @Override
    public int numRemaining() 
    {
        return queue.size() + nextQueue.size();
    }

    @Override
    public int numVisited() 
    {
        return this.visited.size();
    }
    
    
}
