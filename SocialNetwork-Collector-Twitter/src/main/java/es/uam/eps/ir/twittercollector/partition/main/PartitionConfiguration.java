/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.partition.main;

import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import es.uam.eps.ir.twittercollector.exception.TwitterConfigurationErrorException;
import es.uam.eps.ir.twittercollector.partition.DateTemporalPartition;
import es.uam.eps.ir.twittercollector.partition.*;
import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Class for reading the configuration file for the Partition Generator.
 * @author Javier Sanz-Cruzado
 */
public class PartitionConfiguration 
{
    /**
     * Indicates the mode of processing the crawling.
     */
    private PartitionMethod method;
    /**
     * Database name in the configuration.
     */
    private String database;
    /**
     * Second database name in the configuration (test database).
     */
    private String database2;
    /**
     * Subsample file.
     */
    private String subsample;
    /**
     * Route of the files we want to store the partitions in.
     */
    private String outputRoute;
    /**
     * Number of neighbours to add to the queue each iteration.
     */
    private Double percentage;
    /**
     * Oldest date for each tweet (not retweet).
     */
    private Date date;
    /**
     * Partition.
     */
    private Partition partition;
    
    
    /***************************** CONSTANTS ***************************************/
    
    /**
     * Name of the field for retrieving <code>method</code>.
     */
    private static final String METHOD_FIELD = "method";
    /**
     * Value for using the Random partition method.
     */
    private static final String METHOD_RANDOM_VALUE = "random";
    /**
     * Value for using the Temporal partition based in a given date.
     */
    private static final String METHOD_TEMPORALDATE_VALUE = "timeDate";
    /**
     * Value for using the Temporal partition based in a given percentage.
     */
    private static final String METHOD_TEMPORALPERCENT_VALUE = "timePercent";
    /**
     * Value for using the Temporal partition based in a given date.
     */
    private static final String METHOD_TEMPORALDATESUBSAMPLE_VALUE = "timeDateSubsample";
    /**
     * Value for using the Temporal partition based in a given percentage.
     */
    private static final String METHOD_TEMPORALPERCENTSUBSAMPLE_VALUE = "timePercentSubsample";
    /**
     * Value for using the partition based in a train database and a test database.
     */
    private static final String METHOD_TWODATABASES_VALUE = "twoDatabases";
    /**
     * Name of the field for retrieving <code>outputRoute</code>.
     */
    private static final String OUTPUTROUTE_FIELD = "outputRoute";
    /**
     * Name of the field for retrieving <code>date</code>.
     */
    private static final String DATE_FIELD = "date";
    /**
     * Name of the field for retrieving <code>database</code>.
     */
    private static final String DATABASE_FIELD = "database";
    /**
     * Name of the field for retrieving <code>database2</code>.
     */
    private static final String DATABASE2_FIELD = "database2";
    /**
     * Name of the field for retrieving <code>subsample</code>.
     */
    private static final String SUBSAMPLE_FIELD = "subsample";
    /**
     * Name of the field for retrieving <code>percentage</code>.
     */
    private static final String PERCENTAGE_FIELD = "percentage";
    /**
     * Name of train output file.
     */
    private static final String TRAIN_FILE = "train.csv";
    /**
     * Name of the test output file.
     */
    private static final String TEST_FILE = "test.csv";
    
