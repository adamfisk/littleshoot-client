/*
 *  random.cpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/10/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#include <ctime>

#include <boost/pool/detail/singleton.hpp>
#include <boost/random/uniform_real.hpp>

#include <debug.hpp>
#include <random_number.hpp>

using namespace littleshoot;

random_number::random_number()
{
    log_function();
    
    generator_.seed(static_cast<unsigned int>(
        std::time(0))
    );
}

random_number & random_number::instance()
{
    using boost::details::pool::singleton_default;
                
    random_number & instance_ = singleton_default<random_number>::instance();
    
    return instance_;
}

boost::uint32_t random_number::get_uint32()
{
    static const boost::uint32_t begin = (std::numeric_limits<
        boost::uint32_t
    >::min)();
    static const boost::uint32_t end = (std::numeric_limits<
        boost::uint32_t
    >::max)();
    return random_number::instance().from_range(begin, end);
}

template <typename T>
T random_number::from_range(T begin, T end)
{
    boost::uniform_real<> range(begin, end);
    
    boost::variate_generator<
        boost::mt19937&, boost::uniform_real<> > dice(
        generator_, range
    );

    return (T)dice();
}
