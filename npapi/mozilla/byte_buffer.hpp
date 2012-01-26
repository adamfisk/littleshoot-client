/*
 *  byte_buffer.hpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/15/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef LITTLESHOOT_PLUGIN_BYTE_BUFFER_HPP
#define LITTLESHOOT_PLUGIN_BYTE_BUFFER_HPP

#include <string>

#include <boost/cstdint.hpp>
#include <boost/scoped_ptr.hpp>

namespace littleshoot {

    /**
     * Implements a dynamiclly allocated buffer.
     * @note 32 bit buffer size max. 
     */
    class byte_buffer
    {
        public:
            
            /**
             * Constructor
             */
            byte_buffer();
            
            /**
             * Cosntructor
             * @param len The length to pre-allocate on bytes.
             */
            byte_buffer(std::size_t len);
            
            /**
             * Constructor
             * @param str The input string.
             * @note The lenght is determined by strlen.
             */
            byte_buffer(const char * str);

            /**
             * Constructor
             * @param buf The input buffer.
             * @param len The length of the input buffer.
             */
            byte_buffer(const char * buf, std::size_t len);
            
            /**
             * Copy Constructor
             */
            byte_buffer(const byte_buffer & other);
            
            /**
             * Destructor
             */
            virtual ~byte_buffer();
            
            /**
             * Assignment Operator
             */
            byte_buffer & operator = (const byte_buffer & other);
            
            /**
             *
             */
            const char * data() const { return m_bytes.get() + start_; }
            
            /**
             *
             */
            char * bytes() const { return m_bytes.get(); }
            
            /**
             *
             */
            std::size_t size() { return end_ - start_; }
            
            /**
             *
             */
            std::size_t capacity() { return m_size - start_; }
            
            /**
             *
             */
            void write_uint16(boost::uint16_t val);
            
            /**
             *
             */
            void write_uint32(boost::uint32_t val);
            
            /**
             *
             */
            void write_string(const std::string & str);
            
            /**
             *
             */
            void write_bytes(const char * buf, std::size_t len);
            
            /**
             *
             */
            void seek(std::size_t len);
            
            /**
             *
             */
            void resize(std::size_t len);
            
            /**
             *
             */
            void shift(std::size_t len);
            
            /**
             * Truncates the buffer to len bytes.
             * @param len The length to truncate off of the tail end of the 
             * buffer.
             */
            void truncate(std::size_t len);
            
            /**
             * Prints the buffer contents to cout.
             */
			void print();

        private:
            
            /**
             * The byte buffer.
             */
            boost::scoped_ptr<char> m_bytes;
            
            /**
             * The current size of the byte buffer.
             */
            std::size_t m_size;
            
        protected:
        
            std::size_t start_;
            std::size_t end_;
    };

}; // namespace littleshoot

#endif // LITTLESHOOT_PLUGIN_BYTE_BUFFER_HPP
