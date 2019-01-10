/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.partition.main;

/**
 *
 * @author nets
 */
public enum PartitionMethod 
{
    TEMPORAL_DATE, TEMPORAL_PERCENT, TEMPORAL_DATE_SUBSAMPLE, TEMPORAL_PERCENT_SUBSAMPLE, RANDOM, TWO_DATABASES
}
