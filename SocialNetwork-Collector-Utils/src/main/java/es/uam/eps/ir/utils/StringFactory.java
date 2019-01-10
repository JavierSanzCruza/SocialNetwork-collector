/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.utils;

import org.apache.commons.collections15.Factory;

/**
 * Factoria de cadenas
 * @author Javier Sanz-Cruzado Puig y Monica Villanueva Aylagas
 */
public class StringFactory implements Factory<String>
{
	/**
	 * Numero de Strings generadas
	 */
	private int count = 0;
	@Override
	public String create() {
		String obj = Integer.toString(count);
		count++;
		return obj;
	}

}
