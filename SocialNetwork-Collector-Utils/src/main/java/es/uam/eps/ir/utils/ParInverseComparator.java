/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.utils;

import java.util.Comparator;



/**
 * Comparador para ordenacion de resultados
 * @author Javier Sanz-Cruzado Puig, Monica Villanueva Aylagas
 */
public class ParInverseComparator<H extends Comparable<H>, M extends Comparable<M>> implements Comparator<Par<H, M>> 
{

	@Override
	public int compare(Par<H, M> o1, Par<H, M> o2) 
	{
		return o2.compareTo(o1);
	}
}
