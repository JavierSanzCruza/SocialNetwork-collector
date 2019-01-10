/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.manager;

import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.types.Video;
import es.uam.eps.ir.tumblrcollector.database.dao.TumblrDAOFactory;
import es.uam.eps.ir.tumblrcollector.database.dao.TumblrDAOHandler;
import es.uam.eps.ir.tumblrcollector.database.dao.data.*;
import es.uam.eps.ir.tumblrcollector.database.data.*;
import es.uam.eps.ir.utils.TextCleaner;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scribe.exceptions.OAuthConnectionException;


/**
 * Class for storing posts in the database, starting from the blogs provided by Tumblr.
 * @author Javier
 */
public class PostStore 
{
    /**
     * Factory
     */
    TumblrDAOFactory fact;
    
    /**
     * Constructor. Initializes the DAOHandler and the databases.
     */
    public PostStore()
    {
        TumblrDAOHandler tdaoh = TumblrDAOHandler.getInstance();
        fact = tdaoh.getFactory(TumblrManager.getInstance().getDatabase());
    }
    
    /**
     * Stores a post in the database.
     * @param post Tumblr post to store.
     * @param blogId Blog identifier.
     * @return 0 if OK or the post exists, -1 if not
     */
    public Integer storePost(com.tumblr.jumblr.types.Post post, Long blogId) throws InterruptedException
    {
        
        //Store the post
        Post storePost = fact.getPostDAO().find(post.getId());
        if(storePost != null)
            return 0;
        
        switch(post.getType())
        {
            case "text":
                storePost = storeTextPost((com.tumblr.jumblr.types.TextPost)post,blogId);
                break;
            case "photo":
                storePost = storePhotoPost((com.tumblr.jumblr.types.PhotoPost)post,blogId);
                break;
            case "audio":
                storePost = storeAudioPost((com.tumblr.jumblr.types.AudioPost)post,blogId);
                break;
            case "video":
                storePost = storeVideoPost((com.tumblr.jumblr.types.VideoPost)post,blogId);
                break;
            case "chat":
                storePost = storeChatPost((com.tumblr.jumblr.types.ChatPost) post, blogId);
                break;
            case "link":
                storePost = storeLinkPost((com.tumblr.jumblr.types.LinkPost) post, blogId);
                break;
            case "quote":
                storePost = storeQuotePost((com.tumblr.jumblr.types.QuotePost) post, blogId);
                break;
            case "answer":
                storePost = storeAnswerPost((com.tumblr.jumblr.types.AnswerPost) post, blogId);
                break;
            default:
                return -1;
        }
        
        //Store the tags
        List<String> tags = post.getTags();
        for(String tag : tags)
        {
            storeTag(tag, storePost);
        }
        
        //STORE THE CHAIN OF REBLOGS
        if(storePost.getIsReblog()) //While the post is a reblog
        {
            Map<String, Object> params = new HashMap<>();
            params.put("id", storePost.getReblogs());
            params.put("reblog_info", true);
            params.put("notes_info", true);
            params.put("filter", "text");
            com.tumblr.jumblr.types.Post p = null;
            Boolean cont;
            int counter = 100;
            do
            {
                cont = false;
                try
                {
                    if(post != null && post.getRebloggedFromName() != null)
                    {
                        List<com.tumblr.jumblr.types.Post> aux = TumblrManager.getInstance().getTumblr().blogPosts(post.getRebloggedFromName(), params);
                        if(aux != null && aux.size() > 0 && post.getRebloggedFromName() != null)
                            p = aux.get(0);
                    }
                }
                catch(JumblrException je)
                {
                    p = null;
                }
                catch(OAuthConnectionException oauth)
                {
                    if(counter > 0)
                    {
                        counter--;
                        Thread.sleep(60);
                        cont = true;
                    }
                }
            }
            while(cont);
            
            if(p != null) //Recursively find the reblog chain.
            {
                storePost(p);
                Post test = fact.getPostDAO().find(p.getId());
                if(test != null)
                    storeReblogNote(p, storePost);
            }
            
        }
        else //STORE THE NOTES, AND THE CHAIN OF REBLOGS LEADING TO THIS POST
        {
            //Obtain the notes
            List<com.tumblr.jumblr.types.Note> notes = post.getNotes();
            if(notes != null)
            {
                for(com.tumblr.jumblr.types.Note note : notes)
                {
                    if(note.getType().equals("reblog")) //Recursively find the reblog chain.
                    {
                        
                        Boolean cont = true;
                        com.tumblr.jumblr.types.Post p = null;
                        do
                        {
                            Map<String, Object> params = new HashMap<>();
                            params.put("id", note.getPostId());
                            params.put("reblog_info", true);
                            params.put("notes_info", true);
                            params.put("filter", "text");
                            cont = true;
                            
                            try
                            {
                                p = TumblrManager.getInstance().getTumblr().blogPosts(note.getBlogName(), params).get(0);
                            }
                            catch(OAuthConnectionException | IllegalStateException oauthe)
                            {
                                System.out.println("ERROR: Connection Lost");
                                TumblrManager.resetTumblr();
                                cont = false;
                            }
                            catch(JumblrException je)
                            {
                                System.out.println("ERROR: Jumblr Exception");
                                return -1;
                            }
                        }
                        while (cont == false);
                            
                        boolean b = false;
                        int i = 0;
                        do
                        {
                            try
                            {
                                if(p != null) //Recursively find the reblog chain.
                                {
                                    storePost(p);
                                    storeReblogNote(storePost, p);
                                    b = true;
                                }
                            }
                            catch(Exception e)
                            {
                                ++i;
                                System.out.println("RETRYING");
                            }
                        } while(b == false && i < 100);
                    }
                    else //Store the other note
                    {
                        storeNote(note, storePost);
                    }
                }
            }
        }
        
        
        return 0;
    }
    /**
     * 
     * @param post Raw post to store in the database.
     * @return The final level assigned to the post.
     */
    public Integer storePost(com.tumblr.jumblr.types.Post post) throws InterruptedException
    {        
        TumblrDAOHandler tdaoh = TumblrDAOHandler.getInstance();
        
        Blog blog = storeBlog(post.getBlogName());
        if(blog != null)
            return this.storePost(post, blog.getBlogId());
        return -1;
    }
    
