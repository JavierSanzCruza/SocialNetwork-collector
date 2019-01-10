/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.manager;

import es.uam.eps.ir.tumblrcollector.exception.TumblrConfigurationErrorException;
import es.uam.eps.ir.tumblrcollector.util.Direction;
import es.uam.eps.ir.utils.ConfigurationFile;
import es.uam.eps.ir.utils.exceptions.ConfigurationFileNotReadyException;
import es.uam.eps.ir.utils.exceptions.InvalidConfigurationFileException;
import java.util.Date;

/**
 * Configuration settings for the Tumblr Crawler.
 * @author Javier
 */
public class TumblrConfiguration 
{
    /**
     * Processing type. 
     */
    private TumblrProcessing processing;
    /**
     * Direction of expansion of the tree.
     */
    private Direction direction;
    /**
     * Name of the database.
     */
    private String database;
    /**
     * Minimum number of users to retrieve during the crawling.
     */
    private Integer numMinUsers;
    /**
     * Maximum number of posts to retrieve for each blog.
     */
    private Integer numMaxPosts;
    /**
     * Maximum number of neighbours to retrieve for each user.
     */
    private Integer numNeighSBS;
    /**
     * Minimum date for retrieving posts.
     */
    private Date startDate;
    /**
     * Maximum date for retrieving posts.
     */
    private Date endDate;
    /**
     * Seed blog. The crawling will start from this blog.
     */
    private String seedUser;
    
    /**
     * Processing type label in configuration file.
     */
    private static final String PROCESSING_LABEL = "processing";
    /**
     * Direction of expansion label in configuration file.
     */
    private static final String DIRECTION_LABEL = "direction";
    /**
     * Database name label in configuration file.
     */
    private static final String DATABASE_LABEL = "database";
    /**
     * Minimum number of users label in configuration file.
     */
    private static final String NUMMINUSERS_LABEL = "numMinUsers";
    /**
     * Maximum number of posts for each blog label in configuration file.
     */
    private static final String NUMMAXPOSTS_LABEL = "numMaxPosts";
    /**
     * Maximum number of neighbours for each blog label in configuration file
     */
    private static final String NUMNEIGHSBS_LABEL = "numNeighSBS";
    /**
     * Start date label in configuration file.
     */
    private static final String STARTDATE_LABEL = "startDate";
    /**
     * End date label in configuration file.
     */
    private static final String ENDDATE_LABEL = "endDate";
    /**
     * Seed user label in configuration file.
     */
    private static final String SEEDUSER_LABEL = "seedUser";
    
    /**
     * Configures the Tumblr sampling.
     * @param confFile Configuration File route.
     * @throws es.uam.eps.ir.tumblrcollector.exception.TumblrConfigurationErrorException

     * @throws TumblrConfigurationErrorException if something fails at configuration file, or any compulsory field does not appear .
     */
    public void configure(String confFile) throws TumblrConfigurationErrorException
    {
        try
        {
            ConfigurationFile cf = new ConfigurationFile();
            cf.readConfigurationFile(confFile);

            // Storing the processing type for the Tumblr sampling
            String processingField = cf.getString(PROCESSING_LABEL);
            {
                if(processingField == null)
                {
                    throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + PROCESSING_LABEL + " not found");
                }
                
                processing = TumblrProcessing.fromString(processingField);
                if(processing == TumblrProcessing.ERROR)
                {
                    throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + PROCESSING_LABEL + " has an incorrect value");
                }
            }
            
