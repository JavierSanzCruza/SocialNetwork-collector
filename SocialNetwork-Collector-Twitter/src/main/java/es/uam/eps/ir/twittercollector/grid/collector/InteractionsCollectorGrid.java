/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector;

import es.uam.eps.ir.twittercollector.grid.collector.stopcondition.StopConditionParamReader;
import es.uam.eps.ir.twittercollector.grid.collector.interactionsfilter.InteractionFilterParamReader;
import es.uam.eps.ir.twittercollector.grid.collector.tweetcollector.TweetCollectorParamReader;
import es.uam.eps.ir.twittercollector.grid.collector.userexplorer.UserExplorerParamReader;
import es.uam.eps.ir.twittercollector.grid.ParametersReader;
import java.util.*;
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.ranksys.formats.parsing.Parsers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reads the grids for several algorithms.
 * @author Javier Sanz-Cruzado Puig
 */
public class InteractionsCollectorGrid extends ParametersReader
{
    /**
     * Identifier for the protocol
     */
    private final static String EXPLORER = "explorer";
    /**
     * Identifier for filters
     */
    private final static String FILTER = "filter";
    /**
     * Identifier for the filter
     */
    private final static String COLLECTOR = "collector";
    /**
     * Identifier for the stop condition
     */
    private final static String STOP = "stop";
    /**
     * Identifier for the database name
     */
    private final static String DATABASE = "database";
    /**
     * Identifier for the seed users
     */
    private final static String SEEDUSERS = "seedUsers";
    
    private UserExplorerParamReader explorerParams;
    private InteractionFilterParamReader filterParams;
    private TweetCollectorParamReader collectorParams;
    private Set<Long> seedUsers;
    private String database;
    private StopConditionParamReader stopParams;
    
    /**
     * The name of the file
     */
    private final String file;
    
    /**
     * Constructor
     * @param file File that contains the grid data 
     */
    public InteractionsCollectorGrid(String file)
    {
        this.file = file;
    }
    
    /**
     * Reads a XML document containing a grid
     */
    public void readDocument()
    {
        try
        {
            // First of all, obtain the XML Document
            File inputFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            
            Element parent = doc.getDocumentElement();
            parent.normalize();
            
            NodeList databasename = parent.getElementsByTagName(InteractionsCollectorGrid.DATABASE);
            this.database = databasename.item(0).getTextContent();
            
            NodeList userListName = parent.getElementsByTagName(InteractionsCollectorGrid.SEEDUSERS);
            userListName = userListName.item(0).getChildNodes();
            
            this.seedUsers = new HashSet<>();
            for(int i = 0; i < userListName.getLength(); ++i)
            {
                Node node = userListName.item(i);
                
                if(node.getNodeType() == Node.ELEMENT_NODE)
                {
                    long user = Parsers.lp.parse(node.getTextContent());
                    this.seedUsers.add(user);
                }
            }
            
            NodeList userExplorerList = parent.getElementsByTagName(InteractionsCollectorGrid.EXPLORER);
            Node explorer = userExplorerList.item(0);
            this.explorerParams = new UserExplorerParamReader();
            this.explorerParams.readExplorer((Element) explorer);
            
            NodeList interFilterList = parent.getElementsByTagName(InteractionsCollectorGrid.FILTER);
            Node filter = interFilterList.item(0);
            this.filterParams = new InteractionFilterParamReader();
            this.filterParams.readFilter((Element) filter);
            
            NodeList tweetCollectorList = parent.getElementsByTagName(InteractionsCollectorGrid.COLLECTOR);
            Node collector = tweetCollectorList.item(0);
            this.collectorParams = new TweetCollectorParamReader();
            this.collectorParams.readCollector((Element) collector);
            
            NodeList stopCondList = parent.getElementsByTagName(InteractionsCollectorGrid.STOP);
            Node stop = stopCondList.item(0);
            this.stopParams = new StopConditionParamReader();
            this.stopParams.readStop((Element) stop);          
        } 
        catch (ParserConfigurationException | SAXException | IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public UserExplorerParamReader getExplorerParams() {
        return explorerParams;
    }

    public InteractionFilterParamReader getFilterParams() {
        return filterParams;
    }

    public TweetCollectorParamReader getCollectorParams() {
        return collectorParams;
    }

    public Set<Long> getSeedUsers() {
        return seedUsers;
    }

    public String getDatabase() {
        return database;
    }

    public StopConditionParamReader getStopParams() {
        return stopParams;
    }

    public String getFile() {
        return file;
    }
        
    /**
     * Obtains a string detailing the simulation parameters.
     * @param num the index of the simulation.
     * @return a string detailing the simulation parameters.
     */
    public String printSimulation(int num)
    {
        String sim = "";
        sim += "Database: " + this.database + "\n";
        sim += "Seed users: \n";
        for(long u : this.seedUsers)
        {
            sim += "\tUser: " + u + "\n";
        }
        sim += "Explorer: " + this.explorerParams.printUserExplorer() + "\n";
        sim += "Collector: " + this.collectorParams.printTweetCollector() + "\n";
        sim += "Filter: " + this.filterParams.printInteractionsFilter() + "\n";
        sim += "Stop: " + this.stopParams.printStopCondition() + "\n";
        
        return sim;
        
    }


}
