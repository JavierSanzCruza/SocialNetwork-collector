/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.partition.main;

import es.uam.eps.ir.twittercollector.database.dao.TwitterDAOHandler;
import es.uam.eps.ir.twittercollector.database.dao.data.UserDAO;
import es.uam.eps.ir.twittercollector.database.data.User;
import java.util.List;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class PartitionGeneratorFollows 
{
    public static void main(String[] args)
    {
        if(args.length < 4)
        {
            System.out.println("ERROR. Invalid arguments");
            System.out.println("1. Train database");
            System.out.println("2. Test database");
            System.out.println("3. Train file");
            System.out.println("4. Train file");
            return;
        }
        
        String trainDb = args[0];
        String testDb = args[1];
        
        TwitterDAOHandler tdaoh = TwitterDAOHandler.getInstance();
        tdaoh.addFactory(trainDb);
        tdaoh.addFactory(testDb);
        
        UserDAO trainDAO = tdaoh.getFactory(trainDb).getUserDAO();
        UserDAO testDAO = tdaoh.getFactory(testDb).getUserDAO();
        
        List<User> users = testDAO.list();
        
        try(BufferedWriter trainBW = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[2])));
            BufferedWriter testBW = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[3]))))
        {
            for(User user : users)
            {
                Long userId = user.getUserId();
                List<Long> followingTrain = trainDAO.getFollowing(userId);
                List<Long> followingTest = testDAO.getFollowing(userId);
                for(Long followed : followingTest)
                {
                    if(followingTrain.contains(followed))
                    {
                        trainBW.write(userId+"\t"+followed+"\t"+1.0+"\n");
                    }
                    else if(followingTrain.size() > 0)
                    {
                        testBW.write(userId+"\t"+followed+"\t"+1.0+"\n");
                    }
                }
            }
        }
        catch (IOException ex) 
        {
            Logger.getLogger(PartitionGeneratorFollows.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
