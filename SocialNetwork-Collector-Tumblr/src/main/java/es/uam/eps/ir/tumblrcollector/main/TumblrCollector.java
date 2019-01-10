/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.main;

import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.types.Post;
import es.uam.eps.ir.tumblrcollector.collector.PostCollector;
import es.uam.eps.ir.tumblrcollector.database.dao.TumblrDAOFactory;
import es.uam.eps.ir.tumblrcollector.database.dao.TumblrDAOHandler;
import es.uam.eps.ir.tumblrcollector.database.data.Blog;
import es.uam.eps.ir.tumblrcollector.database.data.LinkType;
import es.uam.eps.ir.tumblrcollector.database.data.Note;
import es.uam.eps.ir.tumblrcollector.database.data.Tree;
import es.uam.eps.ir.tumblrcollector.exception.TumblrConfigurationErrorException;
import es.uam.eps.ir.tumblrcollector.manager.BlogStore;
import es.uam.eps.ir.tumblrcollector.manager.PostStore;
import es.uam.eps.ir.tumblrcollector.manager.TumblrConfiguration;
import es.uam.eps.ir.tumblrcollector.manager.TumblrManager;
import es.uam.eps.ir.tumblrcollector.manager.TumblrProcessing;
import es.uam.eps.ir.tumblrcollector.util.Direction;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scribe.exceptions.OAuthConnectionException;

/**
 * Tumblr Crawler
 * @author Javier
 */
