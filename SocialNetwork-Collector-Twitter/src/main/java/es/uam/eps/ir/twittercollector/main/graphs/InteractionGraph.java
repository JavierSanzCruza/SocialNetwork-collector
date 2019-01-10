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
import es.uam.eps.ir.twittercollector.database.dao.data.UsersRetweetDAO;
import es.uam.eps.ir.twittercollector.database.data.UsersRetweet;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import java.io.*;
import java.util.*;

/**
 * Creates a file with the retweet graph reading it from the database
 * @author Javier Sanz-Cruzado
 */
public class InteractionGraph 
{
    public static void main(String args[]) throws TwitterCollectorException
    {
        BufferedWriter bw = null;
        TwitterDB.startDB(args[1]);
        TwitterDB twdb = TwitterDB.getInstance();
        UsersRetweetDAO usersrtdao = twdb.getUsersRTDAO();
        
        List<UsersRetweet> ls = usersrtdao.list();
        try
        {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[0])));
            
            for(UsersRetweet ur : ls)
            {
                bw.write(ur.creator + "," + ur.retweeter + "\n");
            }
            
            bw.close();
        }
        catch(IOException io)
        {
            io.printStackTrace();
        }
        
    }
}

