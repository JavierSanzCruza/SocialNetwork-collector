/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.partition.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class CleanTestFiles 
{
    public static void main(String args[])
    {
        if(args.length < 3)
        {
            System.out.println("Usage: <originFolder> <destinyFolder> <file1> <file2> ...");
            return;
        }
        for(int i = 2; i < args.length; ++i)
        {
            Set<String> visited = new HashSet<>();

            try(BufferedReader br = new BufferedReader(new InputStreamReader (new FileInputStream(args[0] + args[i] + "train.csv")));
                BufferedReader brTest = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]+args[i]+"test.csv")));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(args[1]+ args[i] + "test.csv"))))
            {
                System.out.println(args[0] + args[i] + "test.csv");
                String line;
                while((line = br.readLine()) != null)
                {
                    
                    String[] split = line.split("\t");
                    visited.add(split[0]);
                    visited.add(split[1]);
                }
                
                while((line = brTest.readLine()) != null)
                {
                    String[] split = line.split("\t");
                    if(visited.contains(split[0]) && visited.contains(split[1]))
                    {
                        bw.write(line + "\n");
                    }
                }
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(CleanPartitionFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println("File " + args[i] + " cleaned");
        }   
    }
}
