#!/usr/bin/env bash

# This script is used to install the static website.  It
# uses a temporary directory to get a pristine version of the site from the
# head of the trunk of the source repository.
#
# This script should be run only when the following preconditions have been
# met:
#
#   1.  The caller has already cached a login to the Subversion server.
# 
#   2.  The caller has a password-less connection to the server as user
#       'www'.  See ssh-agent for more details.

function die()
{
  echo $*
  exit 1
}

./runRemote.bash tomcat quickSiteBuild.bash $*

