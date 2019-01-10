/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.utils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cleans text for storing in a database.
 * @author Javier Sanz-Cruzado Puig
 */
public class TextCleaner 
{
    /**
     * Cleans a string of text for storing it at the database.
     * @param text Text to clean.
     * @return The clean text.
     * @throws UnsupportedEncodingException if something fails at encoding.
     */
    public static String cleanText(String text) throws UnsupportedEncodingException 
    {
        if(text == null)
            return "";
        byte[] utf8Bytes = text.getBytes("UTF-8");
        String utf8tweet = new String(utf8Bytes, "UTF-8");

        Pattern unicodeOutliers = Pattern.compile("[^\\x00-\\x7F]",
                Pattern.UNICODE_CASE | Pattern.CANON_EQ
                        | Pattern.CASE_INSENSITIVE);
        Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(utf8tweet);


        utf8tweet = unicodeOutlierMatcher.replaceAll("");
        return utf8tweet;
    }
}
