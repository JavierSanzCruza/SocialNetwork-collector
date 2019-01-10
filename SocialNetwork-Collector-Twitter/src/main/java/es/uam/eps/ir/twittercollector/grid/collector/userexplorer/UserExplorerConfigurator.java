/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.userexplorer;

import es.uam.eps.ir.twittercollector.userexplorer.UserExplorer;
import java.io.Serializable;

/**
 * Configures a expiration mechanism.
 * @author Javier Sanz-Cruzado Puig
 * @param <U> Type of the users.
 * @param <I> Type of the information pieces.
 * @param <P> Type of the parameters.
 * @see es.uam.eps.ir.socialnetwork.informationpropagation.expiration.ExpirationMechanism

 */
public interface UserExplorerConfigurator<U extends Serializable,I extends Serializable,P> 
{
    /**
     * Configures a expiration mechanism for the non-propagated pieces of information
     * @param params the parameters of the mechanism.
     * @return the expiration mechanism.
     */
    public UserExplorer configure(UserExplorerParamReader params);
}
