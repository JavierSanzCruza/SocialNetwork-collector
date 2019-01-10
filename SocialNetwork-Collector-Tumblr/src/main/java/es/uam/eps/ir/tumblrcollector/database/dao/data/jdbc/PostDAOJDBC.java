/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.database.dao.data.jdbc;

import static es.uam.eps.ir.database.dao.DAOUtil.prepareStatement;
import es.uam.eps.ir.tumblrcollector.database.dao.TumblrDAOFactory;
import es.uam.eps.ir.tumblrcollector.database.dao.TumblrDAOJDBC;
import es.uam.eps.ir.tumblrcollector.database.dao.data.PostDAO;
import es.uam.eps.ir.tumblrcollector.database.data.*;
import es.uam.eps.ir.database.dao.exceptions.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Javier
 */
public class PostDAOJDBC extends TumblrDAOJDBC<Post> implements PostDAO
{

    public PostDAOJDBC(TumblrDAOFactory daoFactory) 
    {
        super(daoFactory);
    }

    @Override
    public Post find(Long id) throws DAOException 
    {
        return this.find(SQL_FIND, id);
    }

    @Override
    public List<Post> list() throws DAOException {
        return this.list(SQL_LIST); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Post> list(Long blogId) throws DAOException {
        return this.list(SQL_LIST_BLOG, blogId); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Post> list(TumblrType type) throws DAOException 
    {
        return this.list(SQL_LIST_TYPE, type.toString());
    }

    @Override
    public List<Post> list(Long blogId, TumblrType type) throws DAOException 
    {
        Object[] values = new Object[]
        {
            blogId,
            type.toString()
        };
        
        return this.list(SQL_LIST_TYPE_BLOG, values);
    }

    @Override
    public void create(Post post) throws DAOException 
    {
        Object[] values =
        {
            post.getPostId(),
            post.getBlogId(),
            post.getNumNotes(),
            post.getUrl(),
            post.getLiked() ? 1:0,
            post.getType().toString(),
            post.getTimestamp().getTime(),
            post.getIsReblog(),
            post.getReblogs()            
        };
        try
        {
            this.create(SQL_CREATE_POST, false, true, values);
            //TODO: Guardar la parte comun del post
            if(post.getType() == TumblrType.TEXT_POST)
                createTextPost((TextPost) post);
            else if(post.getType() == TumblrType.PHOTO_POST)
                createPhotoPost((PhotoPost) post);
            else if(post.getType() == TumblrType.AUDIO_POST)
                createAudioPost((AudioPost) post);
            else if(post.getType() == TumblrType.VIDEO_POST)
                createVideoPost((VideoPost) post);
            else if(post.getType() == TumblrType.LINK_POST)
                createLinkPost((LinkPost) post);
            else if(post.getType() == TumblrType.QUOTE_POST)
                createQuotePost((QuotePost) post);
            else if(post.getType() == TumblrType.ANSWER_POST)
                createAnswerPost((AnswerPost) post);
            else
                createChatPost((ChatPost) post);
        }
        catch(Exception daoe)
        {
            System.out.println("1");
            System.out.println(Arrays.toString(daoe.getStackTrace()));
        }
    }
    
    

    @Override
    public void update(Post post) throws DAOException {
        Object[] values =
        {
            post.getPostId(),
            post.getBlogId(),
            post.getNumNotes(),
            post.getUrl(),
            post.getLiked() ? 1:0,
            post.getType().toString(),
            post.getTimestamp().getTime(),
            post.getIsReblog(),
            post.getReblogs(),
            post.getPostId()
        };
        
        this.create(SQL_CREATE_POST, false, true, values);
        //TODO: Guardar la parte comun del post
        if(post.getType() == TumblrType.TEXT_POST)
            updateTextPost((TextPost) post);
        else if(post.getType() == TumblrType.PHOTO_POST)
            updatePhotoPost((PhotoPost) post);
        else if(post.getType() == TumblrType.AUDIO_POST)
            updateAudioPost((AudioPost) post);
        else if(post.getType() == TumblrType.VIDEO_POST)
            updateVideoPost((VideoPost) post);
        else if(post.getType() == TumblrType.LINK_POST)
            updateLinkPost((LinkPost) post);
        else if(post.getType() == TumblrType.QUOTE_POST)
            updateQuotePost((QuotePost) post);
        else if(post.getType() == TumblrType.ANSWER_POST)
            updateAnswerPost((AnswerPost) post);
        else
            updateChatPost((ChatPost) post);
    }

    @Override
    public void delete(Post post) throws DAOException {
        if(post.getType() == TumblrType.TEXT_POST)
            this.delete(SQL_DELETE_TEXTPOST, true);
        else if(post.getType() == TumblrType.PHOTO_POST)
            this.delete(SQL_DELETE_PHOTOPOST, true);
        else if(post.getType() == TumblrType.AUDIO_POST)
            this.delete(SQL_DELETE_AUDIOPOST, true);
        else if(post.getType() == TumblrType.VIDEO_POST)
            this.delete(SQL_DELETE_VIDEOPOST, true);
        else if(post.getType() == TumblrType.LINK_POST)
            this.delete(SQL_DELETE_LINKPOST, true);
        else if(post.getType() == TumblrType.QUOTE_POST)
            this.delete(SQL_DELETE_QUOTEPOST, true);
        else if(post.getType() == TumblrType.ANSWER_POST)
            this.delete(SQL_DELETE_ANSWERPOST, true);
        else
            this.delete(SQL_DELETE_CHATPOST, true);
        
        this.delete(SQL_DELETE_POST, true);
    }

    @Override
    public List<Post> listReblogs(Long postId) throws DAOException {
        return this.list(SQL_LIST_REBLOGS, postId);
    }
    
    
    @Override
    protected Post map(ResultSet resultSet) throws SQLException 
    {
        if(resultSet == null)
            return null;
        String type = resultSet.getString("type");
        Long postId = resultSet.getLong("postId");
        Post post;
        switch(type)
        {
            case "text":
                TextPost textPost = this.findTextPost(postId);
                post = textPost;
                break;
            case "photo":
                PhotoPost photoPost = this.findPhotoPost(postId);
                post = photoPost;
                break;
            case "audio":
                AudioPost audioPost = this.findAudioPost(postId);
                post = audioPost;
                break;
            case "video":
                VideoPost videoPost = this.findVideoPost(postId);
                post = videoPost;
                break;
            case "link":
                LinkPost linkPost = this.findLinkPost(postId);
                post = linkPost;
                break;
            case "chat":
                ChatPost chatPost = this.findChatPost(postId);
                post = chatPost;
                break;
            case "quote":
                QuotePost quotePost = this.findQuotePost(postId);
                post = quotePost;
                break;
            case "answer":
                AnswerPost answerPost = this.findAnswerPost(postId);
                post = answerPost;
                break;
            default:
                return null;
        }
        
        post.setPostId(resultSet.getLong("postId"));
        post.setBlogId(resultSet.getLong("blogId"));
        post.setNumNotes(resultSet.getInt("numNotes"));
        post.setLiked((resultSet.getInt("liked") == 1));
        post.setIsReblog((resultSet.getInt("isReblog") == 1));
        post.setTimestamp(new Date(resultSet.getLong("timestamp")));
        post.setReblogs(resultSet.getLong("reblogs"));
        post.setUrl(resultSet.getString("url"));
        return post;
    }
    
    
    /***************************************AUXILIARY METHODS (CREATION) *********************************************************************/
    /**
     * Stores a text post in the database.
     * @param textPost Text post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void createTextPost(TextPost textPost) 
    {
        if(textPost == null)
        {
            throw new IllegalArgumentException("The text post cannot be null");
        }
        
        Object[] values = 
        {
            textPost.getPostId(),
            textPost.getTitle(),
            textPost.getBody(),
        };

        this.create(SQL_CREATE_TEXTPOST, false, true, values);
    }
    
    /**
     * Stores a photo post in the database.
     * @param photoPost Photo post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void createPhotoPost(PhotoPost photoPost) 
    {
        if(photoPost == null)
        {
            throw new IllegalArgumentException("The photo post cannot be null");
        }
        
        Object[] values = 
        {
            photoPost.getPostId(),
            photoPost.getCaption(),
            photoPost.getWidth(),
            photoPost.getHeight(),
            photoPost.getIsPhotoset() ? 1:0
        };

        this.create(SQL_CREATE_PHOTOPOST, false, true, values);
    }
    
    /**
     * Stores an audio post in the database.
     * @param audioPost Audio post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void createAudioPost(AudioPost audioPost) 
    {
        if(audioPost == null)
        {
            throw new IllegalArgumentException("The audio post cannot be null");
        }
        
        Object[] values = 
        {
            audioPost.getPostId(),
            audioPost.getPlayer(),
            audioPost.getArtist(),
            audioPost.getAlbum(),
            audioPost.getAlbumArt(),
            audioPost.getTrackName(),
            audioPost.getTrackNumber(),
            audioPost.getYear(),
            audioPost.getCaption()
        };

        this.create(SQL_CREATE_AUDIOPOST, false, true, values);
    }
    
    /**
     * Stores an video post in the database.
     * @param videoPost Video post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void createVideoPost(VideoPost videoPost) 
    {
        if(videoPost == null)
        {
            throw new IllegalArgumentException("The video post cannot be null");
        }
        
        Object[] values = 
        {
            videoPost.getPostId(),
            videoPost.getCaption()
        };

        this.create(SQL_CREATE_VIDEOPOST, false, true, values);
    }
    
    /**
     * Stores a link post in the database.
     * @param linkPost Link post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void createLinkPost(LinkPost linkPost) 
    {
        if(linkPost == null)
        {
            throw new IllegalArgumentException("The video post cannot be null");
        }
        
        Object[] values = 
        {
            linkPost.getPostId(),
            linkPost.getTitle(),
            linkPost.getUrl(),
            linkPost.getAuthor(),
            linkPost.getExcerpt(),
            linkPost.getDescription(),
            linkPost.getPublisher()
        };

        this.create(SQL_CREATE_LINKPOST, false, true, values);
    }
    
    /**
     * Stores a quote post in the database.
     * @param quotePost Quote post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void createQuotePost(QuotePost quotePost) 
    {
        if(quotePost == null)
        {
            throw new IllegalArgumentException("The video post cannot be null");
        }
        
        Object[] values = 
        {
            quotePost.getPostId(),
            quotePost.getQuote(),
            quotePost.getSource()
        };

        this.create(SQL_CREATE_QUOTEPOST, false, true, values);
    }
    
    /**
     * Stores a answer post in the database.
     * @param answerPost Answer post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void createAnswerPost(AnswerPost answerPost) 
    {
        if(answerPost == null)
        {
            throw new IllegalArgumentException("The video post cannot be null");
        }
        
        Object[] values = 
        {
            answerPost.getPostId(),
            answerPost.getQuestion(),
            answerPost.getAnswer(),
            answerPost.getAskingName(),
            answerPost.getAskingUrl()
        };

        this.create(SQL_CREATE_ANSWERPOST, false, true, values);
    }
    
    /**
     * Stores a chat post in the database.
     * @param chatPost Answer post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void createChatPost(ChatPost chatPost) 
    {
        if(chatPost == null)
        {
            throw new IllegalArgumentException("The video post cannot be null");
        }
        
        Object[] values = 
        {
            chatPost.getPostId(),
            chatPost.getTitle(),
            chatPost.getBody()
        };

        this.create(SQL_CREATE_CHATPOST, false, true, values);
    }
    
    /**************************************** AUXILIARY METHODS (update) *******************************************/
    /**
     * Stores a text post in the database.
     * @param textPost Text post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void updateTextPost(TextPost textPost) 
    {
        if(textPost == null)
        {
            throw new IllegalArgumentException("The text post cannot be null");
        }
        
        Object[] values = 
        {
            textPost.getPostId(),
            textPost.getTitle(),
            textPost.getBody(),
        };

        this.update(SQL_UPDATE_TEXTPOST, true, values);
    }
    
    /**
     * Stores a photo post in the database.
     * @param photoPost Photo post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void updatePhotoPost(PhotoPost photoPost) 
    {
        if(photoPost == null)
        {
            throw new IllegalArgumentException("The photo post cannot be null");
        }
        
        Object[] values = 
        {
            photoPost.getPostId(),
            photoPost.getCaption(),
            photoPost.getWidth(),
            photoPost.getHeight(),
            photoPost.getIsPhotoset() ? 1:0
        };

        this.update(SQL_UPDATE_PHOTOPOST, true, values);
    }
    
    /**
     * Stores an audio post in the database.
     * @param audioPost Audio post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void updateAudioPost(AudioPost audioPost) 
    {
        if(audioPost == null)
        {
            throw new IllegalArgumentException("The audio post cannot be null");
        }
        
        Object[] values = 
        {
            audioPost.getPostId(),
            audioPost.getPlayer(),
            audioPost.getArtist(),
            audioPost.getAlbum(),
            audioPost.getAlbumArt(),
            audioPost.getTrackName(),
            audioPost.getTrackNumber(),
            audioPost.getYear(),
            audioPost.getCaption()
        };

        this.update(SQL_UPDATE_AUDIOPOST,true, values);
    }
    
    /**
     * Stores an video post in the database.
     * @param videoPost Video post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void updateVideoPost(VideoPost videoPost) 
    {
        if(videoPost == null)
        {
            throw new IllegalArgumentException("The video post cannot be null");
        }
        
        Object[] values = 
        {
            videoPost.getPostId(),
            videoPost.getCaption()
        };

        this.update(SQL_UPDATE_VIDEOPOST, true, values);
    }
    
    /**
     * Stores a link post in the database.
     * @param linkPost Link post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void updateLinkPost(LinkPost linkPost) 
    {
        if(linkPost == null)
        {
            throw new IllegalArgumentException("The video post cannot be null");
        }
        
        Object[] values = 
        {
            linkPost.getPostId(),
            linkPost.getTitle(),
            linkPost.getUrl(),
            linkPost.getAuthor(),
            linkPost.getExcerpt(),
            linkPost.getDescription(),
            linkPost.getPublisher()
        };

        this.update(SQL_UPDATE_LINKPOST, true, values);
    }
    
    /**
     * Stores a quote post in the database.
     * @param quotePost Quote post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void updateQuotePost(QuotePost quotePost) 
    {
        if(quotePost == null)
        {
            throw new IllegalArgumentException("The video post cannot be null");
        }
        
        Object[] values = 
        {
            quotePost.getPostId(),
            quotePost.getQuote(),
            quotePost.getSource()
        };

        this.update(SQL_UPDATE_QUOTEPOST, true, values);
    }
    
    /**
     * Stores a answer post in the database.
     * @param answerPost Answer post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void updateAnswerPost(AnswerPost answerPost) 
    {
        if(answerPost == null)
        {
            throw new IllegalArgumentException("The video post cannot be null");
        }
        
        Object[] values = 
        {
            answerPost.getPostId(),
            answerPost.getQuestion(),
            answerPost.getAnswer(),
            answerPost.getAskingName(),
            answerPost.getAskingUrl()
        };

        this.update(SQL_UPDATE_ANSWERPOST, true, values);
    }
    
    /**
     * Stores a chat post in the database.
     * @param chatPost Answer post to store.
     * @throws IllegalArgumentException if the post is null.
     */
    private void updateChatPost(ChatPost chatPost) 
    {
        if(chatPost == null)
        {
            throw new IllegalArgumentException("The video post cannot be null");
        }
        
        Object[] values = 
        {
            chatPost.getPostId(),
            chatPost.getTitle(),
            chatPost.getBody()
        };

        this.update(SQL_UPDATE_CHATPOST, true, values);
    }
    
    
    /**************************************** AUXILIARY METHODS (find and map) *************************************/
    
    /**
     * Finds a text post in the database by its identifier.
     * @param id Identifier of the post.
     * @return The text post.
     * @throws DAOException if something fails at database level.
     */
    private TextPost findTextPost(Long id) throws DAOException
    {
        return this.findTextPost(SQL_FIND_TEXT, id);
    }
    
    /**
     * Finds a photo post in the database by its identifier.
     * @param id Identifier of the post.
     * @return The photo post.
     * @throws DAOException if something fails at database level.
     */
    private PhotoPost findPhotoPost(Long id) throws DAOException
    {
        return this.findPhotoPost(SQL_FIND_PHOTO, id);
    }
    
    /**
     * Finds an audio post in the database by its identifier.
     * @param id Identifier of the post.
     * @return The audio post.
     * @throws DAOException if something fails at database level.
     */
    private AudioPost findAudioPost(Long id) throws DAOException
    {
        return this.findAudioPost(SQL_FIND_AUDIO, id);
    }
    
    /**
     * Finds a video post in the database by its identifier.
     * @param id Identifier of the post.
     * @return The video post.
     * @throws DAOException if something fails at database level.
     */
    private VideoPost findVideoPost(Long id) throws DAOException
    {
        return this.findVideoPost(SQL_FIND_VIDEO, id);
    }

    /**
     * Finds a link post in the database by its identifier.
     * @param id Identifier of the post.
     * @return The link post.
     * @throws DAOException if something fails at database level.
     */
    private LinkPost findLinkPost(Long id) throws DAOException
    {
        return this.findLinkPost(SQL_FIND_LINK, id);
    }
    
    /**
     * Finds a quote post in the database by its identifier.
     * @param id Identifier of the post.
     * @return The quote post.
     * @throws DAOException if something fails at database level.
     */
    private QuotePost findQuotePost(Long id) throws DAOException
    {
        return this.findQuotePost(SQL_FIND_QUOTE, id);
    }
    
    /**
     * Finds an answer post in the database by its identifier.
     * @param id Identifier of the post.
     * @return The answer post.
     * @throws DAOException if something fails at database level.
     */
    private AnswerPost findAnswerPost(Long id) throws DAOException
    {
        return this.findAnswerPost(SQL_FIND_ANSWER, id);
    }
    
    /**
     * Finds a chat post in the database by its identifier.
     * @param id Identifier of the post.
     * @return The chat post.
     * @throws DAOException if something fails at database level.
     */
    private ChatPost findChatPost(Long id) throws DAOException
    {
        return this.findChatPost(SQL_FIND_CHAT, id);
    }
    
    /**
     * Maps a text post from the database.
     * @param resultSet Query result
     * @return A text post with the data obtained from the result set
     * @throws SQLException if something fails at the result
     */
    private TextPost mapTextPost(ResultSet resultSet) throws SQLException
    {
        TextPost tp = new TextPost();
        tp.setTitle(resultSet.getString("title"));
        tp.setBody(resultSet.getString("body"));
        return tp;
    }
    
    /**
     * Maps a photo post from the database.
     * @param resultSet Query result
     * @return A photo post with the data obtained from the result set
     * @throws SQLException if something fails at the result
     */
    private PhotoPost mapPhotoPost(ResultSet resultSet) throws SQLException
    {
        PhotoPost pp = new PhotoPost();
        pp.setCaption(resultSet.getString("caption"));
        pp.setWidth(resultSet.getInt("width"));
        pp.setHeight(resultSet.getInt("height"));
        pp.setIsPhotoset((resultSet.getInt("isPhotoset") == 1));
        return pp;
    }
    
    /**
     * Maps an audio post from the database.
     * @param resultSet Query result
     * @return An audio post with the data obtained from the result set
     * @throws SQLException if something fails at the result
     */
    private AudioPost mapAudioPost(ResultSet resultSet) throws SQLException
    {
        AudioPost ap = new AudioPost();
        ap.setPlayer(resultSet.getString("player"));
        ap.setArtist(resultSet.getString("artist"));
        ap.setAlbum(resultSet.getString("album"));
        ap.setAlbumArt(resultSet.getString("albumArt"));
        ap.setTrackName(resultSet.getString("trackName"));
        ap.setTrackNumber(resultSet.getInt("trackNumber"));
        ap.setYear(resultSet.getInt("year"));
        ap.setCaption(resultSet.getString("caption"));
        return ap;
    }
    
    /**
     * Maps a video post from the database.
     * @param resultSet Query result
     * @return A video post with the data obtained from the result set
     * @throws SQLException if something fails at the result
     */
    private VideoPost mapVideoPost(ResultSet resultSet) throws SQLException
    {
        VideoPost vp = new VideoPost();
        vp.setCaption(resultSet.getString("caption"));
        return vp;
    }
    
    /**
     * Maps a quote post from the database.
     * @param resultSet Query result
     * @return A quote post with the data obtained from the result set
     * @throws SQLException if something fails at the result
     */
    private QuotePost mapQuotePost(ResultSet resultSet) throws SQLException
    {
        QuotePost qp = new QuotePost();
        qp.setQuote(resultSet.getString("quote"));
        qp.setSource(resultSet.getString("source"));
        return qp;
    }
    
    /**
     * Maps an answer post from the database.
     * @param resultSet Query result
     * @return An answer post with the data obtained from the result set
     * @throws SQLException if something fails at the result
     */
    private AnswerPost mapAnswerPost(ResultSet resultSet) throws SQLException
    {
        AnswerPost ap = new AnswerPost();
        ap.setQuestion(resultSet.getString("question"));
        ap.setAnswer(resultSet.getString("answer"));
        ap.setAskingName(resultSet.getString("askingName"));
        ap.setAskingUrl(resultSet.getString("askingUrl"));
        return ap;
    }
    
    /**
     * Maps a chat post from the database.
     * @param resultSet Query result
     * @return A chat post with the data obtained from the result set
     * @throws SQLException if something fails at the result
     */
    private ChatPost mapChatPost(ResultSet resultSet) throws SQLException
    {
        ChatPost cp = new ChatPost();
        cp.setTitle(resultSet.getString("title"));
        cp.setBody(resultSet.getString("chat"));
        return cp;
    }
    
    /**
     * Maps a link post from the database.
     * @param resultSet Query result
     * @return A link post with the data obtained from the result set
     * @throws SQLException if something fails at the result
     */
    private LinkPost mapLinkPost(ResultSet resultSet) throws SQLException
    {
        LinkPost lp = new LinkPost();
        lp.setTitle(resultSet.getString("title"));
        lp.setUrl(resultSet.getString("url"));
        lp.setAuthor(resultSet.getString("author"));
        lp.setExcerpt(resultSet.getString("excerpt"));
        lp.setDescription(resultSet.getString("description"));
        lp.setPublisher(resultSet.getString("publisher"));
        return lp;
    }
    
    
    private TextPost findTextPost(String sql, Object... values) throws DAOException 
    {
        TextPost t = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try 
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, sql, false, values);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                t = mapTextPost(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } 
        finally
        {
            if(statement != null)
            {
                try 
                {
                        statement.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
            if(resultSet != null)
            {
                try 
                {
                    resultSet.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
        }
        return t;
    }
    
    private PhotoPost findPhotoPost(String sql, Object... values) throws DAOException 
    {
        PhotoPost t = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try 
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, sql, false, values);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                t = mapPhotoPost(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } 
        finally
        {
            if(statement != null)
            {
                try 
                {
                        statement.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
            if(resultSet != null)
            {
                try 
                {
                    resultSet.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
        }
        return t;
    }
    
    private AudioPost findAudioPost(String sql, Object... values) throws DAOException 
    {
        AudioPost t = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try 
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, sql, false, values);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                t = mapAudioPost(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } 
        finally
        {
            if(statement != null)
            {
                try 
                {
                        statement.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
            if(resultSet != null)
            {
                try 
                {
                    resultSet.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
        }
        return t;
    }
    
    private VideoPost findVideoPost(String sql, Object... values) throws DAOException 
    {
        VideoPost t = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try 
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, sql, false, values);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                t = mapVideoPost(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } 
        finally
        {
            if(statement != null)
            {
                try 
                {
                        statement.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
            if(resultSet != null)
            {
                try 
                {
                    resultSet.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
        }
        return t;
    }
    
    private LinkPost findLinkPost(String sql, Object... values) throws DAOException 
    {
        LinkPost t = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try 
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, sql, false, values);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                t = mapLinkPost(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } 
        finally
        {
            if(statement != null)
            {
                try 
                {
                        statement.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
            if(resultSet != null)
            {
                try 
                {
                    resultSet.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
        }
        return t;
    }
    
    private QuotePost findQuotePost(String sql, Object... values) throws DAOException 
    {
        QuotePost t = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try 
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, sql, false, values);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                t = mapQuotePost(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } 
        finally
        {
            if(statement != null)
            {
                try 
                {
                    statement.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
            if(resultSet != null)
            {
                try 
                {
                    resultSet.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
        }
        return t;
    }
    
    protected ChatPost findChatPost(String sql, Object... values) throws DAOException 
    {
        ChatPost t = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try 
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, sql, false, values);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                t = mapChatPost(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } 
        finally
        {
            if(statement != null)
            {
                try 
                {
                        statement.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
            if(resultSet != null)
            {
                try 
                {
                    resultSet.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
        }
        return t;
    }
    
    protected AnswerPost findAnswerPost(String sql, Object... values) throws DAOException 
    {
        AnswerPost t = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try 
        {
            connection = daoFactory.getConnection();
            statement = prepareStatement(connection, sql, false, values);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                t = mapAnswerPost(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } 
        finally
        {
            if(statement != null)
            {
                try 
                {
                        statement.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
            if(resultSet != null)
            {
                try 
                {
                    resultSet.close();
                } 
                catch (SQLException ex) 
                {
                    throw new DAOException(ex);
                }
            }
        }
        return t;
    }
    
    private static final String SQL_FIND =
            "SELECT postId, "
            + "blogId, "
            + "numNotes, "
            + "url, "
            + "liked, "
            + "type, "
            + "timestamp, "
            + "isReblog, "
            + "reblogs "
            + "FROM Post "
            + "WHERE postId = ?";
    private static final String SQL_FIND_TEXT =
            "SELECT postId, "
            + "title, "
            + "body "
            + "FROM TextPost "
            + "WHERE postId = ?";
    private static final String SQL_FIND_PHOTO =
            "SELECT postId, "
            + "caption, "
            + "width, "
            + "height, "
            + "isPhotoset "
            + "FROM PhotoPost "
            + "WHERE postId = ?";
    private static final String SQL_FIND_AUDIO = 
            "SELECT postId, "
            + "player, "
            + "artist, "
            + "album, "
            + "albumArt, "
            + "trackName, "
            + "trackNumber, "
            + "year, "
            + "caption "
            + "FROM AudioPost "
            + "WHERE postId = ?";
    private static final String SQL_FIND_VIDEO =
            "SELECT postId, "
            + "caption "
            + "FROM VideoPost "
            + "WHERE postId = ?";
    private static final String SQL_FIND_LINK =
            "SELECT postId, "
            + "title, "
            + "url, "
            + "author, "
            + "excerpt, "
            + "description, "
            + "publisher "
            + "FROM LinkPost "
            + "WHERE postId = ?";
    private static final String SQL_FIND_QUOTE = 
            "SELECT postId, "
            + "quote, "
            + "source "
            + "FROM QuotePost "
            + "WHERE postId = ?";
    private static final String SQL_FIND_CHAT = 
            "SELECT postId, "
            + "title, "
            + "chat "
            + "FROM ChatPost "
            + "WHERE postId = ?";
    private static final String SQL_FIND_ANSWER =
            "SELECT postId, "
            + "question, "
            + "answer, "
            + "askingName, "
            + "askingUrl "
            + "FROM AnswerPost "
            + "WHERE postId = ?";
    private static final String SQL_LIST =
            "SELECT postId, "
            + "blogId, "
            + "numNotes, "
            + "url, "
            + "liked, "
            + "type, "
            + "timestamp, "
            + "isReblog, "
            + "reblogs "
            + "FROM Post";
    private static final String SQL_LIST_BLOG = 
            "SELECT postId, "
            + "blogId, "
            + "numNotes, "
            + "url, "
            + "liked, "
            + "type, "
            + "timestamp, "
            + "isReblog, "
            + "reblogs "
            + "FROM Post "
            + "WHERE blogId = ?";
    private static final String SQL_LIST_TYPE = 
            "SELECT postId, "
            + "blogId, "
            + "numNotes, "
            + "url, "
            + "liked, "
            + "type, "
            + "timestamp, "
            + "isReblog, "
            + "reblogs "
            + "FROM Post "
            + "WHERE type = ?";
    private static final String SQL_LIST_TYPE_BLOG =
            "SELECT postId, "
            + "blogId, "
            + "numNotes, "
            + "url, "
            + "liked, "
            + "type, "
            + "timestamp, "
            + "isReblog, "
            + "reblogs "
            + "FROM Post "
            + "WHERE blogId = ? and type = ?";
    private static final String SQL_CREATE_POST =
            "INSERT INTO Post "
            + "(postId,"
            + "blogId, "
            + "numNotes, "
            + "url, "
            + "liked, "
            + "type, "
            + "timestamp, "
            + "isReblog, "
            + "reblogs) "
            + "VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String SQL_CREATE_TEXTPOST =
            "INSERT INTO TextPost "
            + "(postId, "
            + "title, "
            + "body) "
            + "VALUES (?, ?, ?)";
    private static final String SQL_CREATE_PHOTOPOST =
            "INSERT INTO PhotoPost "
            + "(postId, "
            + "caption, "
            + "width, "
            + "height, "
            + "isPhotoset) "
            + "VALUES (?,?,?,?,?)";
    private static final String SQL_CREATE_AUDIOPOST =
            "INSERT INTO AudioPost "
            + "(postId, "
            + "player, "
            + "artist, "
            + "album, "
            + "albumArt, "
            + "trackName, "
            + "trackNumber, "
            + "year, "
            + "caption) "
            + "VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String SQL_CREATE_VIDEOPOST =
            "INSERT INTO VideoPost "
            + "(postId, "
            + "caption) "
            + "VALUES (?, ?)";
    private static final String SQL_CREATE_LINKPOST =
            "INSERT INTO LinkPost "
            + "(postId, "
            + "title, "
            + "url, "
            + "author, "
            + "excerpt, "
            + "description, "
            + "publisher) "
            + "VALUES (?,?,?,?,?,?,?)";
    private static final String SQL_CREATE_QUOTEPOST =
            "INSERT INTO QuotePost "
            + "(postId, "
            + "quote, "
            + "source) "
            + "VALUES (?,?,?)";
    private static final String SQL_CREATE_ANSWERPOST =
            "INSERT INTO AnswerPost "
            + "(postId, "
            + "question, "
            + "answer, "
            + "askingName, "
            + "askingUrl) "
            + "VALUES (?,?,?,?,?)";
    private static final String SQL_CREATE_CHATPOST =
            "INSERT INTO ChatPost "
            + "(postId, "
            + "title, "
            + "chat) "
            + "VALUES (?, ?, ?)";
    private static final String SQL_UPDATE_POST =
            "UPDATE Post "
            + "SET postId = ?, "
            + "blogId = ?, "
            + "numNotes = ?, "
            + "url = ?, "
            + "liked = ?, "
            + "type = ?, "
            + "timestamp = ?, "
            + "isReblog = ?, "
            + "reblogs= ?) "
            + "WHERE postId = ?";
    private static final String SQL_UPDATE_TEXTPOST =
            "Update TextPost "
            + "SET postId = ?, "
            + "title = ?, "
            + "body = ? "
            + "WHERE postId = ?";
    private static final String SQL_UPDATE_PHOTOPOST =
            "UPDATE PhotoPost "
            + "SET postId = ?, "
            + "caption = ?, "
            + "width = ?, "
            + "height = ?, "
            + "isPhotoset = ? "
            + "WHERE postId = ?";
    private static final String SQL_UPDATE_AUDIOPOST =
            "UPDATE AudioPost "
            + "SET postId = ?, "
            + "player = ?, "
            + "artist = ?, "
            + "album = ?, "
            + "albumArt = ?, "
            + "trackName = ?, "
            + "trackNumber = ?, "
            + "year = ?, "
            + "caption = ? "
            + "WHERE postId = ?";
    private static final String SQL_UPDATE_VIDEOPOST =
            "UPDATE VideoPost "
            + "SET postId = ?, "
            + "caption = ? "
            + "WHERE postId = ?";
    private static final String SQL_UPDATE_LINKPOST =
            "UPDATE LinkPost "
            + "SET postId = ?, "
            + "title = ?, "
            + "url = ?, "
            + "author = ?, "
            + "excerpt = ?, "
            + "description = ?, "
            + "publisher = ? "
            + "WHERE postId = ?";
    private static final String SQL_UPDATE_QUOTEPOST =
            "UPDATE QuotePost "
            + "SET postId = ?, "
            + "quote = ?, "
            + "source = ? "
            + "WHERE postId = ?";
    private static final String SQL_UPDATE_ANSWERPOST =
            "UPDATE AnswerPost "
            + "SET postId = ?, "
            + "question = ?, "
            + "answer = ?, "
            + "askingName = ?, "
            + "askingUrl = ? "
            + "WHERE postId = ?";
    private static final String SQL_UPDATE_CHATPOST =
            "UPDATE ChatPost "
            + "SET postId = ?, "
            + "title = ?, "
            + "chat = ? "
            + "WHERE postId = ?";
    private static final String SQL_DELETE_POST =
            "DELETE FROM Post "
            + "WHERE postId = ?";
    private static final String SQL_DELETE_TEXTPOST =
            "DELETE FROM TextPost "
            + "WHERE postId = ?";
    private static final String SQL_DELETE_PHOTOPOST =
            "DELETE FROM PhotoPost "
            + "WHERE postId = ?";;
    private static final String SQL_DELETE_AUDIOPOST =
            "DELETE FROM AudioPost "
            + "WHERE postId = ?";
    private static final String SQL_DELETE_VIDEOPOST =
            "DELETE FROM VideoPost "
            + "WHERE postId = ?";
    private static final String SQL_DELETE_LINKPOST =
            "DELETE FROM LinkPost "
            + "WHERE postId = ?";
    private static final String SQL_DELETE_QUOTEPOST =
           "DELETE FROM QuotePost "
            + "WHERE postId = ?";
    private static final String SQL_DELETE_ANSWERPOST =
           "DELETE FROM AnswerPost "
            + "WHERE postId = ?";
    private static final String SQL_DELETE_CHATPOST =
           "DELETE FROM ChatPost "
            + "WHERE postId = ?";
    private static final String SQL_LIST_REBLOGS =
            "SELECT postId, "
            + "blogId, "
            + "numNotes, "
            + "url, "
            + "liked, "
            + "type, "
            + "timestamp, "
            + "isReblog, "
            + "reblogs "
            + "FROM Post "
            + "WHERE reblogs = ?";
    
}
