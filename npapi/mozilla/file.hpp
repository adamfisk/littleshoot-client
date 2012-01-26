/*
 *  file.hpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/8/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef LITTLESHOOT_PLUGIN_FILE_HPP
#define LITTLESHOOT_PLUGIN_FILE_HPP

#include <fstream>
#include <string>

#include <boost/scoped_ptr.hpp>
#include <boost/interprocess/sync/file_lock.hpp>
#include <boost/filesystem/fstream.hpp>

namespace littleshoot {

    class file
    {
        public:
        
            file();
    
            file(const std::string & file_path);
            
            file(const std::string & file_path, const std::string & lock_path);
            
            ~file();
            
            void set_path(const std::string & file_path);
            void open_write();
            
            void close();
            
            bool lock_acquired();
            
            int append(char * buf, std::size_t len);
            int read(char * buf, std::size_t len);
            int write(const char * buf, std::size_t len);
            
            const std::string & path()
            {
                return path_;
            }
            
        private:
        
            std::string path_;
            
            std::string lock_path_;
            
            boost::filesystem::ofstream ofstream_;
            
            boost::scoped_ptr<boost::interprocess::file_lock> file_lock_;
    };

} // namespace littleshoot

#endif // LITTLESHOOT_PLUGIN_FILE_HPP
