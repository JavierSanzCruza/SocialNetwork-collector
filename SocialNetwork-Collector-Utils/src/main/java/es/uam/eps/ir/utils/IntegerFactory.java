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
 * Factoria de enteros
 * @author Javier Sanz-Cruzado Puig y Monica Villanueva Aylagas
 */
public class IntegerFactory implements Factory<Integer>
{
	/**
	 * Numero de elementos creados
	 */
	private int count = 0;

	@Override
	public Integer create() 
	{
		count++;
		return count-1;
	}
}
