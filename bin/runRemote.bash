#!/usr/bin/env bash

# This script copies and runs a local script on a website.  
#
# This script should be run only when the following preconditions have been
# met:
# 
#   1.  The caller has a password-less connection to the server as user
#       'www'.  See ssh-agent for more details.

function die
{   
  echo $*
  exit 1
}

user=$1
script=$2
shift
shift
test -e $script || die "Cannot run remote script at '$script'."

hosts=("littleshoot.org")
for host in ${hosts[@]}
do
    echo "Copying script to host: $host"
    scp $script $user@$host:/home/$user
    
    echo "Running script"
    ssh $user@$host ./$script $@
done

