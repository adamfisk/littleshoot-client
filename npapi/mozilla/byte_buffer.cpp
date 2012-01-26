/*
 *  byte_buffer.cpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/15/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#include <iostream>

#ifdef WIN32
#include <Winsock2.h>
#pragma comment(lib, "ws2_32.lib")
#endif

#include <byte_buffer.hpp>

using namespace littleshoot;

byte_buffer::byte_buffer()
    : start_(0)
    , end_(0)
    , m_size(0)
    , m_bytes(0)
{
    // ...
}

byte_buffer::byte_buffer(std::size_t len)
    : start_(0)
    , end_(len)
    , m_size(len)
    , m_bytes(new char[len])
{
    assert(len);
}

byte_buffer::byte_buffer(const char * buf, std::size_t len)
    : start_(0)
    , end_(len)
    , m_size(len)
    , m_bytes(new char[len])
{
    assert(len);
    
    std::memcpy(m_bytes.get(), buf, end_);
}

byte_buffer::byte_buffer(const char * str)
    : start_(0)
    , end_(strlen(str))
    , m_size(end_)
    , m_bytes(new char[m_size])
{
    std::memcpy(m_bytes.get(), str, end_);
}

byte_buffer::byte_buffer(const byte_buffer & other)
    : start_(other.start_)
    , end_(other.end_)
    , m_size(other.m_size)
    , m_bytes(new char[other.m_size])
{
    std::memcpy(m_bytes.get(), other.data(), other.end_);
}

byte_buffer::~byte_buffer()
{
    // ...
}

byte_buffer & byte_buffer::operator = (const byte_buffer & other)
{
    start_ = other.start_;
    end_ = other.end_;
    m_size = other.m_size;
    m_bytes.reset(new char[other.m_size]);
    std::memcpy(m_bytes.get(), other.data(), other.end_);
    return *this;
}

void byte_buffer::write_uint16(boost::uint16_t val)
{
    boost::uint16_t i = htons(val);
    write_bytes(reinterpret_cast<const char *>(&i), 2);
}

void byte_buffer::write_uint32(boost::uint32_t val)
{
    boost::uint32_t i = htonl(val);
    write_bytes(reinterpret_cast<const char *>(&i), 4);
}

void byte_buffer::write_string(const std::string & in)
{
    write_bytes(in.c_str(), in.size());
}

void byte_buffer::write_bytes(const char * buf, std::size_t len)
{
    if (size() + len > capacity())
    {
        resize(size() + len);
    }

    std::memcpy(m_bytes.get() + end_, buf, len);
    
    end_ += len;
}

void byte_buffer::seek(std::size_t len)
{
    if (len < m_size)
    {
        start_ = len;
    }
}

void byte_buffer::resize(std::size_t len)
{
    if (len > m_size)
    {
        len = (std::max)(len, 3 * m_size / 2);
    }

    std::size_t new_len = (std::min)(end_ - start_, len);

    boost::scoped_ptr<char> new_bytes(new char[len]);
    
    std::memcpy(new_bytes.get(), m_bytes.get() + start_, new_len);
    
    start_ = 0;
    end_ = new_len;
    m_size = len;
    
    m_bytes.reset(new char[m_size]);
    
    std::memcpy(m_bytes.get(), new_bytes.get(), m_size);
}

void byte_buffer::shift(std::size_t len)
{
    if (len > size())
    {
        return;
    }

    end_ = size() - len;
    std::memmove(m_bytes.get(), m_bytes.get() + start_ + len, end_);
    start_ = 0;
}

void byte_buffer::truncate(std::size_t len)
{
    if (len == 0)
    {
        resize(capacity() - size());
    }
    else if (len < size())
    {
        resize(len);
    }
}

void byte_buffer::print()
{
#ifndef NDEBUG
	std::cout << "--- Byte Buffer HEX ---" << std::endl;
	std::cout << std::hex;
	
	for (std::size_t i = 0; i < size(); i++)
	{
		std::cout << 
            (char)(m_bytes.get()[i]) << "(" << (int)(m_bytes.get()[i]) << ") ";
	}
	std::cout << std::dec << std::endl;
    std::cout << "-----------------------" << std::endl << std::flush;
#endif
}

