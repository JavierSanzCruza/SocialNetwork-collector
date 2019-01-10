/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.main.graphs;
import es.uam.eps.ir.twittercollector.database.TwitterDB;
import es.uam.eps.ir.twittercollector.database.data.Retweet;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import java.text.ParseException;
import java.util.*;
import java.io.*;
/**
 * Obtains a file with the retweet graph information
 * @author Javier Sanz-Cruzado
 */
public class RetweetGraph 
{
    /**
     * Main method. Obtains a file with the following format for each line:
     * <retweetId> <originalId> <timeIntervals>
     * @param args 
     */
    public static void main(String[] args) throws InterruptedException, ParseException, IOException
    {
        if(args.length < 4)
        {
            System.out.println("Invalid arguments");
            System.out.println("\tARGUMENT 1: Database");
            System.out.println("\tARGUMENT 2: Output file");
            System.out.println("\tARGUMENT 3: Time unit");
            System.out.print("\t\t-s\tSeconds\n\t\t-m\tMinutes\n\t\t-h\tHours\n\t\t-d\tDays\n");
            System.out.println("\tARGUMENT 4: Units > 0");
            return;
        }
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1])));
        //Initializing the database
        TwitterDB.startDB(args[0]);
        TwitterDB td;
        try
        {
            td = TwitterDB.getInstance();
        }
        catch(TwitterCollectorException tce)
        {
            System.err.println(tce.getMessage());
            return;
        }
        
        List<Retweet> rtList = td.getTweetDAO().getRetweetList();
        Long previousTweet = null;
        
        Integer units = Integer.parseInt(args[3]);
        if(units < 1)
        {
            System.out.println("Invalid arguments");
            System.out.println("\tARGUMENT 1: Database");
            System.out.println("\tARGUMENT 2: Output file");
            System.out.println("\tARGUMENT 3: Time unit");
            System.out.print("\t\t-s\tSeconds\n\t\t-m\tMinutes\n\t\t-h\tHours\n\t\t-d\tDays\n");
            System.out.println("\tARGUMENT 4: Units > 0");
            return;
        }
        
        int i = 0;
        for(Retweet rt : rtList)
        {
            ++i;
            Tweet original = td.getTweetDAO().find(rt.getOriginalTweet());
            Date originalDate = original.getCreated();
            
            Tweet retweet = td.getTweetDAO().find(rt.getRetweet());
            Date retweetDate = retweet.getCreated();
            
            long milli1 = originalDate.getTime();
            long milli2 = retweetDate.getTime();

            if(args[2].equals("-ms"))
            {
                bw.write(rt.getOriginalTweet() + "\t" + rt.getRetweet() + "\t" + (milli2 - milli1)/units + "\n");
            }
            else if(args[2].equals("-s"))
            {
                bw.write(rt.getOriginalTweet() + "\t" + rt.getRetweet() + "\t" + (milli2 - milli1)/(units*1000) + "\n");
            }
            else if(args[2].equals("-m"))
            {
                bw.write(rt.getOriginalTweet() + "\t" + rt.getRetweet() + "\t" + (milli2 - milli1)/(units*1000*60) + "\n");
            }
            else if(args[2].equals("-h"))
            {
                Long l = (milli2-milli1)/(units*1000*60*60);
                String cadena = rt.getOriginalTweet() + "\t" + rt.getRetweet() + "\t" + l + "\n";
                bw.write(cadena);
            }
            else if(args[2].equals("-d"))
            {
                bw.write(rt.getOriginalTweet() + "\t" + rt.getRetweet() + "\t" + (milli2-milli1)/(units*1000*60*60*24) + "\n");
            }
            else
            {
                System.out.println("ERROR. Invalid time unit");
                System.out.print("\t\t-s\tSeconds\n\t\t-m\tMinutes\n\t\t-h\tHours\n\t\t-d\tDays\n");
                return;
            }
        }
        
        bw.close();
    }
}
