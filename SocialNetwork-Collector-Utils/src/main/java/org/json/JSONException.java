/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.json;

/**
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 *
 * @author JSON.org
 * @version 2014-05-03
 */
public class JSONException extends RuntimeException {
    private static final long serialVersionUID = 0;
    private Throwable cause;

    /**
     * Constructs a JSONException with an explanatory message.
     *
     * @param message
     *            Detail about the reason for the exception.
     */
    public JSONException(String message) {
        super(message);
    }

    /**
     * Constructs a new JSONException with the specified cause.
     * @param cause The cause.
     */
    public JSONException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    /**
     * Returns the cause of this exception or null if the cause is nonexistent
     * or unknown.
     *
     * @return the cause of this exception or null if the cause is nonexistent
     *          or unknown.
     */
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
