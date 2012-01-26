/*
 *  constants.hpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/9/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef LITTLESHOOT_PLUGIN_CONSTANTS_HPP
#define LITTLESHOOT_PLUGIN_CONSTANTS_HPP

#include <boost/filesystem/path.hpp>

#include <path.hpp>

namespace littleshoot {

#ifdef WIN32
    static const boost::filesystem::path data_dir(
        boost::filesystem::path(path::appdata_dir()) /= "littleshoot/"
    );
#else
    static const boost::filesystem::path data_dir(
        boost::filesystem::path(path::home_dir()) /= ".littleshoot/"
    );
#endif

/**
 * Logging paths
 */
#ifdef WIN32
    static const boost::filesystem::path _log_dir(
        boost::filesystem::path(path::appdata_dir()) /= "logs/"
    );
#elif defined(__APPLE__)
    static const boost::filesystem::path _log_dir(
        boost::filesystem::path("/Library/Logs/LittleShoot/")
    );
#else
    static const boost::filesystem::path _log_dir(
        boost::filesystem::path(path::home_dir()) /= ".littleshoot/logs/"
    );
#endif
    
    static const boost::filesystem::path stream_dir(
        boost::filesystem::path(data_dir) /= "stream/"
    );
    
    static boost::filesystem::path instance_lock_path_(
        boost::filesystem::path(data_dir) /= "littleshoot_instance.lck"
    );
    
    static boost::filesystem::path ipc_lock_path_(
        boost::filesystem::path(data_dir) /= "plugin_littleshoot_ipc.lck"
    );
    
    static boost::filesystem::path ipc_data_path_(
        boost::filesystem::path(data_dir) /= "plugin_littleshoot_ipc.dat"
    );
    
    static boost::filesystem::path stream_lock_path_(
        boost::filesystem::path(stream_dir) /= "plugin_littleshoot_stream.lck"
    );

    static boost::filesystem::path stream_data_path_(
        boost::filesystem::path(stream_dir) /= "plugin_littleshoot_stream.dat"
    );

    static const char * instance_lock_path = instance_lock_path_.string(
        ).c_str(
    );

    static const char * stream_lock_path = stream_lock_path_.string().c_str();
    
    static const char * stream_data_path = stream_data_path_.string().c_str();
    
    static const char * ipc_lock_path = ipc_lock_path_.string().c_str();
    
    static const char * ipc_data_path = ipc_data_path_.string().c_str();
    
};

#endif // LITTLESHOOT_PLUGIN_CONSTANTS_HPP
