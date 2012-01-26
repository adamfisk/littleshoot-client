
#include <cassert>

#include <windows.h>

#include <debug.hpp>
#include <windows/launch_impl.hpp>

using namespace littleshoot;

int launch_impl::launch(
    const char * path, char * args, char * vars
    )
{  
    bool ret = false;
    
    log_debug(path);
    log_debug(args);
    log_debug(vars);
    
    assert(path);
    assert(args);
    assert(vars);

#define USE_SHELL 1

#if USE_SHELL
    ShellExecuteA(0, "open", path, 0, 0, SW_SHOWNORMAL);
    ret = true;
#else
    STARTUPINFOA * si;
    STARTUPINFOA si1 = {0};

#if 0 // We may needs some flags.
    si->dwFlags |= STARTF_RUNFULLSCREEN;
#endif

    si = &si1;
    si->cb = sizeof(si1);

    PROCESS_INFORMATION pi;
    
    if (!(err = CreateProcessA(
        path, args, 0, 0, false, DETACHED_PROCESS, 0, 0, si, &pi)
        ))
    {
        DWORD err = GetLastError();
        
        log_debug(
            "Failed to launch (" << path << "), last error(" << err << ")."
        );
    }
    
    CloseHandle(pi.hThread);
    CloseHandle(pi.hProcess);
    
#endif

    return ret;
}
