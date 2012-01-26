/*
 *  stream_entry.hpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/9/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef LITTLESHOOT_PLUGIN_STREAM_ENTRY_HPP
#define LITTLESHOOT_PLUGIN_STREAM_ENTRY_HPP

#include <boost/cstdint.hpp>

#include <protocol.hpp>

namespace littleshoot {

    /**
     * This struct encasulates an NPStream.
     */
    class stream_entry
    {
        public:
        
            /**
             * Constructor
             */
            explicit stream_entry(
                const char * url_buf, boost::uint32_t stream_end, 
                boost::uint32_t last_modified, const char * header_buf
            );
            
            /**
             * Destructor
             */
            ~stream_entry();
            
            /**
             * The string respresentation of a stream_entry.
             */
            const char * to_string() const;
            
            /**
             * Marshal's a stream for sending over an IPC channel.
             */
            stream_message marshal();
            
            /**
             * The stream id created and managed by us.
             */
            boost::uint32_t id()
            {
                return m_id;
            }
            
            /**
             *
             */
            const std::string & url()
            {
                return m_url;
            }
            
            /**
             *
             */
            boost::uint32_t end()
            {
                return m_end;
            }
            
            /**
             *
             */
            boost::uint16_t length()
            {
                return m_length;
            }
            
            /**
             *
             */
            std::string tmp_path()
            {
                return m_tmp_path;
            }
            
            bool operator < (const stream_entry & other) const;
    
        private:
       
            /**
             * Stream version.
             */
            boost::uint16_t m_version;
            
            /**
             * Type of stream.
             */
            boost::uint16_t m_type;
            
            /**
             * The stream body length.
             */
            boost::uint32_t m_body_length;
            
            /**
             * The transaction identifier.
             */
            boost::uint32_t m_transaction_id;
            
            /**
             * The url length.
             */
            boost::uint16_t m_url_length;
            
            /**
             * The url.
             */
            std::string m_url;
            
            /**
             * The http header length.
             */
            boost::uint16_t m_http_header_length;
            
            /**
             * The http header.
             */
            std::string m_http_header;
            
            /**
             * The name length.
             */
            boost::uint16_t m_name_length;
            
            /**
             * The name
             */
            std::string m_name;
            
            /**
             * The path length.
             */
            boost::uint16_t m_path_length;
            
            /**
             * The path on disk.
             */
            std::string m_path;
            
            /**
             * The tmp path length.
             */
            boost::uint16_t m_tmp_path_length;
            
            /**
             * The temporary path on disk.
             */
            std::string m_tmp_path;
    
            // these are not part of the struct.
    
            /**
             * The end of file offset.
             */
            boost::uint32_t m_end;
            
            /**
             * The time the stream was last modified.
             */
            boost::uint32_t m_last_modified;
            
            /**
             * The length.
             */
            boost::uint16_t m_length;
           
            /**
             * The stream id created and managed by us.
             */
            boost::uint32_t m_id;
    
        protected:
        
            // ...
    };

} // namespace littleshoot

#endif // LITTLESHOOT_PLUGIN_STREAM_ENTRY_HPP
