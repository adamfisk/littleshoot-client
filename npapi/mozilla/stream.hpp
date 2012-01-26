/*
 *  stream.hpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/9/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef LITTLESHOOT_PLUGIN_STREAM_HPP
#define LITTLESHOOT_PLUGIN_STREAM_HPP

#include <string>

#include <boost/noncopyable.hpp>
#include <boost/scoped_ptr.hpp>

#include <file.hpp>
#include <stream_entry.hpp>

namespace littleshoot {

    /**
     * This class implements a stream file wrapper/writer/reader thing, yea.
     */
    class stream : private boost::noncopyable
    {
        public:
        
            /**
             * Construct a stream from a char buffer.
             */
            stream(stream_entry & ent);
            
            /**
             * Destructor
             */
            ~stream();
            
            /**
             * Deallocates the stream, should be renamed to destroy.
             */
            void deallocate();
            
            /**
             * True if the stream offset is equal to end.
             */
            bool is_complete();

            /**
             * Write data to the stream for async writes.
             */
            int write(
                const char * buf, std::size_t len, 
                std::size_t offset, bool flush
            );
            
            /**
             * Flush the buffer to the path, appending if needed.
             */
            void flush();
            
            /**
             * Explicitly flush the stream entry to IPC channel.
             */
            void flush_stream_entry();
            
            /**
             * The stream entry.
             */
            stream_entry & entry();

        private:
            
            /**
             * The stream entry.
             */
            stream_entry m_entry;
            
        protected:
        
            boost::scoped_ptr<char> buffer_;
        
            boost::uint32_t offset_;
            
            littleshoot::file file_;
    };

} // namespace littleshoot

#endif // LITTLESHOOT_PLUGIN_FILE_HPP
