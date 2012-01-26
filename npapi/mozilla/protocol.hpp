/*
 *  protocol.hpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/13/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef LITTLESHOOT_PLUGIN_PROTOCOL_HPP
#define LITTLESHOOT_PLUGIN_PROTOCOL_HPP

#include <boost/cstdint.hpp>

#include <byte_buffer.hpp>

namespace littleshoot {
        
    typedef struct stream_message
    {
        boost::uint16_t version; // 16 bit - version of this struct
        boost::uint16_t type; // 16 bit type of stream
        boost::uint32_t bodyLength; // 32 bit - length of the body to follow, in bytes
        boost::uint32_t transactionId; // 32 bit - ID for a transaction 
        boost::uint16_t urlLength; // 16 bit - length of url
        std::string url; // url
        boost::uint16_t httpHeaderLength; // 16 bit - length of HTTP headers
        std::string httpHeaders; // HTTP headers
        boost::uint16_t nameLength; // 16 bit - length of stream name
        std::string streamName; // stream name
        boost::uint16_t streamPathlen; // 16 bit - The length of the path string.
        std::string streamPath; // The path to the stream file on disk.
        boost::uint16_t streamTmpPathlen; // 16 bit - The length of the tmp path string.
        std::string streamTmpPath; // The path to the tmp stream file on disk.
        
        void marshal()
        {
            bytes.write_uint16(version);
            bytes.write_uint16(type);
            bytes.write_uint32(bodyLength);
            bytes.write_uint32(transactionId);
            bytes.write_uint16(urlLength);
            bytes.write_string(url);
            bytes.write_uint16(httpHeaderLength);
            bytes.write_string(httpHeaders);
            bytes.write_uint16(nameLength);
            bytes.write_string(streamName);
            bytes.write_uint16(streamPathlen);
            bytes.write_string(streamPath);
            bytes.write_uint16(streamTmpPathlen);
            bytes.write_string(streamTmpPath);
            
            bytes.print();
        }
        
        char * data()
        {
            return bytes.bytes();
        }
        
        std::size_t size()
        {
            return bytes.size();
        }
        
        byte_buffer bytes;

        
    } stream_message_t;
    
    static stream_message_t test_vector_message()
    {
        stream_message_t msg;
        
        char stream_name[] = { 
            0x5C11, 0x3057, 0x20, 0x0412, 0x0441, 0x0445, 0x043E, 0x0434, 0x20, 
            0x4E, 0x50, 0x41, 0x50, 0x49, 0x20, 0x6D41, 0x308C
        };
        
        // unused
        (char *)stream_name;

        msg.version = 0x0001;
        msg.type = 0x0001;
        msg.bodyLength = 0x000000CC;
        msg.transactionId = 0x7FFFFFFF;
        msg.urlLength = 0x002B;
        msg.url = "http://somesiteoutthere.com/newfile.torrent";
        msg.httpHeaderLength = 0x004D;
        msg.httpHeaders = "HTTP/1.1 200 OK\r\ncontent-length:10\r\n"
            "content-type:application/x-bittorrent\r\n\r\n";
        msg.nameLength = 0x001E;
        msg.streamName = "少し Всход NPAPI 流れ";
        msg.streamPathlen = 0x0014;
        msg.streamPath = "/Users/adamfisk/test";
        msg.streamTmpPathlen = 0x0018;
        msg.streamTmpPath = "/Users/adamfisk/testTemp";
        
        return msg;
    }
    

        
}; // namespace littleshoot
    
#endif // LITTLESHOOT_PLUGIN_PROTOCOL_HPP
