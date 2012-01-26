
#pragma once

#include <fstream>

static void little_log(const char * blah)
{
    std::ofstream __ofs( 
		"littleshoot.activex.plugin.log", 
		std::ios_base::out | std::ios_base::app 
    ); 
	__ofs << blah << std::endl;
}

#ifndef NDEBUG
#ifndef log_debug
#define log_debug(str) little_log(str)
#endif
#ifndef log_function
#define log_function little_log(__FUNCTION__);
#endif
#else
#ifndef log_debug
#define log_debug(str) /* */
#endif
#ifndef log_function
#define log_function /* */
#endif
#endif // NDEBUG

