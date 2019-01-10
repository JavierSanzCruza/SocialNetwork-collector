/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.database.dao.data;

import es.uam.eps.ir.tumblrcollector.database.data.LinkType;
import es.uam.eps.ir.tumblrcollector.database.data.Tree;
import es.uam.eps.ir.tumblrcollector.util.Direction;
import java.util.List;

/**
 *
 * @author Javier
 */
public interface TreeDAO 
{

    public Tree find(Long parent, Long child);
    public List<Tree> list();
    public List<Tree> listExpansion(Long parent) ;
    public List<Tree> listExpansion(Long parent, Direction dir);
    public void create(Long parent, Long child, LinkType type, Direction dir);
    public void update(Long parent, Long child, LinkType type, Direction dir);
    public void delete(Long parent, Long child);
}
