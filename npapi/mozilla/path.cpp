

#include <boost/filesystem/path.hpp>

#include "path.hpp"
#include "constants.hpp"

#include <debug.hpp>
#include <file.hpp>

using namespace littleshoot;

path::path()
{
    // ...
}

path::~path()
{
    // ..
}
        
path & path::instance()
{
    return boost::details::pool::singleton_default<path>::instance();
}

const std::string path::execPath()
{
#if defined(__APPLE__)
    static const std::string ret("/bin/launchctl");
#elif defined(_MSC_VER)
    static const std::string ret("");
#else
    static const std::string ret("");
#endif // __APPLE__
    return ret;
}

const std::vector<std::string> path::args()
{
    std::vector<std::string> args;
#if defined(__APPLE__)
    args.push_back(std::string("/bin/launchctl"));
    args.push_back(std::string("load"));
    args.push_back(std::string("-F"));
    args.push_back(std::string(
        "/Library/LaunchAgents/org.littleshoot.littleshoot.plist")
    );
#elif defined(_MSC_VER)
#else
#error ":FIXME:"
#endif // __APPLE__
    return args;
}

const std::string path::home_dir()
{
    std::string home;

    if (getenv("HOME"))
    {
        home = getenv("HOME");
    }
    else if (getenv("USERPOFILE"))
    {
        home = getenv("USERPOFILE");
    }
    else if(getenv("HOMEDRIVE") && getenv("HOMEPATH"))
    {
        home = (
            boost::format("%1%%2%") % getenv("HOMEDRIVE") %  getenv("HOMEPATH")
        ).str();
    }
    else
    {
        log_debug("Failed to determine user home directory, using ./");
        
        home = ".";
    }

#ifdef _WIN32
    return home + "\\";
#else
    return home + "/";
#endif
}

const std::string path::log_dir()
{
    std::string ret;

#ifdef _WIN32
    ret += getenv("APPDATA");
    ret += "/LittleShoot/";
#elif defined (__APPLE__)
    ret += "/Library/";
    ret += "Logs/";
    ret += "LittleShoot/";
#else
		ret = home_dir();
        ret += ".littleshoot/";
#endif
    boost::filesystem::path full_path = 
    boost::filesystem::system_complete(
    boost::filesystem::path(ret.c_str(), 
    boost::filesystem::native));
        
    if (!boost::filesystem::exists(full_path))
    {
        std::cout << "Log dir not found, creating: " << 
            full_path.native_file_string() <<
        std::endl;
                      
        boost::filesystem::create_directory(full_path);
    }
        
    return ret;
}

const std::string path::appdata_dir()
{
    std::string ret;
#ifdef _WIN32
    ret += getenv("APPDATA");
    ret += "/LittleShoot/";
#elif defined (__APPLE__)
    ret = home_dir();
    ret += "Library/";
    ret += "Application Support/";
    ret += "LittleShoot/";
#else
    ret = home_dir();
    ret += ".littleshoot/data/";
#endif
    
    boost::filesystem::path full_path = 
    boost::filesystem::system_complete(
    boost::filesystem::path(ret.c_str(), 
    boost::filesystem::native));
        
    if (!boost::filesystem::exists(full_path))
    {
        log_debug("Data dir not found, creating: "
            << full_path.native_file_string()
        );
                      
        boost::filesystem::create_directory(full_path);
    }
        
    return ret;
}

const std::string path::working_dir() 
{
    return
#if defined(__APPLE__)
    path::instance().working_dir_osx();
#elif defined(_MSC_VER)
    path::instance().working_dir_win_32();
#else
#error ":FIXME:"
    std::string("./LittleShoot");
#endif // __APPLE__
}

const std::string path::working_dir_osx()
{
    std::string ret = "/";
    return ret;
}

const std::string path::working_dir_win_32()
{
    std::string ret = "c:/Program Files/LittleShoot";
    return ret;
}

const std::string path::littleshoot_path() 
{
    return
#if defined(__APPLE__)
    path::instance().java_application_stub();

#elif defined(_MSC_VER)
    path::instance().win_32_path();
#else
    #error ":FIXME:"
    std::string("./LittleShoot");
#endif // __APPLE__
}

const std::string path::win_32_path()
{
    std::string prefix
#ifdef WIN32
    = getenv("ProgramFiles");
#else
    ;
#endif
    return std::string("\"" + prefix + "\\LittleShoot\\LittleShoot.lnk\"");
}

const std::string path::java_application_stub()
{
    std::string ret;
        
    ret = working_dir();
    ret += "Applications/";
    ret += "LittleShoot.app/";
    ret += "Contents/";
    ret += "MacOS/";
    ret += "LittleShoot";

    log_debug("Path to LittleShoot executable(%s)" << ret);
         
    return ret;
}
    
void path::create_directory(const std::string & dir)
{
    boost::filesystem::path full_path = 
        boost::filesystem::system_complete(
        boost::filesystem::path(dir.c_str(), boost::filesystem::native
        )
    );
        
    if (!boost::filesystem::exists(full_path))
    {
		/*
        log_debug("%s not found, creating dir(%s)", 
            full_path.string().c_str(), full_path.native_file_string().c_str()
        );
		*/
                      
        boost::filesystem::create_directory(full_path);
    }
}
    
void path::create_file(const std::string & file_path)
{
    boost::filesystem::path full_path = boost::filesystem::path(
        file_path.c_str()
    );

    if (!boost::filesystem::exists(full_path))
    {
        log_debug( full_path.string() << " not found, creating file" <<
            full_path.native_file_string()
        );
    }
    
    std::filebuf fbuf;
    
    fbuf.open(
        file_path.c_str(), std::ios_base::in | std::ios_base::out | 
        std::ios_base::trunc | std::ios_base::binary
    ); 
    fbuf.close();

    if (!boost::filesystem::is_regular_file(full_path))
    {
        log_debug("Creating file(%s)" << full_path.string());
    }
}
    
void path::create_runtime_paths_if()
{
    create_directory(data_dir.string());
    create_directory(_log_dir.string());
    create_directory(stream_dir.string());
    create_file(stream_lock_path);
    create_file(stream_data_path);
    create_file(ipc_lock_path);
    create_file(ipc_data_path);
}
