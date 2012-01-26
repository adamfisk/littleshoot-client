/*
 *  stream_entry.cpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/9/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#include <sstream>

#include <boost/lexical_cast.hpp>
#include <boost/filesystem/path.hpp>
#include <boost/filesystem/operations.hpp>

#include <constants.hpp>
#include <debug.hpp>
#include <path.hpp>
#include <stream_entry.hpp>
#include <random_number.hpp>

using namespace littleshoot;

stream_entry::stream_entry(
    const char * url_buf, boost::uint32_t stream_end, 
    boost::uint32_t last_modified, const char * header_buf
    )
    : m_version(1)
    , m_type(1)
    , m_body_length(0)
    , m_transaction_id(random_number::instance().get_uint32())
    , m_url_length(strlen(url_buf))
    , m_url(url_buf)
	, m_http_header_length((header_buf == 0) ? 0 : strlen(header_buf))
    , m_http_header((header_buf == 0) ? "" : header_buf)
    , m_name_length(19)
    , m_name("TORRENT STREAM TEST")
    , m_path_length(0)
    , m_path("")
    , m_tmp_path_length(0)
    , m_tmp_path("")
    // these are not part of the struct.
    , m_end(stream_end)
    , m_last_modified(last_modified)
    , m_length(0)
    , m_id(random_number::instance().get_uint32())  
{
    boost::filesystem::path complete_dir(boost::filesystem::path(data_dir) /= "read/");

    boost::filesystem::path complete_path(complete_dir /= boost::lexical_cast<std::string> (id()) + ".torrent");
    
    m_path = complete_path.string();

    m_tmp_path += boost::lexical_cast<std::string> (m_id);
    m_tmp_path += ".tmp";
    
    log_debug(m_tmp_path.c_str());
}

stream_entry::~stream_entry()
{
    // ...
}

const char * stream_entry::to_string() const
{
    std::stringstream ss;
        ss << "Stream: ";
        ss << "id(";
        ss << m_id;
        ss << "url(";
        ss << m_url;
        ss << "), end(";
        ss << m_end;
        ss << "), last_modified(";
        ss << m_last_modified;
        ss << "), header_buf(";
        ss << m_http_header;
    return ss.str().c_str();
}

stream_message stream_entry::marshal()
{
    m_path_length = m_path.size();
    m_tmp_path_length = m_tmp_path.size();
    
    m_body_length = m_url_length + m_http_header_length + m_name_length + 
        m_path_length + m_tmp_path_length
    ;
    
    stream_message msg;

    msg.version = m_version;
    msg.type = m_type;
    msg.bodyLength = m_body_length;
    msg.transactionId = m_transaction_id;
    msg.urlLength = m_url_length;
    msg.url = m_url;
    msg.httpHeaderLength = m_http_header_length;
    msg.httpHeaders = m_http_header;
    msg.nameLength = m_name.size();
    msg.streamName = m_name;
    msg.streamPathlen = m_path_length;
    msg.streamPath = m_path;
    msg.streamTmpPathlen = m_tmp_path_length;
    msg.streamTmpPath = m_tmp_path;
    
    msg.marshal();
    
    return msg;
}

bool stream_entry::operator < (const stream_entry & other) const
{
    return m_end < other.m_end;
}
