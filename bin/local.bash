#!/usr/bin/env bash

# This is a quick script for locally testing our server with essentially the
# same layout we use with production servers.  That is, we're running Apache
# locally with ajp proxy running to forward appropriate requests to 
# Tomcat.  
function die()
{
  echo $*
  exit 1
}


echo "Shutting down tomcat..."

webappName=lastbamboo-server-site
homeDir=../../..
tomcatDir=$homeDir/code/tomcat
#tomcatDir=$homeDir/tomcat
$tomcatDir/bin/shutdown.sh
trunkDir=$homeDir/littleshoot/trunk
serverDir=$trunkDir/server
commonDir=$trunkDir/common
apacheDir=/Library/WebServer/Documents/
#apacheDir=$homeDir/apache/htdocs
rsync -avz $serverDir/static/build/src/main/webapp/ $apacheDir || die "Could not sync"

webappDir=$serverDir/site/
bugsDir=$commonDir/bug-server/
cd $serverDir
mvn install -Dmaven.test.skip=true || die "Could not build webapp"


rm -rf $tomcatDir/webapps/$webappName
rm -rf $tomcatDir/webapps/lastbamboo-common-bug-server
cp $webappDir/target/$webappName.war $tomcatDir/webapps
cp $bugsDir/target/lastbamboo-common-bug-server.war $tomcatDir/webapps

#rsync -avz $webappDir/target/lastbamboo-server-site/ $tomcatDir || die "Could not sync"

function startTomcat()
{
  echo "Starting tomcat..."
  rm $tomcatDir/logs/catalina.out
  $tomcatDir/bin/startup.sh || return 1
  sleep 3 
}

startTomcat || die "Could not start tomcat"

#netstat -na | grep 8080 > /dev/null || startTomcat || die "Could not start tomcat"

if [ $# -eq 0 ]
then
  open http://local.littleshoot.org
else
  open http://local.littleshoot.org/$1
fi
