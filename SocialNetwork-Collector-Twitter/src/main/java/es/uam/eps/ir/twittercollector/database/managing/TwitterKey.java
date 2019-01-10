/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.database.managing;

import java.util.*;
/**
 * Class that represents an API Key in the database
 * @author Javier Sanz-Cruzado
 */
public class TwitterKey 
{
    /**
     * Key identifier
     */
    Integer keyId;
    /**
     * Consumer key of the API key
     */
    String consumerKey;
    /**
     * Consumer secret of the API key
     */
    String consumerSecret;
    /**
     * Access token of the API key
     */
    String accessToken;
    /**
     * Access token secret of the API Key
     */
    String accessTokenSecret;
    /**
     * Number of requests used since startUseTime
     */
    Integer numRequestUsed;

    
    public TwitterKey()
    {
        this.numRequestUsed = 0;
    }
    public Integer getKeyId() {
        return keyId;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public Integer getNumRequestUsed() {
        return numRequestUsed;
    }

    public void setKeyId(Integer keyId) {
        this.keyId = keyId;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    public void setNumRequestUsed(Integer numRequestUsed) {
        this.numRequestUsed = numRequestUsed;
    }
    
    public void increaseUses()
    {
        this.numRequestUsed++;
    }
}
