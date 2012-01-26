/*
 *  torrent.cpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/9/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#include <iostream>
#include <fstream>

#include <boost/lexical_cast.hpp>
#include <boost/filesystem/path.hpp>
#include <boost/filesystem/operations.hpp>

#include <constants.hpp>
#include <debug.hpp>
#include <path.hpp>
#include <protocol.hpp>
#include <stream.hpp>

using namespace littleshoot;

stream::stream(stream_entry & ent)
    : m_entry(ent)
    , buffer_(new char[/* ent.end() Could be 0 when server doesn't send content-length - jc */4096])
    , offset_(0)
{
    std::string tmp_path = (
        boost::filesystem::path(data_dir) /= "write/"
    ).string() + ent.tmp_path();
    
#if 0
    log_debug("Temp stream path1: " << tmp_path.c_str());
    log_debug("Temp stream path2: " <<  ent.tmp_path().c_str());
    log_debug("Temp stream path3: " << file_.path().c_str());
#endif

    path::create_file(tmp_path);

    file_.set_path(tmp_path);
    file_.open_write();
}

stream::~stream()
{
    log_debug("offset(" <<  offset_ << ", entry.end(" << m_entry.end() << ").");
}

void stream::deallocate()
{
    flush();
    flush_stream_entry();
}

bool stream::is_complete()
{
    return m_entry.end() == offset_;
}

int stream::write(
    const char * buf, std::size_t len, std::size_t offset, bool flush
    )
{
    offset_ = offset;
    
    // If flush is true then we clear the data that we just wrote to disk from
    // the write buffer.
    
    if (flush)
    {
        file_.write(buf, len);
    }
    else
    {
#if 0
        char * buf_new = new char[offset];
        std::memcpy(buf_new, buffer_.get(), len);
        std::memcpy(buf_new + offset, buf, len);
        std::memcpy(buffer_.get() + offset, buf, len);
        
        buffer_.reset(buf_new);
#endif
    }

    return len;
}

void stream::flush()
{
    file_.close();

    boost::filesystem::path temp_dir = boost::filesystem::system_complete(boost::filesystem::path(data_dir) /= "write/");
    boost::filesystem::path temp_path = boost::filesystem::system_complete(boost::filesystem::path(data_dir) /= "write/" + m_entry.tmp_path());
    
    boost::filesystem::path complete_dir(boost::filesystem::path(data_dir) /= "read/");

    boost::filesystem::create_directory(complete_dir);
    
    boost::filesystem::path complete_path(complete_dir /= boost::lexical_cast<std::string> (m_entry.id()) + ".torrent");
    
    log_debug(file_.path().c_str());
    log_debug(complete_dir.string().c_str());
    log_debug(temp_dir.string().c_str());
    log_debug(temp_path.string().c_str());
    log_debug(complete_path.string().c_str());
    
    try
    {
        boost::filesystem::copy_file(temp_path, complete_path);
    }
    catch (std::exception & e)
    {
        log_debug(e.what());
    }
    
    boost::filesystem::remove(temp_path);
}

void stream::flush_stream_entry()
{
#if 1
    stream_message_t msg = m_entry.marshal();
    
    file f(ipc_data_path, ipc_lock_path);
    f.set_path(ipc_data_path);
    f.open_write();
    f.append(msg.data(), msg.size());
    f.close();
#else
    file f(ipc_data_path);
    
    f.set_path(ipc_data_path);
    f.open_write();
    
    stream_message_t msg = test_vector_message();

    msg.marshal();
    
    f.append(msg.data(), msg.size());

    f.close();
#endif
}
            
stream_entry & stream::entry()
{
    return m_entry;
}

