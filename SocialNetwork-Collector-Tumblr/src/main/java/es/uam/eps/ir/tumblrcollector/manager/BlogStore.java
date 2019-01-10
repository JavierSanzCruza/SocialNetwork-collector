/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.manager;

import es.uam.eps.ir.tumblrcollector.database.dao.TumblrDAOFactory;
import es.uam.eps.ir.tumblrcollector.database.dao.TumblrDAOHandler;
import es.uam.eps.ir.tumblrcollector.database.data.Blog;
import es.uam.eps.ir.utils.TextCleaner;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class BlogStore 
{   
    TumblrDAOFactory fact;
    
    /**
     * Constructor. Initializes the DAOHandler and the databases.
     */
    public BlogStore()
    {
        TumblrDAOHandler tdaoh = TumblrDAOHandler.getInstance();
        fact = tdaoh.getFactory(TumblrManager.getInstance().getDatabase());
    }
    /**
     * Stores a blog from a raw blog
     * @param blog Raw blog
     * @param visited Indicates if the blog has been visited in the exploration.
     * @param level Level of the tree.
     * @return The blog.
     */
    public Blog storeBlog(com.tumblr.jumblr.types.Blog blog, boolean visited, int level)
    {
        try {
            Blog b = new Blog();
            
            b.setName(blog.getName());
            b.setUrl("http://" + b.getName() + ".tumblr.com");
            b.setTitle(TextCleaner.cleanText(blog.getTitle()));
            b.setDescription(TextCleaner.cleanText(blog.getDescription()));
            b.setNumPosts(blog.getPostCount());
            b.setNumLikes(blog.getLikeCount());
            b.setIsVisited(visited);
            b.setLevel(level);
            
            Blog aux = fact.getBlogDAO().find(blog.getName());
            if(aux != null)
            {
                b.setBlogId(aux.getBlogId());
                b.setIsVisited((b.getIsVisited()? true : visited));
                b.setLevel((b.getIsVisited()? b.getLevel() : level));
                fact.getBlogDAO().update(b);
            }
            else
            {
                fact.getBlogDAO().create(b);
            }
            
            return b;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PostStore.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public void setVisited(Blog blog)
    {
        blog.setIsVisited(true);
        fact.getBlogDAO().update(blog);
    }
}
