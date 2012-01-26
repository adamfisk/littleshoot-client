/*
 *  process.cpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/8/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#include <constants.hpp>
#include <debug.hpp>
#include <file.hpp>
#include <process.hpp>

#include <boost/interprocess/sync/scoped_lock.hpp>

using namespace littleshoot;

bool process::is_running()
{
    try
    {
        boost::interprocess::file_lock file_lock(instance_lock_path);
        
        if (!file_lock.try_lock())
        {
            return true;
        }
    }
    catch (std::exception & e)
    {
        log_debug(e.what());
        
        return false;
    }
    
    return false;
}
