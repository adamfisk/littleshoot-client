#!/usr/bin/env bash

function die
{
  echo $*
  exit 1
}

function checkProcesses()
{

numTomcats=`ps aux | grep java | grep -v grep | grep catalina | wc -l` || die "Could not get number of processes" 

echo "tomcats: $numTomcats"
if [ $numTomcats -ne "1" ]
then
  echo "Tomcats don't seem to be dying correctly!!  There are now $numTomcat Tomcat processes."
  die "PLEASE MANUALLY KILL TOMCAT INSTANCES!!"
fi
}

tmpBase=`basename $0`
tmpDir=`mktemp -d /tmp/${tmpBase}.XXXXXX` || die "Could not create temp dir"

localSitePath="http://svn.littleshoot.org/svn/littleshoot/trunk"
exportDir=$tmpDir/svn

svn export $localSitePath $exportDir || die "Could not export svn"

cd $exportDir

if [ $# -eq 1 ]
then
  echo "one args"
  if [ $1 = "true" ]
  then
    echo "Setting create tables to true"
    createdTables="true"
    perl -pi -e s/createTables=false/createTables=true/g server/site/src/main/webapp/WEB-INF/shoot.properties
  fi
fi

mvn install -Dmaven.test.skip=true || die "Could not build."

if [ "$createdTables" = "true" ]
then
  perl -pi -e s/createTables=true/createTables=false/g server/site/src/main/webapp/WEB-INF/shoot.properties
fi

cp server/site/target/lastbamboo-server-site.war /usr/local/tomcat/webapps
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
