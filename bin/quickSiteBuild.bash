#!/usr/bin/env bash

function die
{
  echo $*
  exit 1
}

function checkProcesses()
{

local numTomcats=`ps aux | grep java | grep -v grep | grep catalina | wc -l` || die "Could not get number of processes" 

echo "tomcats: $numTomcats"
if [ $numTomcats -ne "1" ]
then
  echo "Tomcats don't seem to be dying correctly!!  There are now $numTomcat Tomcat processes."
  die "PLEASE MANUALLY KILL TOMCAT INSTANCES!!"
fi
}

#tmpBase=`basename $0`
#tmpDir=`mktemp -d /tmp/${tmpBase}.XXXXXX` || die "Could not create temp dir"

localSitePath="http://svn.littleshoot.org/svn/littleshoot"
#exportDir=$tmpDir/svn

test -d littleshoot || svn co $localSitePath || die "Could not checkout svn"

#svn export $localSitePath $exportDir || die "Could not export svn"

cd littleshoot/trunk

function resetTables()
{
    echo "Setting create tables to true"
    perl -pi -e s/createTables=false/createTables=true/g server/site/src/main/webapp/WEB-INF/shoot.properties
}

# In case we didn't need a full checkout above -- can't hurt anyway.
svn up
if [ $# -eq 1 ]
then
  echo "one args"
  if [ $1 = "true" ]
  then
      echo "Are you sure you want to erase the existing tables? (y/n)"
      read tables
      case $tables in
          y)
          resetTables || die "Could not build"
          ;;
          Y)
          resetTables || die "Could not build"
          ;;
          *)
          echo "OK, just building."
      ;;
      esac

  fi
fi

mvn install -Dmaven.test.skip=true || die "Could not build."
perl -pi -e s/createTables=true/createTables=false/g server/site/src/main/webapp/WEB-INF/shoot.properties

cp server/site/target/lastbamboo-server-site.war /usr/local/tomcat/webapps || die "Could not copy war."
cd /usr/local/tomcat/

echo "About to shut down tomcat"
./bin/shutdown.sh || die "Could not shutdown tomcat!!"
rm -rf webapps/lastbamboo-server-site/*
rmdir webapps/lastbamboo-server-site
sleep 4
./bin/startup.sh || die "Could not start tomcat!!"

echo "Sleeping to make sure tomcat shut down successfully..."
sleep 20

checkProcesses $*
echo "Successfully reloaded the webapp"
