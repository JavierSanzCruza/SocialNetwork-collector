/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.utils.exceptions;

/**
 * Exception that will be launched when a configuration file data is accessed without
 * having read it before.
 * @author Javier Sanz-Cruzado
 */
public class InvalidConfigurationFileException extends Exception {
    // Constants ----------------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Constructs a InvalidConfigurationFileException with the given detail message.
     * @param message The detail message of the InvalidConfigurationFileException.
     */
    public InvalidConfigurationFileException(String message) {
        super(message);
    }

    /**
     * Constructs a InvalidConfigurationFileException with the given root cause.
     * @param cause The root cause of the InvalidConfigurationFileException.
     */
    public InvalidConfigurationFileException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a InvalidConfigurationFileException with the given detail message and root cause.
     * @param message The detail message of the InvalidConfigurationFileException.
     * @param cause The root cause of the InvalidConfigurationFileException.
     */
    public InvalidConfigurationFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
