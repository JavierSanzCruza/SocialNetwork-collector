/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.partition.main;

import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOFactory;
import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOHandler;
import es.uam.eps.ir.twittercollector.database.data.Tweet;
import es.uam.eps.ir.twittercollector.database.data.User;
import java.io.*;
import java.util.List;
/**
 *
 * @author Javier
 */
public class userIndexGenerator 
{
    public static void main(String args[])
    {
        TwitterDAOHandler dh = TwitterDAOHandler.getInstance();
        dh.addFactory("twitter4.jdbc");
        
        BufferedWriter bw = null;
        
        TwitterDAOFactory daoF = dh.getFactory("twitter4.jdbc");
        
        List<User> tws = daoF.getUserDAO().list();
        try
        {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:/Users/Javier/Documents/LABO/javier/Projects/TwitterCollector/res/userIndex.txt")));
            for(User t : tws)
            {
                bw.write(t.getUserId() + "\r\n");
            }
            System.out.println("FINISHED");
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if(bw != null)
            {
                try
                {
                    bw.close();
                }
                catch(IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
}
