/*
 *  debug.hpp
 *  littleshoot
 *
 *  Created by Julian Cain on 12/4/08.
 *  Copyright 2008 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef LITTLE_SHOOT_DEBUG_HPP
#define LITTLE_SHOOT_DEBUG_HPP

#if defined(WIN32)
#include <windows.h>
#endif

#include <fstream>
#include <sstream>

#include <boost/scoped_ptr.hpp>
#include <boost/pool/detail/singleton.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/recursive_mutex.hpp>

#include <constants.hpp>

namespace littleshoot {

    /**
     * This class implements a (very tiny) singleton logging facility.
     */
    class logger
    {
        /**
         * The number of lines we have written to the log file. 
         */
        unsigned int lines_written_;
        
        public:
        
            /**
             * Constructor.
             */
            logger()
                : console_(false)
                , filter_(0)
                , lines_written_(0)
            {
                // ...
            }
		
            /**
             * Destructor, closes the file.
             */
            virtual ~logger()
            {
                ofstream_.close();
            }
            
            /**
             * Singleton accessor.
             */
            static logger & instance()
            {
                using boost::details::pool::singleton_default;

                return singleton_default<logger>::instance();
            }
             
            /**
             * Disables printing of messages to the standard output.
             */
            void console_off()
            {
                console_ = false;
            }
            
            /**
             * Enables printing of messages to the standard output.
             */
            void console_on()
            {
                console_ = true;
            }

            /**
             * Set the output logfile.
             * @param file The name of the file
             * @throw Exception if the file can't be opened
             */
            void set_output_file(const std::string & file)
            {
                if (file.size())
                {
                    output_file_ = _log_dir.string() + file;
                    
                    if (output_file_.size())
                    {
                        open_log_file();
                    }
                }
            }
            
            /**
             *
             */
            void open_log_file()
            {
                if (ofstream_.is_open())
                {
                    ofstream_.close();
                }
                
                ofstream_.open(
                    output_file_.c_str(), std::ios::out | std::ios::trunc
                );
                ofstream_ << date_time_str();
                ofstream_ << " ";
                ofstream_ << "*** Logging started. ***" << std::endl;
            }
            
            /**
             *
             */
            inline std::string date_time_str()
            {
                std::ostringstream os;

                static const char * month_abbrevs[] =
                {
                    "jan", "feb", "mar", 
                    "apr", "may", "jun", 
                    "jul", "aug", "sep", 
                    "oct", "nov", "dec"
                };
        
                std::time_t rawtime;
        
                struct tm * timeinfo;

                std::time(&rawtime);
        
                timeinfo = std::localtime(&rawtime);
        
                char time_string[100];
        
                sprintf(
                    time_string, "%d-%s-%02d %02d:%02d:%02d",
                    1900 + timeinfo->tm_year, month_abbrevs[timeinfo->tm_mon],
                    timeinfo->tm_mday, timeinfo->tm_hour, 
                    timeinfo->tm_min, timeinfo->tm_sec
                );
        
                os << time_string;
        
                return os.str();
            }

            template <class T>
            logger & operator << (T const & val)
            {
                std::stringstream ss;
                
                // Format the data for output.
                ss << date_time_str();
                ss << " ";
                ss << val;
                
                if (1)
                {
                    // Write to the log file.
                    ofstream_ << ss.str() << std::endl;
                    ofstream_.flush();
                    
                    // Check if we are over limit.
                    if (lines_written_++ > 10000)
                    {
                        // Truncate the file.
                        open_log_file();
                    }
                }
                
                if (console_)
                {
#if defined(WIN32)

#if defined(_UNICODE)
                    DWORD dwNum = MultiByteToWideChar(
                        CP_ACP, 0, ss.str().c_str(), -1, NULL, 0
                    );

                    wchar_t * pwText = new wchar_t[dwNum];
                
                    if(!pwText)
                    {
                        delete [] pwText;
                    }

                    MultiByteToWideChar(
                        CP_ACP, 0, ss.str().c_str(), -1, pwText, dwNum
                    );
                    
                    OutputDebugString(pwText);

                    OutputDebugString(L"\n");

                    delete [] pwText;
#endif
#else
                    std::cout << ss.str() << std::endl;
#endif
                }
                
                return logger::instance();
            }
            
        private:
        
            // ...
            
        protected:

            std::ofstream ofstream_;
            bool console_;
            std::stringstream ss_;
            unsigned int filter_;
            std::string output_file_;
    };

} // namespace littleshoot

#define LOG_DEBUG 1

#ifndef DISABLE_LOGGING

#ifdef WIN32
#define __LITTLE_FUNCTION__ __FUNCSIG__ /* __FUNCDNAME__ */ 
#else
#define __LITTLE_FUNCTION__ __FUNCTION__ 
#endif

#define log_function() \
{ \
    std::stringstream _final_log_ss; \
    _final_log_ss << __LITTLE_FUNCTION__; \
    _final_log_ss << "()"; \
    _final_log_ss << "["; \
    _final_log_ss << __LINE__; \
    _final_log_ss << "][FUNCTION] - "; \
    littleshoot::logger::instance() << _final_log_ss.str(); \
}

#define log_info(ss) \
{ \
    std::stringstream _final_log_ss; \
    _final_log_ss << __LITTLE_FUNCTION__; \
    _final_log_ss << "()"; \
    _final_log_ss << "["; \
    _final_log_ss << __LINE__; \
    _final_log_ss << "][INFO] - "; \
    _final_log_ss << ss; \
    littleshoot::logger::instance() << _final_log_ss.str(); \
}

#if LOG_DEBUG

#define log_debug(ss) \
{ \
    std::stringstream _final_log_ss; \
    _final_log_ss << __LITTLE_FUNCTION__; \
    _final_log_ss << "()"; \
    _final_log_ss << "["; \
    _final_log_ss << __LINE__; \
    _final_log_ss << "][DEBUG] - "; \
    _final_log_ss << ss; \
    littleshoot::logger::instance() << _final_log_ss.str(); \
}

#else
    #define log_debug(ss) /* void */
#endif
    
#define log_error(ss) \
{ \
    std::stringstream _final_log_ss; \
    _final_log_ss << __LITTLE_FUNCTION__; \
    _final_log_ss << "()"; \
    _final_log_ss << "["; \
    _final_log_ss << __LINE__; \
    _final_log_ss << "][ *** ERROR *** ] - "; \
    _final_log_ss << ss; \
    littleshoot::logger::instance() << _final_log_ss.str(); \
}

#else
#define log_function() \
    /* void */
#define log_info(ss) \
    /* void */
#define log_debug(ss) \
    /* void */
#define log_error(ss) \
    /* void */

#endif // DISABLE_LOGGING

#endif // LITTLE_SHOOT_DEBUG_HPP
