/*
 *  little_shoot_ipc.h
 *  littleshoot
 *
 *  Created by Julian Cain on 12/3/08.
 *  Copyright 2008 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef LITTLE_SHOOT_IPC_HPP
#define LITTLE_SHOOT_IPC_HPP

#include <string>
#include <vector>

#include <boost/thread.hpp>
#include <boost/shared_ptr.hpp>
#include <boost/enable_shared_from_this.hpp>

/**
 * This class implements IPC for the Little Shoot Runner.
 */
class little_shoot_ipc : public boost::enable_shared_from_this<little_shoot_ipc>
{
    public:
    
        /**
         * Constructor
         * @param argc Argument count.
         * @param argv Argument variables.
         */
        little_shoot_ipc(int argc, const char * argv[]);
        
        /**
         * Destructor
         */
        ~little_shoot_ipc();
        
        /**
         * Starts the LittleShoot IPC process.
         */
        void start();
        
        /**
         * Stops the LittleShoot IPC process.
         */
        void stop();
        
        /**
         * The current state of the ipc interface.
         */
        unsigned int state();
        
    private:
    
        /**
         * The littleshoot process is run, monitored and control via this 
         * thread.
         */
        void thread();
    
        /**
         * The current state of the ipc interface.
         */
        enum state
        {
            stopped = 0,
            starting = 1,
            started = 2,
            stopping = 3
        };
        
        unsigned int m_state;
        
        /**
         * Sets the current state of the ipc interface.
         */
        bool set_state(unsigned int val);
        
    protected:
        
        /**
         * The executable.
         */
        std::string exec_;
        
        /**
         * The arguments.
         */
        std::vector<std::string> args_;
        
        /**
         * The littleshoot process is run, monitored and controled via this 
         * thread.
         */
        boost::shared_ptr<boost::thread> thread_;
        
        /**
         * Used to trigger termination.
         */
        mutable bool terminate_;
        
        /**
         * Condition varbiable used to block the runner thread.
         */
        boost::condition_variable condition_variable_;
        
        /**
         * Mutex used in conjunction with condition variable.
         */
        mutable boost::mutex mutex_;
};

// ...
typedef little_shoot_ipc little_shoot_ipc_t;

#endif // LITTLE_SHOOT_IPC_HPP