    /**
     * Stores basic information for a blog in the database
     * @param blogName 
     * @return 
     */
    private Blog storeBlog(String blogName)
    {
        Blog blog = fact.getBlogDAO().find(blogName);
        if(fact.getBlogDAO().find(blogName) == null)
        {
            blog = new Blog();
            blog.setName(blogName);
            blog.setIsVisited(Boolean.FALSE);
            fact.getBlogDAO().create(blog);
        }
        return blog;
    }
    
    /**
     * Stores basic information for a blog in the database.
     * @param blogName blog name. Unique identifier.
     * @param blogUrl Url for the blog.
     * @return the stored blog.
     */
    private Blog storeBlog(String blogName, String blogUrl)
    {
        Blog blog = fact.getBlogDAO().find(blogName);
        if(blog == null)
        {
            blog = new Blog();
            blog.setName(blogName);
            blog.setUrl(blogUrl);
            blog.setIsVisited(Boolean.FALSE);
            fact.getBlogDAO().create(blog);
        }
        else if(blog.getUrl() == null)
        {
            blog.setUrl(blogUrl);
            blog.setIsVisited(blog.getIsVisited());
            fact.getBlogDAO().update(blog);
        }
        return blog;
    }
    /**
     * Stores a text post in the database.
     * @param post The raw text post from Tumblr.
     * @param blogId Identifier of the blog that published the post.
     * @return The stored post.
     */
    public TextPost storeTextPost(com.tumblr.jumblr.types.TextPost post, Long blogId)
    {
        TextPost tp = new TextPost();
        tp.setPostId(post.getId());
        tp.setBlogId(blogId);
        tp.setNumNotes(post.getNoteCount().intValue());
        tp.setUrl(post.getPostUrl());
        tp.setLiked(post.isLiked());
        tp.setTimestamp(new Date(post.getTimestamp()));
        tp.setIsReblog((post.getRebloggedFromId() != null));
        tp.setReblogs(post.getRebloggedFromId());
        
        try
        {
            tp.setTitle(TextCleaner.cleanText(post.getTitle()));
            tp.setBody(TextCleaner.cleanText(post.getBody()));
        }
        catch(UnsupportedEncodingException uee)
        {
            System.err.println(uee.getStackTrace());
            return null;
        }
        
        fact.getPostDAO().create(tp);
        return tp;
    }

