/*
 *  random.hpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/10/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef LITTLESHOOT_PLUGIN_RANDOM_HPP
#define LITTLESHOOT_PLUGIN_RANDOM_HPP

#include <boost/cstdint.hpp>
#include <boost/noncopyable.hpp>
#include <boost/random.hpp>

namespace littleshoot {

    /**
     * Implements the mersenne twister random number generator.
     */
    class random_number : private boost::noncopyable
    {
        public:
        
            /**
             * Constructor
             */
            random_number();
        
            /**
             * The sole random object.
             * @return A statically allocated accessor.
             */
            static random_number & instance();
            
            /**
             * Get a random 32 bit unsigned integer.
             */
            boost::uint32_t get_uint32();
        
        private:
            
            /**
             * Get a random number between begin and end.
             */
            template <typename T> T from_range(
                T begin = (std::numeric_limits<T>::min)(), 
                T end = (std::numeric_limits<T>::max)()
            );
            
        protected:
        
            /**
             * The mersenne twister.
             */
            boost::mt19937 generator_;
    };
}; // namespace littleshoot

#endif // LITTLESHOOT_PLUGIN_RANDOM_HPP
