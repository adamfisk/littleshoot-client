/*
 *  file.cpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/8/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#include <fstream>

#include <boost/filesystem/path.hpp>
#include <boost/filesystem/operations.hpp>
#include <boost/interprocess/sync/scoped_lock.hpp>
#include <boost/interprocess/sync/sharable_lock.hpp>

#include <constants.hpp>
#include <debug.hpp>
#include <file.hpp>
#include <path.hpp>

using namespace littleshoot;

file::file()
    : path_("")
{

}

file::file(const std::string & file_path)
    : path_(file_path)
    , lock_path_(file_path)
{

}

file::file(const std::string & file_path, const std::string & lock_path)
    : path_(file_path)
    , lock_path_(lock_path)
{

}

void file::set_path(const std::string & file_path)
{
    boost::filesystem::path write_dir(
        boost::filesystem::path(data_dir) /= "write/"
    );

    try
    {
        path::create_directory(write_dir.string());
        
        path::create_file(file_path);
    
        path_ = file_path;
        
        if (!lock_path_.size())
        {
            lock_path_ = file_path;
        }
    
        log_debug(path_.c_str());
        log_debug(lock_path_.c_str());

    }
    catch (std::exception & e)
    {
        log_debug(e.what());
    }
    
    if (!lock_acquired())
    {
        throw std::runtime_error("!lock_acquired");
    }
}

file::~file()
{
    // ...
}

bool file::lock_acquired()
{
    try
    {
        file_lock_.reset(new boost::interprocess::file_lock(
            lock_path_.c_str())
        );

#ifdef WIN32
        // :FIXME: Locking an open stream fails on vista.
#else
        file_lock_->lock();
#endif
        
        return true;
    }
    catch (std::exception & e)
    {
        log_debug(e.what());
        
        return false;
    }
    
    return false;
}

void file::open_write()
{
    try
    {    
        boost::interprocess::sharable_lock<boost::interprocess::file_lock> l(
            *file_lock_
        );
        
        ofstream_.open(
            path_.c_str(), 
            std::ios_base::binary | std::ios_base::ate | std::ios_base::app
        );
    }
    catch (std::exception & e)
    {
        log_debug(e.what());
    }
}

void file::close()
{
    try
    {
        boost::interprocess::sharable_lock<boost::interprocess::file_lock> l(
            *file_lock_
        );
        
        ofstream_.flush();
        ofstream_.close();
    }
    catch (std::exception & e)
    {
        log_debug(e.what());
    }
}

int file::append(char * buf, std::size_t len)
{
    std::size_t ret = 0;
    
    try
    {
        boost::interprocess::scoped_lock<boost::interprocess::file_lock> l(
            *file_lock_
        );

        //ofstream_.unsetf(std::ios_base::skipws);
        
        ofstream_.write(buf, len);
        
        ofstream_.flush();
    }
    catch (std::exception & e)
    {
        log_debug(e.what());
        
        return ret;
    }

    return ret;
}

int file::read(char * buf, std::size_t len)
{
    std::size_t ret = 0;
    
    try
    {
        boost::interprocess::file_lock file_lock(path_.c_str());
        
        boost::interprocess::sharable_lock<boost::interprocess::file_lock> l(
            *file_lock_
        );
    
        log_debug(":TODO: asserting now...");
        assert(0);
    }
    catch (std::exception & e)
    {
        log_debug(e.what());
        
        return ret;
    }
    
    return ret;
}

int file::write(const char * buf, std::size_t len)
{
    std::size_t ret = 0;
    
    try
    {
		/**
		 * This is a workaround from adam so we aren't blocked. 
		 * The underlying issue is that Vista doesn't like me to have a lock
		 * on a file that I am writing too. Basically streams are self 
		 * locking on nix* and broke on Vista but work fine on Win2K and XP. 
		 * For now we will not write lock the stream so in the mean time I 
		 * will be investigating the issue.
		 */
#ifndef WIN32
        boost::interprocess::scoped_lock<boost::interprocess::file_lock> l(
            *file_lock_
        );
#else // WIN32
//	log_debug("%s", buf);
#endif // !WIN32
        
        ofstream_.write(buf, len);
        
        ofstream_.flush();
    }
    catch (std::exception & e)
    {
        log_debug(e.what());
        
        return ret;
    }

    return ret;
}

