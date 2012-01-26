/*
 *  launcher.hpp
 *  littleshoot
 *
 *  Created by Julian Cain on 2/4/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef LITTLESHOOT_PLUGIN_LAUNCHER_HPP
#define LITTLESHOOT_PLUGIN_LAUNCHER_HPP

namespace littleshoot {

    /**
     * The path to the LittleShoot plist for launchctl.
     */
    static const char * plist_path = 
        "/Library/LaunchAgents/org.littleshoot.littleshoot.plist";
    
    /**
     * Implements a cross platform executable launcher.
     */
    struct launch_impl
    {
        int launch(
            bool dev_null, const char * command, const char * plist_path
        );
    };
    
} // namespace littleshoot

#endif // LITTLESHOOT_PLUGIN_LAUNCHER_HPP
