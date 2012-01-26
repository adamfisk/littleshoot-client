#!/usr/bin/env bash
#
# Passes the necessary URLs to our Subversion repository creation script 
# for LittleShoot.
#

function die
{
  echo $1
  exit 1
}

#LS_SVN=http://svn.littleshoot.org/svn/littleshoot
LS_SVN=http://littleshoot.s3.amazonaws.com/subversion.conf

echo "Downloading backed up repository..."
./svn.bash littleshoot_backups svn.dump.gz $LS_SVN littleshoot || die "Could not setup subversion."
