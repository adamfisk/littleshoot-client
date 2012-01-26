/*
 *  process.hpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/8/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef LITTLESHOOT_PLUGIN_PROCESS_HPP
#define LITTLESHOOT_PLUGIN_PROCESS_HPP

#include <string>

#include <boost/interprocess/sync/file_lock.hpp>

namespace littleshoot {

    /**
     * This class encapsulates runtime information about LittleShoot.
     */
    class process
    {
        public:
    
            /**
             * Checks 
             */
            static bool is_running();
            
        private:
        
            /**
             * The path to the littleshoot .lck file.
             */
            static std::string lock_path_;
            
    };

} // namespace littleshoot

#endif // LITTLESHOOT_PLUGIN_FILE_HPP