    /**
    * Configures the twitter collector.
    * @param conffile Route to the configuration file.
     * @throws es.uam.eps.ir.twittercollector.exception.TwitterCollectorException
     * @throws java.text.ParseException
    */
    public void configure(String conffile) throws TwitterCollectorException, ParseException
    {
        BufferedReader br;

        HashMap<String, String> aux = new HashMap<>();
        try
        {
            // Reading the configuration file
            br = new BufferedReader(new InputStreamReader(new FileInputStream(conffile)));

            String text;
            while((text = br.readLine()) != null)
            {
                String[] str = text.split("\t");
                if(str.length != 2)
                {
                    throw(new TwitterConfigurationErrorException("Invalid configuration file"));
                }

                aux.put(str[0], str[1]);
            }

            br.close();

            // Get the parameters
            String chain;
            
            // partition method
            chain = aux.get(METHOD_FIELD);
            if(chain != null)
            {
                switch (chain) 
                {
                    case METHOD_RANDOM_VALUE:
                        method = PartitionMethod.RANDOM;
                        break;
                    case METHOD_TEMPORALDATE_VALUE:
                        method = PartitionMethod.TEMPORAL_DATE;
                        break;
                    case METHOD_TEMPORALPERCENT_VALUE:
                        method = PartitionMethod.TEMPORAL_PERCENT;
                        break;
                    case METHOD_TEMPORALPERCENTSUBSAMPLE_VALUE:
                        method = PartitionMethod.TEMPORAL_PERCENT_SUBSAMPLE;
                        break;
                    case METHOD_TEMPORALDATESUBSAMPLE_VALUE:
                        method = PartitionMethod.TEMPORAL_DATE_SUBSAMPLE;
                        break;
                    case METHOD_TWODATABASES_VALUE:
                        method = PartitionMethod.TWO_DATABASES;
                        break;
                    default:
                        throw(new TwitterConfigurationErrorException("Invalid configuration file. Invalid " + METHOD_FIELD));
                }
            }
            else
            {
                throw(new TwitterConfigurationErrorException("Invalid configuration file. " + METHOD_FIELD + " field missing"));
            }

            //database
            chain = aux.get(DATABASE_FIELD);
            if(chain != null)
            {
                database = chain;
            }
            else
            {
                throw(new TwitterConfigurationErrorException("Invalid configuration file. " + DATABASE_FIELD + " field missing"));
            }
            
            //output route
            chain = aux.get(OUTPUTROUTE_FIELD);
            if(chain != null)
            {
                outputRoute = chain;
            }
            else
            {
                throw(new TwitterConfigurationErrorException("Invalid configuration file. " + OUTPUTROUTE_FIELD + " field missing"));
            }

            //percentage
            chain = aux.get(PERCENTAGE_FIELD);
            if(chain == null && (method == PartitionMethod.TEMPORAL_PERCENT || method == PartitionMethod.RANDOM || method == PartitionMethod.TEMPORAL_PERCENT_SUBSAMPLE))
            {
                throw(new TwitterConfigurationErrorException("Invalid configuration file. " + PERCENTAGE_FIELD + " field missing"));
            }
            else if(chain != null)
            {
                percentage = new Double(chain);
                percentage /= 100.0;
                if((percentage < 0.0 || percentage > 1.0) && (method == PartitionMethod.TEMPORAL_PERCENT || method == PartitionMethod.RANDOM || method == PartitionMethod.TEMPORAL_PERCENT_SUBSAMPLE))
                {
                    throw(new TwitterConfigurationErrorException("Invalid configuration file. " + PERCENTAGE_FIELD + " must be a positive integer or 0"));
                }
            }         
            
            //date
            chain = aux.get(DATE_FIELD);
            if(chain == null && (method == PartitionMethod.TEMPORAL_DATE || method == PartitionMethod.TEMPORAL_DATE_SUBSAMPLE))
            {
                throw(new TwitterConfigurationErrorException("Invalid configuration file. " + DATE_FIELD + " field missing"));
            }
            else if(chain != null)
            {
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                date = df.parse(chain);
            }
            
            //Database 2
            chain = aux.get(DATABASE2_FIELD);
            if(chain == null && method == PartitionMethod.TWO_DATABASES)
            {
               throw(new TwitterConfigurationErrorException("Invalid configuration file. " + DATABASE2_FIELD + " field missing"));
            }
            else if(chain != null)
            {
                database2 = chain;
            }

            //Subsample file
            chain = aux.get(SUBSAMPLE_FIELD);
            if(chain == null && (method == PartitionMethod.TEMPORAL_DATE_SUBSAMPLE || method == PartitionMethod.TEMPORAL_PERCENT_SUBSAMPLE))
            {
                throw(new TwitterConfigurationErrorException("Invalid configuration file. " + SUBSAMPLE_FIELD + " field missing."));
            }
            else if(chain != null)
            {
                subsample = chain;
            }
            // Create the necessary folders for storing the output files
            File f = new File(this.outputRoute);
            if(f.exists() == false)
            {
                f.mkdirs();
            }
            
            
            
            
            if(method == PartitionMethod.TEMPORAL_DATE)
            {
                this.partition = new DateTemporalPartitionNoReps(database, date);
            }
            else if(method == PartitionMethod.TEMPORAL_PERCENT)
            {
                this.partition = new PercentageTemporalPartitionNoReps(database, percentage);
            }
            else if(method == PartitionMethod.TEMPORAL_DATE_SUBSAMPLE)
            {
                this.partition = new DateTemporalPartitionSubsample(database, subsample, date);
            }
            else if(method == PartitionMethod.TEMPORAL_PERCENT_SUBSAMPLE)
            {
                this.partition = new PercentageTemporalPartitionSubsample(database, subsample, percentage);
            }
            else if(method == PartitionMethod.TWO_DATABASES)
            {
                this.partition = new TwoDatabasesPartition(database, database2);
            }
            else
            {
                //TODO Configurar con el metodo aleatorio
                this.partition = new RandomPartition(database, percentage);
            }
        }
        catch(IOException ioe)
        {
            throw(new TwitterConfigurationErrorException(ioe));
        }
    }

    public PartitionMethod getMethod() {
        return method;
    }

    public String getDatabase() {
        return database;
    }

    public String getOutputRoute() {
        return outputRoute;
    }

    public Double getPercentage() {
        return percentage;
    }

    public Date getDate() {
        return date;
    }

    public Partition getPartition() {
        return partition;
    }

    public String getTrainFile() 
    {
        return outputRoute + TRAIN_FILE;
    }
    
    public String getTestFile()
    {
        return outputRoute + TEST_FILE;
    }
}
