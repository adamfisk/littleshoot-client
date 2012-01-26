/*
 *  launcher.cpp
 *  littleshoot
 *
 *  Created by Julian Cain on 2/4/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#include <errno.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdbool.h>
#include <assert.h>
#include <stdio.h>
#include <string.h>
#include <limits.h>
#include <sys/stat.h>

#include <launcher.hpp>

#define environ (*_NSGetEnviron())

#ifdef __cplusplus
extern "C"
{
    extern char ** environ;
}
#endif

using namespace littleshoot;

int launch_impl::launch(
    bool dev_null, const char * command, const char * plist_path
    )
{  
    int err;
    const char * args[5];
    pid_t childPID;
    pid_t waitResult;
    int status;
  
    assert(command);
    assert(plist_path);

    status = 0;

    args[0] = "/bin/launchctl";
    args[1] = command;        // "load" or "unload"
#if 0 // We may need to write to the .plist control runtime behaviour.
    args[2] = "-w";
#endif
    args[2] = "-F";
    args[3] = plist_path;      // path to plist
    args[4] = 0;

    fprintf(stderr, "launchctl %s %s '%s'\n", args[1], args[2], args[3]);
  
    childPID = fork();
    
    switch (childPID)
    {
        case 0:
            err = 0;
            
            if (dev_null)
            {
                int fd;
                int err2;

                fd = open("/dev/null", O_RDWR);
        
                if (fd < 0)
                {
                    err = errno;
                }
                
                if (err == 0)
                {
                    if (dup2(fd, STDIN_FILENO) < 0)
                    {
                        err = errno;
                    }
                }
            
            if (err == 0)
            {
                if (dup2(fd, STDOUT_FILENO) < 0)
                {
                    err = errno;
                }
            }
            
            if (err == 0)
            {
                if (dup2(fd, STDERR_FILENO) < 0)
                {
                    err = errno;
                }
            }
            
            err2 = close(fd);
            
            if (err2 < 0)
            {
                err2 = 0;
            }
            
            if (err == 0)
            {
                err = err2;
            }
        }
      
        if (err == 0)
        {
            err = execve(args[0], (char **)args, environ);
        }
        
        if (err < 0)
        {
            err = errno;
        }
      
        _exit(EXIT_FAILURE);
        break;
        case -1:
            err = errno;
      break;
        default:
            err = 0;
        break;
    }

    if (err == 0)
    {
        do
        {
            waitResult = waitpid(childPID, &status, 0);
        } while ((waitResult == -1) && (errno == EINTR));

        if (waitResult < 0)
        {
            err = errno;
        }
        else
        {
            assert(waitResult == childPID);

            if (!WIFEXITED(status) || (WEXITSTATUS(status) != 0))
            {
                err = EINVAL;
            }
        }
    }

    fprintf(stderr, "launch -> %d %ld 0x%x\n", err, (long)childPID, status);
  
    return err;
}
