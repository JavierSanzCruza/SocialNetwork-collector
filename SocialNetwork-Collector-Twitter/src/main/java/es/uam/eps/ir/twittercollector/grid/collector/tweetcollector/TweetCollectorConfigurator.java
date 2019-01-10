/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.tweetcollector;

import es.uam.eps.ir.twittercollector.tweetcollector.TweetCollector;

/**
 * Configures a expiration mechanism.
 * @author Javier Sanz-Cruzado Puig
 * @see es.uam.eps.ir.socialnetwork.informationpropagation.expiration.ExpirationMechanism
 */
public interface TweetCollectorConfigurator
{
    /**
     * Configures a expiration mechanism for the non-propagated pieces of information
     * @param params the parameters of the mechanism.
     * @return the expiration mechanism.
     */
    public TweetCollector configure(TweetCollectorParamReader params);
}
