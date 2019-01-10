/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.utils;

import java.util.Objects;

/**
 * Par comparable generico.
 * 
 * @author Javier Sanz-Cruzado Puig, Monica Villanueva Aylagas
 */
public class Par<H extends Comparable<H>,M extends Comparable<M>> implements Comparable<Par<H,M>>
{
	/**
	 * Primer elemento de la terna. Es de tipo H
	 */
	private H t;
	
	/**
	 * Segundo elemento de la terna. Es de tipo M
	 */
	private M i;
	
	/**
	 * Constructor de la clase terna
	 * @param t Primer miembro de la terna
	 * @param i Segundo miembro de la terna
	 */
	public Par(H t, M i)
	{
		this.t = t;
		this.i = i;

	}

	/**
	 * Devuelve el primer elemento de la terna
	 * @return primer elemento de la terna
	 */
	public H getFirst()
	{
		return this.t;
	}
	
	/**
	 * Devuelve el segundo elemento de la terna
	 * @return segundo elemento de la terna
	 */
	public M getSecond()
	{
		return this.i;
	}
	

	@Override
	public int compareTo(Par<H, M> obj) 
	{
            int comparation = this.getSecond().compareTo(obj.getSecond());
            if(comparation == 0)
            {
                    return this.getFirst().compareTo(obj.getFirst());
            }
            else
                    return comparation;

        }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Par<?, ?> other = (Par<?, ?>) obj;
        if(other.getFirst() == this.getFirst() && other.getSecond() == this.getSecond())
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.t);
        hash = 37 * hash + Objects.hashCode(this.i);
        return hash;
    }
}