            // Storing the direction of the sampling (following the links, the reverse direction, or both)
            String directionField = cf.getString(DIRECTION_LABEL);
            if(directionField == null)
            {
                throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + DIRECTION_LABEL + " not found");
            }

            direction = Direction.fromString(directionField);
            if(direction == Direction.ERROR)
            {
                throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + DIRECTION_LABEL + " has an incorrect value");
            }
            
            // Storing the database name
            database = cf.getString(DATABASE_LABEL);
            if(database == null)
            {
                throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + DATABASE_LABEL + " not found");
            }
            
            // Storing the minimum number of users to retrieve
            this.numMinUsers = cf.getInteger(NUMMINUSERS_LABEL);
            if(numMinUsers == null)
            {
                throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + NUMMINUSERS_LABEL + " not found");
            }
            else if(this.numMinUsers <= 0)
            {
                throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + NUMMINUSERS_LABEL + "has an incorrect value");
            }
            
            // Storing the maximum number of posts for each blog. This field is compulsory if processing catches a fixed number of posts, or it's a trade off between
            // the number of posts and the posts generated during a given amount of time.
            this.numMaxPosts = cf.getInteger(NUMMAXPOSTS_LABEL);
            if((this.processing.equals(TumblrProcessing.NUMPOSTS) || this.processing.equals(TumblrProcessing.MIXED)) && numMaxPosts == null)
            {
                throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + NUMMINUSERS_LABEL + " not found");
            }
            else if((this.processing.equals(TumblrProcessing.NUMPOSTS) || this.processing.equals(TumblrProcessing.MIXED)) && this.numMaxPosts <= 0)
            {
                throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + NUMMINUSERS_LABEL + "has an incorrect value");
            }
            
            // Storing the maximum number of neighbours to expand for the next level of the algorithm. If this number is > 0, only the specified number will be retrieved,
            // selected randomly from the neighbours of the user in expansion. If this number equals 0, then every neighbour will be expanded. If this number is smaller than
            // zero, the file is badly configured.
            this.numNeighSBS = cf.getInteger(NUMNEIGHSBS_LABEL);
            if(this.numNeighSBS == null)
            {
                throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + NUMNEIGHSBS_LABEL + " not found");
            }
            else if(this.numNeighSBS < 0)
            {
                throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + NUMNEIGHSBS_LABEL + " has an incorrect value");
            }
            
            // Storing the starting date for temporal sampling. If processing is temporal, then, this field is compulsory.
            this.startDate = cf.getDate(STARTDATE_LABEL, "dd-MM-yyyy HH:mm:ss z");
            if((this.processing.equals(TumblrProcessing.TEMPORAL) || this.processing.equals(TumblrProcessing.MIXED)) && startDate == null)
            {
                throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + STARTDATE_LABEL + " not found");
            }
            if((this.processing.equals(TumblrProcessing.TEMPORAL) || this.processing.equals(TumblrProcessing.MIXED)) && startDate.after(new Date()))
            {
                throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + STARTDATE_LABEL + " has an incorrect value");
            }
            
            // Storing the final date for temporal sampling. If processing is temporal, then, there are two options. If this date is not configured,
            // every post between the start date and the sampling date will be used. If it is configured, it will be a valid date only if endDate > startDate.
            this.endDate = cf.getDate(ENDDATE_LABEL, "dd-MM-yyyy HH:mm:ss z");
            if((this.processing.equals(TumblrProcessing.TEMPORAL) || this.processing.equals(TumblrProcessing.MIXED)) && endDate == null)
            {
                endDate = new Date();
            }
            else if ((this.processing.equals(TumblrProcessing.TEMPORAL) || this.processing.equals(TumblrProcessing.MIXED)) && endDate.before(startDate))
            {
                throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + ENDDATE_LABEL + " has an incorrect value");
            }
            
            this.seedUser = cf.getString(SEEDUSER_LABEL);
            if(this.seedUser == null)
            {
                throw new TumblrConfigurationErrorException("Invalid Configuration File. Field " + SEEDUSER_LABEL + " not found");
            }
        }
        catch(InvalidConfigurationFileException | ConfigurationFileNotReadyException ex)
        {
            throw new TumblrConfigurationErrorException(ex);
        }
    }

    
    public TumblrProcessing getProcessing() 
    {
        return processing;
    }

    public Direction getDirection() 
    {
        return direction;
    }

    public String getDatabase() 
    {
        return database;
    }

    public Integer getNumMinUsers() 
    {
        return numMinUsers;
    }

    public Integer getNumMaxPosts() 
    {
        return numMaxPosts;
    }

    public Integer getNumNeighSBS() 
    {
        return numNeighSBS;
    }

    public Date getStartDate() 
    {
        return startDate;
    }

    public Date getEndDate() 
    {
        return endDate;
    }

    public String getSeedUser() 
    {
        return seedUser;
    }
}
