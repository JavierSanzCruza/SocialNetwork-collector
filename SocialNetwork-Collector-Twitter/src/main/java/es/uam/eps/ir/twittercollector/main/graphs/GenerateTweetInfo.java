/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.main.graphs;

import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOFactory;
import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOHandler;
import es.uam.eps.ir.twittercollector.database.dao.data.TweetDAO;
import es.uam.eps.ir.twittercollector.database.dao.data.jdbc.TweetDAOJDBC;
import java.sql.ResultSet;
import java.io.*;
import java.sql.SQLException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
/**
 *
 * @author Javier
 */
public class GenerateTweetInfo
{
    public static void main(String args[]) throws IOException, SQLException
    {
        if(args.length < 3)
        {
            System.out.println("Invalid arguments");
            System.out.println("Usage: <database> <file> <tweetId>");
            return;
        }
        
        TwitterDAOHandler tdaoh = TwitterDAOHandler.getInstance();
        tdaoh.addFactory(args[0]);
        TwitterDAOFactory twdao = tdaoh.getFactory(args[0]);
        TweetDAO tweetDAO = new TweetDAOJDBC(twdao);
        ResultSet resultSet = tweetDAO.listTweetInfo(new Long(args[2]));
        

        Appendable appendable = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1])));
        try (CSVPrinter printer = CSVFormat.DEFAULT.withDelimiter('\t').withHeader(resultSet).print(appendable)) 
        {
            printer.printRecords(resultSet);
        }
        
    }
}
