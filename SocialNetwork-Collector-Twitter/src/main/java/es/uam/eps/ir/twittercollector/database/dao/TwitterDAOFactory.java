/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.database.dao;

import es.uam.eps.ir.database.dao.DAOProperties;
import es.uam.eps.ir.twittercollector.database.dao.data.jdbc.UsersRetweetDAOJDBC;
import es.uam.eps.ir.twittercollector.database.dao.data.jdbc.TweetDAOJDBC;
import es.uam.eps.ir.twittercollector.database.dao.data.jdbc.UserDAOJDBC;
import es.uam.eps.ir.twittercollector.database.dao.data.jdbc.UrlDAOJDBC;
import es.uam.eps.ir.twittercollector.database.dao.data.jdbc.MediaDAOJDBC;
import es.uam.eps.ir.twittercollector.database.dao.data.jdbc.TwitterKeyDAOJDBC;
import es.uam.eps.ir.twittercollector.database.dao.data.jdbc.HashtagDAOJDBC;
import es.uam.eps.ir.twittercollector.database.dao.data.UsersRetweetDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.TweetDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UserDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.MediaDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.HashtagDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.TwitterKeyDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UrlDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.UserInteractionDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.jdbc.UserInteractionDAOJDBC;
import es.uam.eps.ir.twittercollector.database.dao.exceptions.DAOConfigurationException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * This class represents a DAO factory for a SQL database. You can use {@link #getInstance(String)}
 * to obtain a new instance for the given database name. The specific instance returned depends on
 * the properties file configuration. You can obtain DAO's for the DAO factory instance using the 
 * DAO getters.
 * <p>
 * This class requires a properties file named 'dao.properties' in the classpath with among others
 * the following properties:
 * <pre>
 * name.url *
 * name.driver
 * name.username
 * name.password
 * </pre>
 * Those marked with * are required, others are optional and can be left away or empty. Only the
 * username is required when any password is specified.
 * <ul>
 * <li>The 'name' must represent the database name in {@link #getInstance(String)}.</li>
 * <li>The 'name.url' must represent either the JDBC URL or JNDI name of the database.</li>
 * <li>The 'name.driver' must represent the full qualified class name of the JDBC driver.</li>
 * <li>The 'name.username' must represent the username of the database login.</li>
 * <li>The 'name.password' must represent the password of the database login.</li>
 * </ul>
 * If you specify the driver property, then the url property will be assumed as JDBC URL. If you
 * omit the driver property, then the url property will be assumed as JNDI name. When using JNDI
 * with username/password preconfigured, you can omit the username and password properties as well.
 * <p>
 * Here are basic examples of valid properties for a database with the name 'javabase':
 * <pre>
 * javabase.jdbc.url = jdbc:mysql://localhost:3306/javabase
 * javabase.jdbc.driver = com.mysql.jdbc.Driver
 * javabase.jdbc.username = java
 * javabase.jdbc.password = d$7hF_r!9Y
 * </pre>
 * <pre>
 * javabase.jndi.url = jdbc/javabase
 * </pre>
 * Here is a basic use example:
 * <pre>
 * DAOFactory javabase = DAOFactory.getInstance("javabase.jdbc");
 * UserDAO userDAO = javabase.getUserDAO();
 * </pre>
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public abstract class TwitterDAOFactory {

    // Constants ----------------------------------------------------------------------------------
    Connection connection = null;
    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_DRIVER = "driver";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_PASSWORD = "password";

    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns a new DAOFactory instance for the given database name.
     * @param name The database name to return a new DAOFactory instance for.
     * @return A new DAOFactory instance for the given database name.
     * @throws DAOConfigurationException If the database name is null, or if the properties file is
     * missing in the classpath or cannot be loaded, or if a required property is missing in the
     * properties file, or if either the driver cannot be loaded or the datasource cannot be found.
     */
    protected static TwitterDAOFactory getInstance(String name) throws DAOConfigurationException {
        if (name == null) {
            throw new DAOConfigurationException("Database name is null.");
        }

        DAOProperties properties = new DAOProperties(name);
        String url = properties.getProperty(PROPERTY_URL, true);
        String driverClassName = properties.getProperty(PROPERTY_DRIVER, false);
        String password = properties.getProperty(PROPERTY_PASSWORD, false);
        String username = properties.getProperty(PROPERTY_USERNAME, password != null);
        TwitterDAOFactory instance;

        // If driver is specified, then load it to let it register itself with DriverManager.
        if (driverClassName != null) {
            try {
                Class.forName(driverClassName);
            } catch (ClassNotFoundException e) {
                throw new DAOConfigurationException(
                    "Driver class '" + driverClassName + "' is missing in classpath.", e);
            }
            instance = new DriverManagerDAOFactory(url, username, password);
        }

        // Else assume URL as DataSource URL and lookup it in the JNDI.
        else {
            DataSource dataSource;
            try {
                dataSource = (DataSource) new InitialContext().lookup(url);
            } catch (NamingException e) {
                throw new DAOConfigurationException(
                    "DataSource '" + url + "' is missing in JNDI.", e);
            }
            if (username != null) {
                instance = new DataSourceWithLoginDAOFactory(dataSource, username, password);
            } else {
                instance = new DataSourceDAOFactory(dataSource);
            }
        }

        return instance;
    }

    /**
     * Returns a connection to the database. Package private so that it can be used inside the DAO
     * package only.
     * @return A connection to the database.
     * @throws SQLException If acquiring the connection fails.
     */
    abstract public Connection getConnection() throws SQLException;

    // DAO implementation getters -----------------------------------------------------------------

    /**
     * Returns the User DAO associated with the current DAOFactory.
     * @return The User DAO associated with the current DAOFactory.
     */
    public UserDAO getUserDAO() {
        return new UserDAOJDBC(this);
    }
    
    public TweetDAO getTweetDAO() {
        return new TweetDAOJDBC(this);
    }
    
    public HashtagDAO getHashtagDAO()
    {
        return new HashtagDAOJDBC(this);
    }
    
    public MediaDAO getMediaDAO()
    {
        return new MediaDAOJDBC(this);
    }
    
    public UrlDAO getUrlDAO()
    {
        return new UrlDAOJDBC(this);
    }
    
    public TwitterKeyDAO getTwitterKeyDAO()
    {
        return new TwitterKeyDAOJDBC(this);
    }

    public UsersRetweetDAO getUsersRetweetDAO()
    {
        return new UsersRetweetDAOJDBC(this);
    }
    
    public UserInteractionDAO getUserInteractionDAO()
    {
        return new UserInteractionDAOJDBC(this);
    }
    // You can add more DAO implementation getters here.
    public void close() throws SQLException
    {
        if(connection != null && !connection.isClosed())
            connection.close();
    }
}

// Default DAOFactory implementations -------------------------------------------------------------

/**
 * The DriverManager based DAOFactory.
 */
class DriverManagerDAOFactory extends TwitterDAOFactory {
    private String url;
    private String username;
    private String password;

    DriverManagerDAOFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException 
    {
        if(connection == null || connection.isClosed())
            connection = DriverManager.getConnection(url, username, password);
        return connection;
    }
}

/**
 * The DataSource based DAOFactory.
 */
class DataSourceDAOFactory extends TwitterDAOFactory {
    private DataSource dataSource;

    DataSourceDAOFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException 
    {
        if(connection == null || connection.isClosed())
            connection = dataSource.getConnection();
        return connection;
    }
}

/**
 * The DataSource-with-Login based DAOFactory.
 */
class DataSourceWithLoginDAOFactory extends TwitterDAOFactory {
    private DataSource dataSource;
    private String username;
    private String password;

    DataSourceWithLoginDAOFactory(DataSource dataSource, String username, String password) {
        this.dataSource = dataSource;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException 
    {
        if(connection == null || connection.isClosed())
            connection = dataSource.getConnection(username, password);
        return connection;
    }
}
