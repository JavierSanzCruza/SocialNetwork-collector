/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.jsonrep;

import java.io.*;
import java.util.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.json.JSONException;
import org.json.JSONObject;
import twitter4j.IDs;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;

/**
 * This class allows to store tweets and users in a JSON repository, to prevent the crawler
 * from accessing to the database everytime.
 * @author Javier
 */
public class JSONBackup 
{
    private static final int BUFFER_SIZE = 4096;
    private static final String REPOSITORY_ROUTE = "jsonrep";
    private static final String REPOSITORY_USER = "user";
    private static final String REPOSITORY_USER_FILE = REPOSITORY_USER + ".json";
    private static final String REPOSITORY_TWEETS = "tweets";
    private static final String REPOSITORY_TWEETS_FILE = REPOSITORY_TWEETS + ".zip";
    private static final String REPOSITORY_FOLLOWS = "friends";
    private static final String REPOSITORY_FOLLOWS_FILE = "friends.json";
    
    
    
    /**
     * Stores user information on JSON repository. If the user previously exists,
     * this function does nothing
     * @param status Tweet that contains user information
     */
    public static void backupStatusUser(Status status)
    {
        User user = status.getUser();
        File directory = new File(REPOSITORY_ROUTE);
        if(directory.exists() == false)
        {
            if(directory.mkdir() == true)
            {
                System.out.println("Creada carpeta " + REPOSITORY_ROUTE);
            }
        }
        
        
        String folder = REPOSITORY_ROUTE  + "/" + user.getId();
            directory = new File(folder);
            if(directory.exists() == false)
            {
                if(directory.mkdir() == true)
                {
                    System.out.println("Creada carpeta " + folder);
                };


                JSONObject jsonUser = null;
                try
                {
                    String jsonTweet = TwitterObjectFactory.getRawJSON(status);
                    JSONObject tweet = new JSONObject(jsonTweet);
                    jsonUser = tweet.getJSONObject("user");
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }

                BufferedWriter bw = null;

                try
                {
                    bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(folder + "/" + REPOSITORY_USER_FILE)));
                    if(jsonUser != null)
                        bw.write(jsonUser.toString());
                    bw.close();

                    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(folder + "/" + REPOSITORY_TWEETS_FILE));
                    zos.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        
    }
    
    public static void backupStatus(List<Status> statuses)
    {
        if(statuses == null || statuses.size() == 0)
        {
            return;
        }
        
        User user = statuses.get(0).getUser();
        backupStatusUser(statuses.get(0));
        ZipOutputStream zos = null;
        try
        {
            String folder = REPOSITORY_ROUTE + "/" + user.getId() + "/" + REPOSITORY_TWEETS_FILE;
            zos = new ZipOutputStream(new FileOutputStream(folder));
            for(Status status : statuses)
            {
                String jsonTweet = TwitterObjectFactory.getRawJSON(status);
                zos.putNextEntry(new ZipEntry(status.getId() + ".json"));
                
                int i = 0;
                byte[] b = jsonTweet.getBytes();
                zos.write(b);
                zos.closeEntry();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {
                if(zos != null)
                    zos.close();
            } catch (IOException ex) {
                Logger.getLogger(JSONBackup.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
  /*  public status void backupRetweetedStatus(Status status)
    {
        
    }*/
//    // Information storage -----------------------------------------------------
//    /**
//     * Backups a Status as a compressed JSON in res/JSON/Status/statusId.zip.
//     *
//     * @param status Status to be saved.
//     * @param backupRetweetedStatusJSON If true, the retweeted status will also
//     * be saved.
//     * @param backupRetweetedStatusAuthor If true, the retweeted status author
//     * user will also be saved.
//     */
//    public static void backupStatus(Status status, boolean backupRetweetedStatusJSON, boolean backupRetweetedStatusAuthor) {
//        if (status == null) {
//            return;
//        }
//        String json = DataObjectFactory.getRawJSON(status);
//        if (json == null) {
//            json = new JSONObject(status).toString();
//            if (json.equals("{}")) {
//                return;
//            }
//        }
//        backupStatusJSON(json, status.getId());
//        if (backupRetweetedStatusJSON || backupRetweetedStatusAuthor) {
//            if (status.isRetweet()) {
//                String rjson = getRetweetedStatusJSON(json);
//                if (rjson != null) {
//                    if (backupRetweetedStatusJSON) {
//                        backupStatusJSON(rjson, status.getRetweetedStatus().getId());
//                    }
//                    if (backupRetweetedStatusAuthor) {
//                        String rajson = getStatusAuthorJSON(rjson);
//                        if (rajson != null) {
//                            backupUserJSON(rajson, status.getRetweetedStatus().getUser().getId());
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Backups a Status JSON in res/JSON/Status/statusId.zip.
//     *
//     * @param json Status JSON representation.
//     * @param statusId Status id.
//     *
//     */
//    public static void backupStatusJSON(String json, Long statusId) {
//        if (json == null || json.equals("") || statusId == null) {
//            return;
//        }
//        Settings settings = Settings.getInstance();
//        backupJSON(json, settings.getDirectoryJsonStatusOutputZipPath(statusId), statusId.toString());
//    }
//
//    /**
//     * Backups a User as a compressed JSON in res/JSON/User/userId.zip. the JSON
//     * backup.
//     *
//     * @param user User to be saved in res/JSON/User/userId.zip.
//     */
//    public static void backupUser(User user) {
//        if (user == null) {
//            return;
//        }
//        String json = DataObjectFactory.getRawJSON(user);
//        if (json == null) {
//            json = new JSONObject(user).toString();
//            if (json.equals("{}")) {
//                return;
//            }
//        }
//        backupUserJSON(json, user.getId());
//    }
//
//    /**
//     * Backups a User JSON in res/JSON/User/userId.zip.
//     *
//     * @param json User JSON representation.
//     * @param userId User id.
//     */
//    public static void backupUserJSON(String json, Long userId) {
//        if (json == null || json.equals("") || userId == null) {
//            return;
//        }
//        Settings settings = Settings.getInstance();
//        backupJSON(json, settings.getDirectoryJsonUserOutputZipPath(userId), userId.toString());
//    }
//
//    /**
//     * Backups a followees' list as a compressed JSON in
//     * res/JSON/Follow/userId.zip. the JSON backup.
//     *
//     * @param followees List of user's followees' Ids.
//     * @param userId user whose followees' ids will be saved.
//     */
//    public static void backupFollowees(IDs followees, Long userId) {
//        if (followees == null || userId == null) {
//            return;
//        }
//        String json = DataObjectFactory.getRawJSON(followees);
//        if (json == null) {
//            json = new JSONObject(followees).toString();
//            if (json.equals("{}")) {
//                return;
//            }
//        }
//        backupFolloweesJSON(json, userId);
//    }
//
//    /**
//     * Backups a followees' list JSON as a compressed JSON in
//     * res/JSON/Follow/userId.zip.
//     *
//     * @param json List of user's followees' Ids in JSON representation.
//     * @param userId user whose followees' ids will be saved.
//     */
//    public static void backupFolloweesJSON(String json, Long userId) {
//        if (json == null || json.equals("") || userId == null) {
//            return;
//        }
//        Settings settings = Settings.getInstance();
//        backupJSON(json, settings.getDirectoryJsonFollowOutputZipPath(userId), userId.toString());
//    }
//
//    /**
//     * Backups a JSON in the corresponding path.
//     *
//     * @param json JSON to be saved.
//     * @param path Path to the zip file.
//     * @param entryName Entry name to store the JSON in the zip file.
//     */
//    public static void backupJSON(String json, String path, String entryName) {
//        if (json == null || json.equals("") || path == null || path.equals("") || entryName == null || entryName.equals("")) {
//            return;
//        }
//        FileOutputStream outputFileOutputStream = null;
//        try {
//            File outputFile = new File(path);
//            outputFileOutputStream = new FileOutputStream(outputFile);
//            ZipOutputStream outputZipOutputStream = new ZipOutputStream(outputFileOutputStream);
//            ZipEntry entry = new ZipEntry(entryName);
//            outputZipOutputStream.putNextEntry(entry);
//            outputZipOutputStream.write(json.getBytes());
//            outputZipOutputStream.closeEntry();
//            outputZipOutputStream.close();
//        } catch (IOException ex) {
//            IOUtil.getInstance().informUserAndLog("It was not possible to create the zip backup " + path, ex);
//        } finally {
//            try {
//                outputFileOutputStream.close();
//            } catch (IOException ex) {
//                IOUtil.getInstance().informUserAndLog("It was not possible to create the zip backup " + path, ex);
//            }
//        }
//    }
//
//    // Information recovery ----------------------------------------------------
//    /**
//     * Recovers a status from the JSON repository res/JSON/Status/.
//     *
//     * @param statusId Status id.
//     * @return A Status recovered from the repository or null if it doesn't
//     * exists.
//     */
//    public static Status recoverStatus(Long statusId) {
//        if (statusId == null || statusId < 0) {
//            return null;
//        }
//        Settings settings = Settings.getInstance();
//        String json = recoverJSON(settings.getDirectoryJsonStatusOutputZipPath(statusId), statusId.toString());
//        if (json != null) {
//            try {
//                return DataObjectFactory.createStatus(json);
//            } catch (TwitterException ex) {
//                return null;
//            }
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * Recovers a user from the JSON repository res/JSON/User/.
//     *
//     * @param userId User id.
//     * @return A User recovered from the repository or null if it doesn't
//     * exists.
//     */
//    public static User recoverUser(Long userId) {
//        if (userId == null || userId < 0) {
//            return null;
//        }
//        Settings settings = Settings.getInstance();
//        String json = recoverJSON(settings.getDirectoryJsonUserOutputZipPath(userId), userId.toString());
//        if (json != null) {
//            try {
//                return DataObjectFactory.createUser(json);
//            } catch (TwitterException ex) {
//                return null;
//            }
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * Recovers a user's followees list from the JSON repository
//     * res/JSON/Follow/.
//     *
//     * @param userId User id.
//     * @return A User followees list recovered from the repository or null if it
//     * doesn't exists.
//     */
//    public static IDs recoverFollowees(Long userId) {
//        if (userId == null || userId < 0) {
//            return null;
//        }
//        Settings settings = Settings.getInstance();
//        String json = recoverJSON(settings.getDirectoryJsonFollowOutputZipPath(userId), userId.toString());
//        if (json != null) {
//            try {
//                return DataObjectFactory.createIDs(json);
//            } catch (TwitterException ex) {
//                return null;
//            }
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * Recovers a JSON from the given path.
//     *
//     * @param path ZifFile path.
//     * @param entryName Entry name used to save the JSON.
//     * @return A String containing the recovered JSON or null if no JSON had
//     * been saved in path.
//     */
//    public static String recoverJSON(String path, String entryName) {
//        if (path == null || path.equals("") || entryName == null || entryName.equals("")) {
//            return null;
//        }
//        try {
//            ZipInputStream inputZipInputStream = new ZipInputStream(new FileInputStream(path));
//            ZipEntry entry = inputZipInputStream.getNextEntry();
//            if (entry == null) {
//                return null;
//            } else if (entry.getName().equals(entryName)) {
//                StringBuilder json = new StringBuilder("");
//                byte[] buffer = new byte[BUFFER_SIZE];
//                int bytesReaded;
//                try {
//                    while ((bytesReaded = inputZipInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
//                        json = json.append(new String(bytesReaded == BUFFER_SIZE ? buffer : Arrays.copyOf(buffer, bytesReaded)));
//                    }
//                    inputZipInputStream.closeEntry();
//                    inputZipInputStream.close();
//                    return json.toString();
//                } catch (IOException ex) {
//                    return null;
//                }
//            } else {
//                inputZipInputStream.closeEntry();
//                inputZipInputStream.close();
//                return null;
//            }
//        } catch (IOException ex) {
//            return null;
//        }
//    }
//
//    // Information deletion ----------------------------------------------------
//    /**
//     * Deletes a status from the JSON repository res/JSON/Status/.
//     *
//     * @param statusId Status id to be deleted.
//     */
//    public static void deleteStatus(long statusId) {
//        if (statusId < 0) {
//            return;
//        }
//        Settings settings = Settings.getInstance();
//        File statusFile = new File(settings.getDirectoryJsonStatusOutputZipPath(statusId));
//        statusFile.delete();
//    }
//
//    /**
//     * Deletes a user from the JSON repository res/JSON/User/.
//     *
//     * @param userId User id to be deleted.
//     */
//    public static void deleteUser(long userId) {
//        if (userId < 0) {
//            return;
//        }
//        Settings settings = Settings.getInstance();
//        File userFile = new File(settings.getDirectoryJsonUserOutputZipPath(userId));
//        userFile.delete();
//    }
//
//    /**
//     * Deletes a user's followees list from the JSON repository
//     * res/JSON/Follow/.
//     *
//     * @param userId User id whose followees list to be deleted.
//     */
//    public static void deleteFollowees(long userId) {
//        if (userId < 0) {
//            return;
//        }
//        Settings settings = Settings.getInstance();
//        File userFile = new File(settings.getDirectoryJsonFollowOutputZipPath(userId));
//        userFile.delete();
//    }
//
//    // JSON Edition ------------------------------------------------------------
//    /**
//     * Returns the retweeted status JSON for a status JSON.
//     *
//     * @param statusJSON Status JSON.
//     * @return Retweeted status JSON or null if error or status is not a
//     * retweet.
//     */
//    public static String getRetweetedStatusJSON(String statusJSON) {
//        return getSubJSONByLabel(statusJSON, "retweeted_status");
//    }
//
//    /**
//     * Returns the author user JSON for a status JSON.
//     *
//     * @param statusJSON Status JSON.
//     * @return author user JSON for a status JSON or null if error or status has
//     * no author.
//     */
//    public static String getStatusAuthorJSON(String statusJSON) {
//        // To avoid getting the retweetedStatus author instead of the status author,
//        // the user will be searched only in the original status*/
//        String retweetedStatusJSON = getRetweetedStatusJSON(statusJSON);
//        if (retweetedStatusJSON != null) {
//            return getSubJSONByLabel(statusJSON.replace(retweetedStatusJSON, ""), "user");
//        } else {
//            return getSubJSONByLabel(statusJSON, "user");
//        }
//    }
//
//    /**
//     * Returns a sub JSON contented in a JSON referenced by the label given or
//     * null in case of error.
//     *
//     * @param json Original JSON.
//     * @param label Sub JSON label.
//     * @return Sub JSON contained in the original JSON or null in case of error.
//     */
//    public static String getSubJSONByLabel(String json, String label) {
//        String jsonLabel = "\"" + label + "\":";
//        char oppeningBracket = '{';
//        char closingBracket = '}';
//        int labelPosition = json.indexOf(jsonLabel);
//        if (labelPosition == -1) {
//            return null;
//        } else {
//            int j = 0;
//            int openingBracketPosition = -1;
//            for (int i = labelPosition + jsonLabel.length(); i < json.length(); i++) {
//                if (json.charAt(i) == oppeningBracket) {
//                    if (openingBracketPosition == -1) {
//                        openingBracketPosition = i;
//                    }
//                    j++;
//                } else if (json.charAt(i) == closingBracket) {
//                    j--;
//                    if (j == 0) {
//                        if (openingBracketPosition == -1) {
//                            return null;
//                        } else {
//                            return json.substring(openingBracketPosition, i + 1);
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//}

    public static void backupUser(twitter4j.User user)
    {
        File directory = new File(REPOSITORY_ROUTE);
        if(directory.exists() == false)
        {
            if(directory.mkdir() == true)
            {
                System.out.println("Creada carpeta " + REPOSITORY_ROUTE);
            }
        }
        
        String folder = REPOSITORY_ROUTE  + "/" + user.getId();
        directory = new File(folder);
        if(directory.exists() == false)
        {
            if(directory.mkdir() == true)
            {
                System.out.println("Creada carpeta " + folder);
            };


            JSONObject jsonUser = null;
            try
            {    
                JSONObject tweet = new JSONObject(user);
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            } 

            BufferedWriter bw = null;

            try
            {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(folder + "/" + REPOSITORY_USER_FILE)));
                if(jsonUser != null)
                    bw.write(jsonUser.toString());
                bw.close();

                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(folder + "/" + REPOSITORY_TWEETS_FILE));
                zos.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        
    }
    public static void backupRetweetedUser(Status status)
    {
        User user = status.getRetweetedStatus().getUser();
        File directory = new File(REPOSITORY_ROUTE);
        if(directory.exists() == false)
        {
            if(directory.mkdir() == true)
            {
                System.out.println("Creada carpeta " + REPOSITORY_ROUTE);
            }
        }
        
        
        String folder = REPOSITORY_ROUTE  + "/" + user.getId();
        directory = new File(folder);
        if(directory.exists() == false)
        {
            if(directory.mkdir() == true)
            {
                System.out.println("Creada carpeta " + folder);
            };


            JSONObject jsonUser = null;
            try
            {
                String jsonTweet = TwitterObjectFactory.getRawJSON(status);
                JSONObject tweet = new JSONObject(jsonTweet);
                JSONObject retweet = tweet.getJSONObject("retweeted_status");
                jsonUser = retweet.getJSONObject("user");
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            } 

            BufferedWriter bw = null;

            try
            {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(folder + "/" + REPOSITORY_USER_FILE)));
                if(jsonUser != null)
                    bw.write(jsonUser.toString());
                bw.close();

                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(folder + "/" + REPOSITORY_TWEETS_FILE));
                zos.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    /**
     * Backups a list of retweets to a JSON Repository
     * @param statuses List of retweets
     */
    public static void backupRetweetStatus(List<Status> statuses) 
    {
        if(statuses == null || statuses.size() == 0)
        {
            return;
        }
        
        ZipOutputStream zos = null;
        try
        {
            for(Status status : statuses)
            {
                User user = status.getRetweetedStatus().getUser();
                backupRetweetedUser(status);
                String folder = REPOSITORY_ROUTE + "/" + user.getId() + "/" + REPOSITORY_TWEETS_FILE;
                zos = new ZipOutputStream(new FileOutputStream(folder));
                
                String jsonTweet = TwitterObjectFactory.getRawJSON(status);
                JSONObject tweet = new JSONObject(jsonTweet);
                JSONObject jsonUser = tweet.getJSONObject("retweeted_status");
                zos.putNextEntry(new ZipEntry(status.getRetweetedStatus().getId() + ".json"));

                int i = 0;
                byte[] b = jsonUser.toString().getBytes();
                zos.write(b);
                zos.closeEntry();
                zos.close();
                zos = null;
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {
                if(zos != null)
                    zos.close();
            } catch (IOException ex) {
                Logger.getLogger(JSONBackup.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static User recoverUser(Long userID)
    {
        try
        {
            String route = REPOSITORY_ROUTE + "/" + userID;
            String name = REPOSITORY_USER;
            User us = null;
            String json = getJSON(route, name);
            if(json != null)
                us = TwitterObjectFactory.createUser(json);

            return us;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Status recoverTweet(Long tweetID, Long userID)
    {
        try
        {
            String route = REPOSITORY_ROUTE + "/" + userID + "/" + REPOSITORY_TWEETS_FILE;
            String name = tweetID.toString();
            Status st = null;
            String json = getJSON(route, name);
            if(json != null)
                st = TwitterObjectFactory.createStatus(json);
            
            return st;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Recovers JSON Object from an existing file
     * @param route Route to the file. If the file is included in a zip file, 
     * the name of the zip file must be included in the route.
     * @param name Name of the file, without extension
     * @return A string representing the JSON Object if exists, null if not
     */
    public static String getJSON(String route, String name)
    {
        if(route.endsWith(".zip"))
        {
            return getJSONFromZipFile(route, name);
        }
        else
        {
            return getJSONFromFile(route, name);
        }
    }
    
    public static String getJSONFromFile(String route, String name)
    {
        BufferedReader reader = null;
        String json = "";
        String text;
        try
        {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(route + "/" + name + ".json")));
            while((text = reader.readLine()) != null)
            {
                json += text + "\n";
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
        return json;
    }
    
    public static String getJSONFromZipFile(String route, String name)
    {
         try {
            ZipInputStream inputZipInputStream = new ZipInputStream(new FileInputStream(route));
            ZipEntry entry = inputZipInputStream.getNextEntry();
            if (entry == null) {
                return null;
            } else if (entry.getName().equals(name + ".json")) {
                StringBuilder json = new StringBuilder("");
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesReaded;
                try {
                    while ((bytesReaded = inputZipInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        json = json.append(new String(bytesReaded == BUFFER_SIZE ? buffer : Arrays.copyOf(buffer, bytesReaded)));
                    }
                    inputZipInputStream.closeEntry();
                    inputZipInputStream.close();
                    return json.toString();
                } catch (IOException ex) {
                    return null;
                }
            } else {
                inputZipInputStream.closeEntry();
                inputZipInputStream.close();
                return null;
            }
        } catch (IOException ex) {
            return null;
        }
    }
}
