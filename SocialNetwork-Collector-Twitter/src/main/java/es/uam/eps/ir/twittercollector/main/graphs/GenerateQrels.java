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
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import es.uam.eps.ir.utils.Par;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GenerateQrels
{
    public static void main(String args[]) throws IOException, SQLException
    {
        if(args.length < 3)
        {
            System.out.println("Invalid arguments");
            System.out.println("Usage: <database> <file> <MinTweetId> <MaxTweetId> <systemName>");
            return;
        }
        
        HashMap<Long, Long> retweetCounter = new HashMap<>();
        
        TwitterDAOHandler tdaoh = TwitterDAOHandler.getInstance();
        tdaoh.addFactory(args[0]);
        TwitterDAOFactory twdao = tdaoh.getFactory(args[0]);
        TweetDAO tweetDAO = new TweetDAOJDBC(twdao);
        List<Par<Long, Long>> resultSet = tweetDAO.listRetweetsTimes(new Long(args[2]), new Long(args[3]));
        for(Par<Long, Long> par : resultSet)
        {
            retweetCounter.put(par.getFirst(), par.getSecond());
        }

        List<Tweet> notRetweets = tweetDAO.listNotRetweets(new Long(args[2]));
        for(Tweet tw : notRetweets)
        {
            if(retweetCounter.containsKey(tw.getTweetId()) == false)
            {
                retweetCounter.put(tw.getTweetId(), 0L);
            }
        }
        
        try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]))))
        {
            for(Long tweetId : retweetCounter.keySet())
            {
                bw.write(args[4] + "\t0\t" + tweetId + "\t" + retweetCounter.get(tweetId) +"\n");
            }
            bw.close();
        }
        catch(IOException ioe)
        {
            System.out.println(Arrays.toString(ioe.getStackTrace()));
        }        
    }
}