public class TumblrCollector 
{
    /**
     * Collects data from Tumblr and stores it in a database prepared for it.
     * @param args Configuration file.
     * @throws TumblrConfigurationErrorException if the configuration file is invalid.
     */
    public static void main(String args[]) throws TumblrConfigurationErrorException
    {
        if(args.length < 1)
        {
            System.out.println("Usage: program <confFile>");
            return;
        }
        
        //Configuring the sampling
        TumblrConfiguration tc = new TumblrConfiguration();
        tc.configure(args[0]);
        
        // Getting the manager
        TumblrManager tm = TumblrManager.getInstance();
        
        // Configuring the database
        TumblrDAOHandler tdh = TumblrDAOHandler.getInstance();
        tdh.addFactory(tc.getDatabase());
        TumblrDAOFactory tf = tdh.getFactory(tc.getDatabase());
        tm.setDatabase(tc.getDatabase());
        // Get the store
        PostStore ps = new PostStore();
        BlogStore bs = new BlogStore();
        PostCollector pc = new PostCollector();
        String currentBlog = tc.getSeedUser();
        
        LinkedList<String> currentQueue = new LinkedList<>();
        currentQueue.add(currentBlog);
        LinkedList<String> nextQueue = new LinkedList<>();
        Integer level = 1;
        
        Set<String> toVisit = new HashSet<>();
        toVisit.add(currentBlog);
        
        HashMap<String, Boolean> visited = new HashMap<>();
        
        System.out.println("INITIAL PHASE. Goal: " + tc.getNumMinUsers() + " blogs");
        while((!currentQueue.isEmpty() || !nextQueue.isEmpty()) && toVisit.size() < tc.getNumMinUsers())
        {
            String blog = null;
            do
            {
                blog = currentQueue.pollFirst();
                if(blog != null)
                {
                    currentBlog = blog;
                }
            }
            while(visited.containsKey(blog) && currentQueue.isEmpty() == false);
            
            if(blog == null || visited.containsKey(blog)) // No blog has been retrieved, so the currentQueue must be empty
            {
                if(nextQueue.isEmpty())
                {
                    blog = null;
                }
                else
                {
                    currentQueue = nextQueue;
                    nextQueue = new LinkedList<>();
                    ++level;
                    continue;
                }
            }
            
            if(blog == null)
            {
                break;
            }
            
            /* Set the node as visited */
            System.out.println(currentBlog);
            Boolean cont = false;
            Blog b;
            if(!currentBlog.equals(tc.getSeedUser())) 
            {
                b = tf.getBlogDAO().find(currentBlog);
                if(b.getIsVisited()) //The blog has been previously visited
                {
                    cont = true;
                    visited.put(currentBlog, Boolean.TRUE);
                } 
                else //Store info related to the current blog
                {
                    try
                    {
                        com.tumblr.jumblr.types.Blog blogInfo = tm.getTumblr().blogInfo(currentBlog);
                        b = bs.storeBlog(blogInfo, true, level);
                        visited.put(currentBlog, Boolean.TRUE);
                    }
                    catch(JumblrException je)
                    {
                        cont = true;
                    }
                }
            }
            else //Store info related to first blog
            {
                b = tf.getBlogDAO().find(currentBlog);
                if(b != null)
                {
                    cont = true;
                }
                else
                {
                    com.tumblr.jumblr.types.Blog blogInfo = tm.getTumblr().blogInfo(currentBlog);
                    b = bs.storeBlog(blogInfo, false, level);
                }
                visited.put(currentBlog, Boolean.TRUE);
            }
            
            if(cont == true) //If the blog has been previously visited
            {
                List<Tree> listTree;
                Long currentBl = null;
                listTree = tf.getTreeDAO().listExpansion(b.getBlogId(), tc.getDirection());
                for(Tree tree : listTree)
                {
                    if(currentBl == null || !currentBl.equals(tree.getChild()))
                    {
                        currentBl = tree.getChild();
                        Blog bl = tf.getBlogDAO().find(currentBl);
                        nextQueue.add(bl.getName());
                        toVisit.add(bl.getName());
                    }
                }
                continue;
            }
           
            //Retrieve the posts for each user
            List<Post> posts;
            if(tc.getProcessing().equals(TumblrProcessing.TEMPORAL))
            {
                posts = pc.getTimeLine(b.getName(), tc.getStartDate(), tc.getEndDate());
            }
            else if(tc.getProcessing().equals(TumblrProcessing.NUMPOSTS))
            {
                posts = pc.getTimeLine(b.getName(), tc.getNumMaxPosts());
            }
            else //if (tc.getProcessing().equals(TumblrProcessing.MIXED))
            {
                posts = pc.getTimeLine(b.getName(), tc.getNumMaxPosts(), tc.getStartDate(), tc.getEndDate());
            }
       
            final Blog finalBlog = b;
            posts.parallelStream().forEach((p) -> {
                try {
                    ps.storePost(p, finalBlog.getBlogId());
                } catch (InterruptedException ex) {
                    Logger.getLogger(TumblrCollector.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        
            bs.setVisited(b);
                        
            // Retrieve the interaction
            List<Note> backwardNotes = null;
            List<Note> forwardNotes = null;

            if(!tc.getDirection().equals(Direction.BACKWARD))
            {
                forwardNotes = tf.getNoteDAO().listByBlog(b.getBlogId(), Direction.FORWARD);
            }
            if(!tc.getDirection().equals(Direction.FORWARD))
            {
                backwardNotes = tf.getNoteDAO().listByBlog(b.getBlogId(), Direction.BACKWARD);
            }
            
            // CREATING THE HASHMAP OF INTERACTIONS
            HashMap<Long, Direction> interactions = new HashMap<>();
            Long currentBl = null;
            if(forwardNotes != null)
            {
                for(Note note : forwardNotes)
                {
                    if(currentBl == null || !note.getOriginalBlog().equals(currentBl))
                    {
                        currentBl = note.getOriginalBlog();
                        interactions.put(currentBl, Direction.FORWARD);
                    }
                }
            }
            if(backwardNotes != null)
            {
                currentBl = null;
                for(Note note : backwardNotes)
                {
                    if(currentBl == null || !note.getInteractingBlog().equals(currentBl))
                    {
                        currentBl = note.getInteractingBlog();
                        if(interactions.containsKey(currentBl))
                            interactions.put(currentBl, Direction.BOTH);
                        else
                            interactions.put(currentBl, Direction.BACKWARD);
                    }
                }
            }
            
            if(tc.getNumNeighSBS() == 0 || interactions.keySet().size() < tc.getNumNeighSBS())
            {
                LinkType currentType;
                for(Long key : interactions.keySet())
                {
                    Blog bl = tf.getBlogDAO().find(key);
                    nextQueue.add(bl.getName());
                    if(!toVisit.contains(bl.getName()))
                    {
                        currentType = LinkType.EXPANSION;
                    }
                    else
                    {
                        currentType = LinkType.COMPLETION_EXP;
                    }
                    toVisit.add(bl.getName());
                    tf.getTreeDAO().create(b.getBlogId(), key, currentType, interactions.get(key));
                }
            }
            else // if (tc.getNumNeighSBS() > interactions.keySet().size()) //There is a limit in the number of neighbours
            {
                LinkType currentType;
                List<Long> ls = new ArrayList<>(interactions.keySet());
                for(int i = 0; i < tc.getNumNeighSBS(); ++i)
                {
                    Random r = new Random();
                    Integer next = r.nextInt(ls.size());
                    Blog bl = tf.getBlogDAO().find(ls.get(next));
                    nextQueue.add(bl.getName());
                    if(!toVisit.contains(bl.getName()))
                    {
                        currentType = LinkType.EXPANSION;
                    }
                    else
                    {
                        currentType = LinkType.COMPLETION_EXP;
                    }
                    toVisit.add(bl.getName());

                    tf.getTreeDAO().create(b.getBlogId(), bl.getBlogId(), currentType, interactions.get(bl.getBlogId()));
                    ls.remove(next.intValue());
                }
                
                for(Long l : ls)
                {
                    Blog bl = tf.getBlogDAO().find(l);
                    if(toVisit.contains(bl.getName()))
                    {
                        currentType = LinkType.COMPLETION_COMPL;
                    }
                    else
                    {
                        currentType = LinkType.NONE;
                    }
                    tf.getTreeDAO().create(b.getBlogId(), bl.getBlogId(), currentType, interactions.get(bl.getBlogId()));
                }
            }
        }
        
        System.out.println("COMPLETION PHASE: Number of blogs already visited: " + visited.size() + ". Remaining: " + (toVisit.size()-visited.size()));
        LinkedList<String> completionQueue = new LinkedList<>();
        completionQueue.addAll(currentQueue);
        completionQueue.addAll(nextQueue);
        for(String blog : completionQueue )
        {
            if(visited.containsKey(blog))
                continue;
            System.out.println(blog);
            
            /* If the blog has not been visited, the, set it visited at the corresponding level */
            
            Blog b = tf.getBlogDAO().find(blog);
            if(b.getIsVisited()) //The blog has been previously visited
            {
                visited.put(blog, Boolean.TRUE);
                continue;
            } 
            else //Store info related to the current blog
            {
                try
                {
                    com.tumblr.jumblr.types.Blog blogInfo = tm.getTumblr().blogInfo(blog);
                    if(currentQueue.contains(blog))
                        b = bs.storeBlog(blogInfo, true, level);
                    else //if(nextQueue.contains(blog))
                        b = bs.storeBlog(blogInfo, true, level+1);
                    visited.put(blog, Boolean.TRUE);
                }
                catch(JumblrException je) //ERROR: Blog not found --> The blog has disappeared since collected
                {
                    continue;
                }
                catch(OAuthConnectionException ste)
                {
                    try {
                        System.out.println("ERROR: Connection Failed at main. Waiting 10 seconds and retrying");
                        Thread.sleep(10);
                        continue;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TumblrCollector.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            //Retrieve the posts for each blog
            List<Post> posts;
            if(tc.getProcessing().equals(TumblrProcessing.TEMPORAL))
            {
                posts = pc.getTimeLine(b.getName(), tc.getStartDate(), tc.getEndDate());
            }
            else if(tc.getProcessing().equals(TumblrProcessing.NUMPOSTS))
            {
                posts = pc.getTimeLine(b.getName(), tc.getNumMaxPosts());
            }
            else //if (tc.getProcessing().equals(TumblrProcessing.MIXED))
            {
                posts = pc.getTimeLine(b.getName(), tc.getNumMaxPosts(), tc.getStartDate(), tc.getEndDate());
            }
            
            //Store the posts and their correspondent extra resources
            final Blog finalBlog = b;
            posts.parallelStream().forEach((p) -> {
                try {
                    ps.storePost(p, finalBlog.getBlogId());
                } catch (InterruptedException ex) {
                    Logger.getLogger(TumblrCollector.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            // Retrieve the interaction
            List<Note> backwardNotes = null;
            List<Note> forwardNotes = null;

            if(!tc.getDirection().equals(Direction.BACKWARD))
            {
                forwardNotes = tf.getNoteDAO().listByBlog(b.getBlogId(), Direction.FORWARD);
            }
            if(!tc.getDirection().equals(Direction.FORWARD))
            {
                backwardNotes = tf.getNoteDAO().listByBlog(b.getBlogId(), Direction.BACKWARD);
            }
            
            HashMap<Long, Direction> interactions = new HashMap<>();
            Long currentBl = null;
            if(forwardNotes != null)
            {
                for(Note note : forwardNotes)
                {
                    if(currentBl == null || !note.getOriginalBlog().equals(currentBl))
                    {
                        currentBl = note.getOriginalBlog();
                        interactions.put(currentBl, Direction.FORWARD);
                    }
                }
            }
            if(backwardNotes != null)
            {
                currentBl = null;
                for(Note note : backwardNotes)
                {
                    if(currentBl == null || !note.getInteractingBlog().equals(currentBl))
                    {
                        currentBl = note.getInteractingBlog();
                        if(interactions.containsKey(currentBl))
                            interactions.put(currentBl, Direction.BOTH);
                        else
                            interactions.put(currentBl, Direction.BACKWARD);
                    }
                }
            }
            
            for(Long blogId : interactions.keySet())
            {
                Blog bl = tf.getBlogDAO().find(blogId);
                if(toVisit.contains(bl.getName()))
                {
                    tf.getTreeDAO().create(b.getBlogId(), blogId, LinkType.COMPLETION_COMPL, interactions.get(blogId));
                }
                else
                {
                    tf.getTreeDAO().create(b.getBlogId(), blogId, LinkType.NONE, interactions.get(blogId));
                }
            }
        }
        
        System.out.println("SAMPLING COMPLETED. Total number of users: " + visited.size());
    }
}
