/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.twittercollector.grid.collector.userexplorer;

import static es.uam.eps.ir.twittercollector.grid.collector.userexplorer.UserExplorerIdentifiers.BFS;
import es.uam.eps.ir.twittercollector.userexplorer.UserExplorer;

/**
 *
 * @author Javier
 */
public class UserExplorerSelector 
{
    public UserExplorer select(UserExplorerParamReader uepr)
    {
        String name = uepr.getName();
        UserExplorerConfigurator conf;
        switch(name)
        {
            case BFS:
                conf = new BFSUserExplorerConfigurator();
                break;
            default:
                return null;
        }
        
        UserExplorer explorer = conf.configure(uepr);
        return explorer;
    }
}
