/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.tumblrcollector.database.dao.data.jdbc;

import es.uam.eps.ir.tumblrcollector.database.dao.TumblrDAOFactory;
import es.uam.eps.ir.tumblrcollector.database.dao.TumblrDAOJDBC;
import es.uam.eps.ir.tumblrcollector.database.dao.data.TreeDAO;
import es.uam.eps.ir.tumblrcollector.database.data.LinkType;
import es.uam.eps.ir.tumblrcollector.database.data.Tree;
import es.uam.eps.ir.tumblrcollector.util.Direction;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Javier
 */
public class TreeDAOJDBC extends TumblrDAOJDBC<Tree> implements TreeDAO{

    public TreeDAOJDBC(TumblrDAOFactory daoFactory) 
    {
        super(daoFactory);
    }

    @Override
    public Tree find(Long parent, Long child) 
    {
        Object[] values = 
        {
            parent,
            child
        };
        
        return this.find(SQL_FIND, values);
    }

    @Override
    public List<Tree> list() 
    {
        return this.list(SQL_LIST);
    }

    @Override
    public List<Tree> listExpansion(Long parent) 
    {
        return listExpansion(parent, Direction.BOTH);
    }

    @Override
    public List<Tree> listExpansion(Long parent, Direction dir) 
    {
        if(dir.equals(Direction.BACKWARD))
        {
           return this.list(SQL_LIST_EXPANSION_BACKWARDS, parent);
        }
        else if(dir.equals(Direction.FORWARD))
        {
            return this.list(SQL_LIST_EXPANSION_FORWARD, parent);
        }
        else //if(dir.equals(Direction.BOTH))
        {
            return this.list(SQL_LIST_EXPANSION_BOTH, parent);
        }
    }

    @Override
    public void create(Long parent, Long child, LinkType type, Direction dir) 
    {
        Object[] values = 
        {
            parent,
            child,
            type.toString(),
            dir.toString()
        };
        
        this.create(SQL_CREATE, false, true, values);
    }

    @Override
    public void update(Long parent, Long child, LinkType type, Direction dir) 
    {
        Object[] values = 
        {
            type.toString(),
            dir.toString(),
            parent,
            child
        };
        
        this.update(SQL_UPDATE, true, values);
    }

    @Override
    public void delete(Long parent, Long child) 
    {
        Object[] values = 
        {
            parent,
            child
        };
        this.delete(SQL_DELETE, true, values);
    }
    
    @Override
    protected Tree map(ResultSet resultSet) throws SQLException 
    {
        Tree tree = new Tree();
        
        tree.setParent(resultSet.getLong("parent"));
        tree.setChild(resultSet.getLong("child"));
        tree.setDirection(Direction.fromString(resultSet.getString("direction")));
        tree.setType(LinkType.fromString(resultSet.getString("type")));
        
        return tree;
    }
    
    private static final String SQL_FIND = 
        "SELECT parent, "
            + "child, "
            + "type, "
            + "direction "
            + "FROM Tree "
            + "WHERE parent = ? "
            + "AND child = ?";
    private static final String SQL_LIST = 
        "SELECT parent, "
            + "child, "
            + "type, "
            + "direction "
            + "FROM Tree "
            + "ORDER BY parent";
    private static final String SQL_LIST_EXPANSION_BACKWARDS = 
        "SELECT parent, "
            + "child, "
            + "type, "
            + "direction "
            + "FROM Tree "
            + "WHERE parent = ? "
            + "AND type = '" + LinkType.EXPANSION.toString() + "' "
            + "AND direction = '" + Direction.BACKWARD.toString() + "' "
            + "ORDER BY child";
    private static final String SQL_LIST_EXPANSION_FORWARD =
        "SELECT parent, "
            + "child, "
            + "type, "
            + "direction "
            + "FROM Tree "
            + "WHERE parent = ? "
            + "AND type = '" + LinkType.EXPANSION.toString() + "' "
            + "AND direction = '" + Direction.FORWARD.toString() + "' "
            + "ORDER BY child";
    private static final String SQL_LIST_EXPANSION_BOTH = 
        "SELECT parent, "
            + "child, "
            + "type, "
            + "direction "
            + "FROM Tree "
            + "WHERE parent = ? AND "
            + "type = " + LinkType.EXPANSION.toString() + " "
            + "ORDER BY child";
    private static final String SQL_CREATE = 
        "INSERT INTO Tree ("
            + "parent, "
            + "child, "
            + "type, "
            + "direction) "
            + "VALUES (?,?,?,?)";
    private static final String SQL_UPDATE =
        "UPDATE Tree "
            + "SET type = ?, "
            + "direction = ? "
            + "WHERE parent = ? "
            + "AND child = ?";
    private static final String SQL_DELETE = 
        "DELETE FROM Tree "
            + "WHERE parent = ? "
            + "AND child = ?";
}
