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
import es.uam.eps.ir.twittercollector.database.dao.data.UserDAO;
import es.uam.eps.ir.twittercollector.database.data.User;
import es.uam.eps.ir.twittercollector.database.data.UsersRetweet;
import es.uam.eps.ir.twittercollector.exception.TwitterCollectorException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 *
 * @author Javier
 */
public class FollowsGraph 
{
    public static void main(String args[]) throws TwitterCollectorException
    {
        BufferedWriter bw = null;
        TwitterDB.startDB(args[1]);
        TwitterDB twdb = TwitterDB.getInstance();
        UserDAO userdao = twdb.getUserDAO();
        
        List<User> ls = userdao.list();
        try
        {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[0])));
            
            for(User user : ls)
            {
                Long userid = user.getUserId();
                List<Long> following = userdao.getFollowing(userid);
                for(Long followed : following)
                    bw.write(userid + "\t" + followed + "\n");
            }
            
            bw.close();
        }
        catch(IOException io)
        {
            io.printStackTrace();
        }
        
    }
}
