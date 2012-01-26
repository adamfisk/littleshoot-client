#!/usr/bin/env bash

#LS_SVN=http://svn.littleshoot.org/svn/littleshoot
LS_CONFIG=http://littleshoot_config.s3.amazonaws.com

function installAll()
{
mkdir atlassian
cd atlassian
ATLASSIAN_HOME=`pwd`
#curl -o crowd.bash $LS_CONFIG/crowd.bash                          
curl -o jira.bash $LS_CONFIG/jira.bash                           
curl -o bamboo.bash $LS_CONFIG/bamboo.bash                           
chmod +x *.bash

#./crowd.bash
cd $ATLASSIAN_HOME
./jira.bash
cd $ATLASSIAN_HOME
./bamboo.bash
cd $ATLASSIAN_HOME

cd ..
}

