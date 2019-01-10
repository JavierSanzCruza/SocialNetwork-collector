/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.exception;

/**
 * Exception that indicates an error in the configuration of the collector
 * @author Javier Sanz-Cruzado
 */
public class PartitionGeneratorConfigurationErrorException extends TwitterCollectorException 
{
    // Constants ----------------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Constructs a TwitterConfigurationErrorException with the given detail message.
     * @param message The detail message of the TwitterConfigurationErrorException.
     */
    public PartitionGeneratorConfigurationErrorException(String message) {
        super(message);
    }

    /**
     * Constructs a TwitterConfigurationErrorException with the given root cause.
     * @param cause The root cause of the TwitterConfigurationErrorException.
     */
    public PartitionGeneratorConfigurationErrorException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a TwitterConfigurationErrorException with the given detail message and root cause.
     * @param message The detail message of the TwitterConfigurationErrorException.
     * @param cause The root cause of the TwitterConfigurationErrorException.
     */
    public PartitionGeneratorConfigurationErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