    /**
     * Stores a photo post in the database.
     * @param post The raw text post from Tumblr.
     * @param fact The factory that establishes connection with the database.
     * @param blogId Identifier of the blog that published the post.
     * @return The stored post.
     */
    private PhotoPost storePhotoPost(com.tumblr.jumblr.types.PhotoPost post, Long blogId) 
    {
        try {
            PhotoPost pp = new PhotoPost();
            //Common fields
            pp.setPostId(post.getId());
            pp.setBlogId(blogId);
            pp.setNumNotes(post.getNoteCount().intValue());
            pp.setUrl(post.getPostUrl());
            pp.setLiked(post.isLiked());
            pp.setTimestamp(new Date(post.getTimestamp()));
            pp.setIsReblog((post.getRebloggedFromId() != null));
            pp.setReblogs(post.getRebloggedFromId());
            //Photo post fields
            pp.setCaption(TextCleaner.cleanText(post.getCaption()));
            pp.setWidth(post.getWidth());
            pp.setHeight(post.getHeight());
            pp.setIsPhotoset(post.isPhotoset());
            //Store the post
            fact.getPostDAO().create(pp);
            
            //Photos
            List<com.tumblr.jumblr.types.Photo> photos = post.getPhotos();
            PhotoDAO pdao = fact.getPhotoDAO();
            for(com.tumblr.jumblr.types.Photo photo : photos)
            {
                this.storePhoto(photo, pp);
            }
            
            return pp;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PostStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Stores a photo in the database.
     * @param photo Raw photo from Tumblr.
     * @param post Post the photo belongs to.
     */
    private void storePhoto(com.tumblr.jumblr.types.Photo photo, Post post)
    {
        try {
            Photo ph = new Photo();
            ph.setCaption(TextCleaner.cleanText(photo.getCaption()));
            ph.setHeight(photo.getOriginalSize().getHeight());
            ph.setWidth(photo.getOriginalSize().getWidth());
            ph.setUrl(photo.getOriginalSize().getUrl());
            
            PhotoDAO pdao = fact.getPhotoDAO();
            Photo aux = pdao.find(ph.getUrl());
            if(aux == null)
            {
                pdao.create(ph);
                aux = ph;
            }
            pdao.associateToPost(aux, post);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PostStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Stores an audio post in the database.
     * @param post Raw audio post from Tumblr.
     * @param blogId Identifier of the blog that published the post.
     * @return The stored post.
     */
    private AudioPost storeAudioPost(com.tumblr.jumblr.types.AudioPost post, Long blogId) 
    {
        try {
            AudioPost ap = new AudioPost();
            //Common fields
            ap.setPostId(post.getId());
            ap.setBlogId(blogId);
            ap.setNumNotes(post.getNoteCount().intValue());
            ap.setTimestamp(new Date(post.getTimestamp()));
            ap.setUrl(post.getPostUrl());
            ap.setLiked(post.isLiked());
            ap.setIsReblog((post.getRebloggedFromId() != null));
            ap.setReblogs(post.getRebloggedFromId());
            //Audio post fields
            ap.setPlayer(post.getEmbedCode());
            ap.setArtist(TextCleaner.cleanText(post.getArtistName()));
            ap.setAlbum(TextCleaner.cleanText(post.getAlbumName()));
            ap.setAlbumArt(post.getAlbumArtUrl());
            ap.setTrackName(TextCleaner.cleanText(post.getTrackName()));
            ap.setTrackNumber(post.getTrackNumber());
            ap.setYear(post.getYear());
            ap.setCaption(TextCleaner.cleanText(post.getCaption()));
            
            fact.getPostDAO().create(ap);
            
            return ap;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PostStore.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Stores a video post in the database, and the associated VideoPlayers.
     * @param post Raw video post from Tumblr.
     * @param blogId Identifier of the blog that published the post.
     * @return The stored post.
     */
    private VideoPost storeVideoPost(com.tumblr.jumblr.types.VideoPost post, Long blogId) 
    {
        try {
            VideoPost vp = new VideoPost();
            //Common fields
            vp.setPostId(post.getId());
            vp.setBlogId(blogId);
            vp.setNumNotes(post.getNoteCount().intValue());
            vp.setTimestamp(new Date(post.getTimestamp()));
            vp.setUrl(post.getPostUrl());
            vp.setLiked(post.isLiked());
            vp.setIsReblog((post.getRebloggedFromId() != null));
            vp.setReblogs(post.getRebloggedFromId());
            // Video post fields
            vp.setCaption(TextCleaner.cleanText(post.getCaption()));
            
            fact.getPostDAO().create(vp);
            List<Video> listVideo = post.getVideos();
            for(Video v : listVideo)
            {
                storeVideoPlayer(v,vp);
            }
            
            return vp;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PostStore.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * Stores a video player in the database.
     * @param v Raw video player from Tumblr.
     * @param post Post the video player belongs to.
     */
    private void storeVideoPlayer(Video v, Post post)
    {
        try {
            VideoPlayer vp = new VideoPlayer();
            String embedCode = TextCleaner.cleanText(v.getEmbedCode());
            vp.setEmbedCode(embedCode);
            vp.setWidth(v.getWidth());
            
            VideoPlayerDAO vpDAO = fact.getVideoPlayerDAO();
            VideoPlayer aux = vpDAO.find(embedCode);
            if(aux == null)
            {
                vpDAO.create(vp);
                aux = vp;
            }
            vpDAO.associateToPost(aux, post);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PostStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Stores a chat post in the database.
     * @param post Raw chat post from Tumblr.
     * @param blogId Identifier of the blog that published the post.
     * @return The stored post.
     */
    private ChatPost storeChatPost(com.tumblr.jumblr.types.ChatPost post, Long blogId) 
    {
        try {
            ChatPost cp = new ChatPost();
            //Common fields
            cp.setPostId(post.getId());
            cp.setBlogId(blogId);
            cp.setNumNotes(post.getNoteCount().intValue());
            cp.setTimestamp(new Date(post.getTimestamp()));
            cp.setUrl(post.getPostUrl());
            cp.setLiked(post.isLiked());
            cp.setIsReblog((post.getRebloggedFromId() != null));
            cp.setReblogs(post.getRebloggedFromId());
            //Chat post fields
            cp.setTitle(TextCleaner.cleanText(post.getTitle()));
            cp.setBody(TextCleaner.cleanText(post.getBody()));
            
            fact.getPostDAO().create(cp);
            return cp;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PostStore.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Stores a link post in the database.
     * @param post Raw link post from Tumblr.
     * @param blogId Identifier of the blog that published the post.
     * @return The stored post.
     */
    private LinkPost storeLinkPost(com.tumblr.jumblr.types.LinkPost post, Long blogId) 
    {
        try {
            LinkPost lp = new LinkPost();
            //Common fields
            lp.setPostId(post.getId());
            lp.setBlogId(blogId);
            lp.setNumNotes(post.getNoteCount().intValue());
            lp.setTimestamp(new Date(post.getTimestamp()));
            lp.setUrl(post.getPostUrl());
            lp.setLiked(post.isLiked());
            lp.setIsReblog((post.getRebloggedFromId() != null));
            lp.setReblogs(post.getRebloggedFromId());
            //Link Post Fields
            lp.setTitle(TextCleaner.cleanText(post.getTitle()));
            lp.setUrl(post.getLinkUrl());
            lp.setAuthor(TextCleaner.cleanText(post.getAuthorId()));
            lp.setDescription(TextCleaner.cleanText(post.getDescription()));
            fact.getPostDAO().create(lp);
            return lp;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PostStore.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }    

    /**
     * Stores a quote post in the database.
     * @param post Raw quote post from Tumblr.
     * @param blogId Identifier of the blog that published the post.
     * @return The stored post.
     */
    private QuotePost storeQuotePost(com.tumblr.jumblr.types.QuotePost post, Long blogId) 
    {
        try {
            QuotePost qp = new QuotePost();
            //Common fields
            qp.setPostId(post.getId());
            qp.setBlogId(blogId);
            qp.setNumNotes(post.getNoteCount().intValue());
            qp.setTimestamp(new Date(post.getTimestamp()));
            qp.setUrl(post.getPostUrl());
            qp.setLiked(post.isLiked());
            qp.setIsReblog((post.getRebloggedFromId() != null));
            qp.setReblogs(post.getRebloggedFromId());
            //Quote post fields
            qp.setQuote(TextCleaner.cleanText(post.getText()));
            qp.setSource(TextCleaner.cleanText(post.getSource()));
            fact.getPostDAO().create(qp);
            return qp;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PostStore.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Stores an answer post in the database.
     * @param post Raw answer post from Tumblr.
     * @param blogId Identifier of the blog that published the post.
     * @return The stored post.
     */
    private AnswerPost storeAnswerPost(com.tumblr.jumblr.types.AnswerPost post, Long blogId) 
    {
        try {
            AnswerPost ap = new AnswerPost();
            //Common fields
            ap.setPostId(post.getId());
            ap.setBlogId(blogId);
            ap.setNumNotes(post.getNoteCount().intValue());
            ap.setTimestamp(new Date(post.getTimestamp()));
            ap.setUrl(post.getPostUrl());
            ap.setLiked(post.isLiked());
            ap.setIsReblog((post.getRebloggedFromId() != null));
            ap.setReblogs(post.getRebloggedFromId());
            //Answer post fields
            ap.setQuestion(TextCleaner.cleanText(post.getQuestion()));
            ap.setAnswer(TextCleaner.cleanText(post.getAnswer()));
            ap.setAskingName(post.getAskingName());
            ap.setAskingUrl(post.getAskingUrl());
            fact.getPostDAO().create(ap);    
            return ap;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PostStore.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Stores a tag in the database.
     * @param tag Tag text.
     * @param storePost Post that contains the tag.
     */
    private void storeTag(String tag, Post storePost) 
    {
        try {
            Tag t = new Tag();
            t.setText(TextCleaner.cleanText(tag));
            
            
            TagDAO tDAO = fact.getTagDAO();
            Tag aux = tDAO.find(tag);
            if(aux == null)
            {
                tDAO.create(t);
                aux = t;
            }
            tDAO.associateToPost(aux, storePost);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PostStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Stores a note starting from a raw note obtained from Tumblr.
     * @param note Raw note obtained from Tumblr.
     * @param storePost stored post.
     */
    private void storeNote(com.tumblr.jumblr.types.Note note, Post storePost) 
    {
        Blog b = storeBlog(note.getBlogName(), note.getBlogUrl());
        Note n = new Note();
        n.setOriginalPost(storePost.getPostId());
        n.setRebloggingPost(note.getPostId());
        n.setType(note.getType());
        n.setInteractingBlog(b.getBlogId());
        n.setTimestamp(note.getTimestamp());
        n.setOriginalBlog(storePost.getBlogId());
        
        if(fact.getNoteDAO().find(n) == null)
        {
            fact.getNoteDAO().create(n);
        }
    }
    
    /**
     * Stores a reblog note from a raw post and a stored post.
     * @param originalPost Raw post from tumblr that represents the original post.
     * @param rebloggingPost Stored post that represents the reblogging post.
     */
    private void storeReblogNote(com.tumblr.jumblr.types.Post originalPost, Post rebloggingPost)
    {
        Note n = new Note();
        n.setInteractingBlog(rebloggingPost.getBlogId());
        n.setOriginalPost(originalPost.getId());
        n.setRebloggingPost(rebloggingPost.getPostId());
        n.setTimestamp(rebloggingPost.getTimestamp().getTime());
        n.setType("reblog");
        n.setOriginalBlog(fact.getBlogDAO().find(originalPost.getBlogName()).getBlogId());
        
        if(fact.getNoteDAO().find(n) == null)
        {
            fact.getNoteDAO().create(n);
        }
    }
    
    /**
     * Stores a reblog note from a raw post and a stored post.
     * @param originalPost Raw post from tumblr that represents the original post.
     * @param rebloggingPost Stored post that represents the reblogging post.
     */
    private void storeReblogNote(Post originalPost, com.tumblr.jumblr.types.Post rebloggingPost)
    {
        Note n = new Note();
        n.setInteractingBlog(fact.getBlogDAO().find(rebloggingPost.getBlogName()).getBlogId());
        n.setOriginalPost(originalPost.getPostId());
        n.setRebloggingPost(rebloggingPost.getId());
        n.setTimestamp(rebloggingPost.getTimestamp());
        n.setType("reblog");
        n.setOriginalBlog(originalPost.getBlogId());
        
        if(fact.getNoteDAO().find(n) == null)
        {
            fact.getNoteDAO().create(n);
        }
    }
    
    
}
