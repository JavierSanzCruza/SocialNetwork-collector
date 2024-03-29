/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.partition;

import es.uam.eps.ir.twittercollector.exception.*;

/**
 * Represents a partition that divides the tweets depending on the time they were created.
 * @author Javier Sanz-Cruzado
 */
public abstract class TemporalPartition extends Partition
{
    public TemporalPartition(String database) throws TwitterCollectorException 
    {
        super(database);
    }
}
