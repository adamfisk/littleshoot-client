#!/usr/bin/env bash
#
# This script performs all the JIRA configuration and setup for running 
# JIRA on MYSQL.  This includes creating the JIRA database and creating
# a user on the database.  
#
# If no arguments are passed to the script, it prompts you for the 
# data it needs.  Otherwise, you must pass all the required data on the
# command line.  This makes it easier to incorporate this script into
# other scripts if desired.
#
# If you decide to pass in arguments, they are (in order):
#
# 1) The name of the new jira user in the database.
# 2) The password of the new jira user in the database.
# 3) Your MYSQL root password to create the JIRA database.
# 4) The user account to install JIRA under.  This account should 
#    already exist on the system.
#
# To run this script: 
# 
# YOU MUST HAVE DOWNLOADED JIRA STANDALONE INTO YOUR CURRENT DIRECTORY
#
# That file should be the downloaded copy of JIRA standalone.
# 
# If you have any problems, please see the excellent guide at:
# http://confluence.atlassian.com/display/JIRA/Setting+up+JIRA+Standalone+and+MySQL+on+Linux
#

function die
{
echo $*
exit 1
}

function fileCheck()
{
echo "The tar.gz file must be in the current directory.  \
Press any key when you've successfully downloaded the file to this directory."
read ANY_KEY
ls atlassian-jira-*.tar.gz > /dev/null || die "Still could not find the tar.gz.  Exiting."
}

ls atlassian-jira-*.tar.gz > /dev/null || fileCheck

netstat -na | grep 3306 > /dev/null || die "MySQL does not appear to be running on port 3306.  JIRA cannot be installed without MySQL running"

function askUser
{
echo "Please enter your JIRA database user name:"
read JIRA_USER_NAME

echo "Please enter your JIRA database password:"
read JIRA_PWD

echo "Please enter your MySQL root password:"
read MYSQL_ROOT_PWD

echo "What's the name of the user account on this machine you'd like to install JIRA under?"
read USER_ACCOUNT
}

ARGS=4
if [ $# -ne "$ARGS" ]
then
    if [ $# -ne "0" ]
    then
        echo "Usage: jira.bash jira_mysql_user_name jira_mysql_password mysql_root_password user_account"
        echo "You can also just run ./jira.bash to have the script guide you through the setup process."
        die 
    else
        askUser
    fi
else
    JIRA_USER_NAME=$1
    JIRA_PWD=$2
    MYSQL_ROOT_PWD=$3
    USER_ACCOUNT=$4
fi

echo "............................................................"
echo "  Hello $USER, let's start setting up JIRA standalone."
echo "............................................................"
  
function modifyPort
{
  echo "What port would you like to use for JIRA?  The default is 8080."
  read CUSTOM_PORT
  echo "What shutdown port would you like to use for JIRA?  The default is 8005."
  read CUSTOM_SHUTDOWN_PORT
  echo "OK, got it.  Proceeding with install."
}


echo "Would you like to change the port JIRA runs on from the default of 8080? [y/n]"
read CHANGE_PORT
case $CHANGE_PORT in
y)
  modifyPort || die "Could not modify port"
  ;;
Y)
  modifyPort || die "Could not modify port"
  ;;
*)
  echo "OK, using default port of 8080.  Proceeding with install."
  CUSTOM_PORT=8080
  CUSTOM_SHUTDOWN_PORT=8005
  ;;
esac

function installJira
{
echo "Expanding `ls ./atlassian-jira-*.tar.gz`..."
tar xzf `ls ./atlassian-jira-*.tar.gz` || die "Could not open jira tgz file.  Aborting."

# Add a symbolic link to whichever version of JIRA we're running.
ln -s `ls -p | grep atlassian-jira-*/` jira

echo "Downloading MYSQL JDBC connector..."

# Somewhat bad to hard code this, but I don't think JIRA users alone will have much of an impact on this server.
curl -o mysqlj.tgz http://mirrors.24-7-solutions.net/pub/mysql/Downloads/Connector-J/mysql-connector-java-5.1.5.tar.gz
tar xzf mysqlj.tgz
mv mysql-connector-java-5.1.5/mysql-connector-java-5.1.5-bin.jar jira/common/lib || die "Could not move myql jdbc jar"

echo "Customizing server.xml..."
cp jira/conf/server.xml jira/server.xml.copy
perl -pi -e s/Server\ port=\"8005\"/Server\ port=\"$CUSTOM_SHUTDOWN_PORT\"/g jira/conf/server.xml || die "Could not set shutdown port" 
perl -pi -e s/Connector\ port=\"8080\"/Connector\ port=\"$CUSTOM_PORT\"/g jira/conf/server.xml || die "Could not set JIRA port"
perl -pi -e s/username=\"sa\"/username=\"$JIRA_USER_NAME\"/g jira/conf/server.xml || die "Could not modify jira user name"
perl -pi -e s/password=\"\"/password=\"$JIRA_PWD\"/g jira/conf/server.xml || die "Could not modify jira password"
perl -pi -e s/driverClassName=\"org.hsqldb.jdbcDriver/driverClassName=\"com.mysql.jdbc.Driver/g jira/conf/server.xml
perl -pi -e s/jdbc:hsqldb:\\$\{catalina.home\}\\/database\\/jiradb\"/jdbc:mysql:\\/\\/localhost\\/jiradb?autoReconnect\=true\&amp\;useUnicode\=true\&amp\;characterEncoding\=UTF8\"\\/\>/g jira/conf/server.xml || die "Could not set jdbc"
perl -pi -e s/minEvictableIdleTimeMillis\=/\<\\!--minEvictableIdleTimeMillis\=/g jira/conf/server.xml || die "Could not comment out hsql section"
perl -pi -e s/\"20\"\ \\/\>/\"20\"\ \\/\>--\>/g jira/conf/server.xml || die "Could not finish comment"


echo "Customizing entityengine.xml..."
cp jira/atlassian-jira/WEB-INF/classes/entityengine.xml jira/entityengine.xml.copy || die "Could not make entityengine backup"
cp jira/atlassian-jira/WEB-INF/classes/entityengine.xml . || die "Could not copy entityengine to current directory"

perl -pi -e s/name=\"defaultDS\"\ field-type-name=\"hsql\"/name=\"defaultDS\"\ field-type-name=\"mysql\"/g entityengine.xml || die "Could not set entityengine database to MYSQL"
perl -pi -e s/schema-name=\"PUBLIC\"//g entityengine.xml || die "Could not remove public schema from entiry engine"

mv entityengine.xml jira/atlassian-jira/WEB-INF/classes/ || die "Could not move entity engine"

chown -R $USER_ACCOUNT jira || die "Could not set permissions to specified user: $USER_ACCOUNT"

cat <<EOL > jira.sql
create database if not exists jiradb character set utf8;
GRANT ALL PRIVILEGES ON jiradb.* TO '$JIRA_USER_NAME'@'localhost'
IDENTIFIED BY '$JIRA_PWD' WITH GRANT OPTION;
flush privileges;
EOL
mysql -uroot -p$MYSQL_ROOT_PWD < jira.sql || die "Could not set up database for JIRA.  Is your root password correct?"
echo "Starting JIRA on port $CUSTOM_PORT..."
./jira/bin/startup.sh || die "Could not start JIRA"

echo ""
echo "-----------------------------------------------------------------------------------------------------------------"
echo "  Great, JIRA's starting up.  You should be able to access it momentarily on port $CUSTOM_PORT on this machine."
echo "-----------------------------------------------------------------------------------------------------------------"
}

installJira

exit 0
