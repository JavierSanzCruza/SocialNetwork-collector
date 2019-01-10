/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.database.data;

import es.uam.eps.ir.tumblrcollector.util.Direction;

/**
 *
 * @author Javier
 */
public class Tree 
{
    /**
     * Node to expand
     */
    private Long parent;
    /**
     * Node in the expansion list
     */
    private Long child;
    /**
     * Link Type
     */
    private LinkType type;
    /**
     * Direction
     */
    private Direction direction;

    public Long getParent() 
    {
        return parent;
    }

    public void setParent(Long parent) 
    {
        this.parent = parent;
    }

    public Long getChild() 
    {
        return child;
    }

    public void setChild(Long child) 
    {
        this.child = child;
    }

    public LinkType getType() 
    {
        return type;
    }

    public void setType(LinkType type) 
    {
        this.type = type;
    }

    public Direction getDirection() 
    {
        return direction;
    }

    public void setDirection(Direction direction) 
    {
        this.direction = direction;
    }  
}
