#!/usr/bin/env bash
#
# This script installs the LittleShoot svn repository on a new machine.
# It assumes Apache is already present.
#

function die
{
  echo $1
  exit 1
}

S3_BUCKET=$1
SVN_DUMP_NAME=$2
SVN_CONF_URL=$3
REPO_NAME=$4

echo "Creating subversion repository..."
svnLoc=/mnt/srv/svn
mkdir /mnt/srv
mkdir $svnLoc 
svnadmin create $svnLoc/$REPO_NAME || die "Could not create subversion repo."

yum install mod_dav_svn || die "Error installing mod_dav_svn!!"

echo "Downloading subversion conf from $SVN_CONF_URL"
curl -o /etc/httpd/conf.d/subversion.conf $SVN_CONF_URL || die "Could not load svn conf file"

echo "Setting up SVN password file for afisk..."
if ! htpasswd -cs /etc/svn-auth-file afisk 
then
  echo "Password error.  Let's try again."
  htpasswd -cs /etc/svn-auth-file afisk || die "Error setting the password"
fi 

echo "Setting up SVN password file for jjc..."
if ! htpasswd -cs /etc/svn-auth-file jjc 
then
  echo "Password error.  Let's try again."
  htpasswd -s /etc/svn-auth-file jjc || die "Error setting the password"
fi 

echo "Downloading backed up repository from bucket: $S3_BUCKET"
#curl -o svn.dump.gz $SVN_DUMP_URL || die "Could not load svn backup" 
aws -get $S3_BUCKET $SVN_DUMP_NAME
gunzip svn.dump.gz || die "Could not unzip file"

echo "Loading backed up repository into subversion..."
svnadmin load $svnLoc/$REPO_NAME/ < svn.dump || die "Could not import backed up repository..."

echo "Giving Apache ownership of SVN repos..."
chown -R apache:apache /mnt/srv/svn/

exit 0
