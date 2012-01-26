/*
 *  test_launcher.cpp
 *  littleshoot
 *
 *  Created by Julian Cain on 1/17/09.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#include <iostream>
#include <string>
#include <vector>

#include <path.hpp>

using namespace littleshoot;

bool run(const char * path, std::vector<std::string> args)
{
	try
	{
        // ...
    } 
	catch (std::exception & e)
	{
		std::cerr << 
            "Fatal error launching LittleShoot(" << e.what() << ")." << 
        std::endl;
	}
    
    return false;
}

bool test_launcher()
{
    std::vector<std::string> args;
    
    const char * little_shoot_path = path::littleshoot_path().c_str();
    args.push_back(little_shoot_path);
    
    std::cout << "Running with path: " << little_shoot_path << std::endl;

    return run(little_shoot_path, args);
}

int main(int argc, char * argv[])
{
    assert(test_launcher());
    
    return 0;
}

