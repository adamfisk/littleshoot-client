/*
 *  little_shoot_ipc.cpp
 *  littleshoot
 *
 *  Created by Julian Cain on 12/3/08.
 *  Copyright 2008 __MyCompanyName__. All rights reserved.
 *
 */

#if XP_WIN
#include "windows.h"
#endif

#include <constants.hpp>
#include <debug.hpp>
#include <little_shoot_ipc.hpp>
#include <little_shoot_plugin.hpp>
#if XP_WIN
#include <windows/launch_impl.hpp>
#else
#include <launcher.hpp>
#endif // XP_WIN
#include <path.hpp>
#include <process.hpp>

using namespace littleshoot;

little_shoot_ipc::little_shoot_ipc(int argc, const char * argv[])
    : terminate_(false)
    , m_state(stopped)
{
    log_function();
    
    log_debug("IPC arguments:");
    
    for (int i = 0; i < argc; i++)
    {
        log_debug(argv[i]);
    }
}

little_shoot_ipc::~little_shoot_ipc()
{
    log_function();
}
        
void little_shoot_ipc::start()
{
    log_function();
    
    log_debug(instance_lock_path);
    
    if (process::is_running())
    {
        log_debug("LittleShoot is running!");
    }
    else
    {
        log_debug("LittleShoot is NOT running!");
    
        static const char * args = "load";

        launch_impl l;
#if XP_WIN
        char * str_1 = "";
        char * str_2 = "";
        l.launch(path::win_32_path().c_str(), str_1, str_2);
#else
        l.launch(true, args, plist_path);
#endif // XP_WIN
    }
}
        
void little_shoot_ipc::stop()
{
    log_function();
    
    log_debug(instance_lock_path);
    
    if (process::is_running())
    {
        log_debug("Stopping LittleShoot...");

    static const char * args = "unload";

        launch_impl l;
#if XP_WIN
        // Do nothing on Windows.
#else
        l.launch(true, args, plist_path);
#endif // XP_WIN
    }
}

unsigned int little_shoot_ipc::state()
{
    log_function();

    return m_state;
}
        
bool little_shoot_ipc::set_state(unsigned int val)
{
    log_function();
        
    unsigned int old_state = m_state;
    
    unsigned int new_state = val;
    
    m_state = val;
    
    return old_state != new_state;
}

void little_shoot_ipc::thread()
{

}
