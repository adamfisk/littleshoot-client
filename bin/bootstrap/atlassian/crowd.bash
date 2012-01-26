#!/usr/bin/env bash
#
# Script for installing Atlassian Crowd and Crowd ID.  The webapps will
# run on port 8095 -- their default port.
#

die()
{
  echo $*
  exit 1
}

function fileCheck()
{
echo "The tar.gz file must be in the current directory.  \
Press any key when you've successfully downloaded the file to this directory."
read ANY_KEY
ls crowd*.tar.gz > /dev/null || die "Still could not find the tar.gz.  Exiting."
}

ls crowd*.tar.gz > /dev/null || fileCheck

netstat -na | grep 3306 > /dev/null || die "MySQL does not appear to be running on port 3306.  Crowd cannot be installed without MySQL running"

netstat -na | grep 8095 > /dev/null && die "Crowd already appears to be running on port 8095."

function askUser
{
  echo "Please enter your crowd database user name (for example: 'crowduser'):"
  read CROWD_USER_NAME

  echo "Please enter your crowd database password:"
  read CROWD_PWD

  test -s "$MYSQL_ROOT_PWD" || \
  echo "Please enter your MySQL root password.  This is needed to create the crowd and crowd open ID databases." && \
  read MYSQL_ROOT_PWD
}


setDataSource()
{
  local xmlFile=$1
  perl -pi -e s/username=\"sa\"/username=\"$CROWD_USER_NAME\"/g $xmlFile || die "Could not modify crowd user name"
  perl -pi -e s/password=\"\"/password=\"$CROWD_PWD\"/g $xmlFile || die "Could not modify crowd password"
  perl -pi -e s/driverClassName=\"org.hsqldb.jdbcDriver/driverClassName=\"com.mysql.jdbc.Driver/g $xmlFile
  cat $1 | sed "/minEvictableIdleTimeMillis/d" | sed "/timeBetweenEvictionRunsMillis/d" | sed "/maxActive/d" > out.txt || die "Could not delete hsql lines"
  mv out.txt $1
}

installMySqlJdbc()
{
  echo "Downloading MYSQL JDBC connector..."

  # Somewhat bad to hard code this.
  curl -o mysqlj.tgz http://mirrors.24-7-solutions.net/pub/mysql/Downloads/Connector-J/mysql-connector-java-5.1.5.tar.gz
  tar xzf mysqlj.tgz
  mv mysql-connector-java-5.1.5/mysql-connector-java-5.1.5-bin.jar $CROWD_HOME/apache-tomcat/common/lib || die "Could not move myql jdbc jar"
  rm mysqlj.tgz

  echo "Setting data source for crowd..." 
  
  local xmlFile=$CROWD_HOME/apache-tomcat/conf/Catalina/localhost/crowd.xml
  setDataSource $xmlFile
  perl -pi -e s/jdbc:hsqldb:\\$\{catalina.home\}\\/..\\/database\\/crowddb\"/jdbc:mysql:\\/\\/localhost\\/crowddb?autoReconnect\=true\&amp\;useUnicode\=true\&amp\;characterEncoding\=latin1\"/g $xmlFile || die "Could not set jdbc"
 
  echo "Setting data source for open ID server..."
  local xmlFile=$CROWD_HOME/apache-tomcat/conf/Catalina/localhost/openidserver.xml
  setDataSource $xmlFile
  perl -pi -e s/jdbc:hsqldb:\\$\{catalina.home\}\\/..\\/database\\/crowdopenidserverdb\"/jdbc:mysql:\\/\\/localhost\\/crowdopenidserverdb?autoReconnect\=true\&amp\;useUnicode\=true\&amp\;characterEncoding\=latin1\"/g $xmlFile || die "Could not set jdbc"
}

configureMysql()
{
  cat <<EOL > crowd.sql
create database if not exists crowddb character set latin1;
create database if not exists crowdopenidserverdb character set latin1;
GRANT ALL PRIVILEGES ON crowddb.* TO '$CROWD_USER_NAME'@'localhost'
IDENTIFIED BY '$CROWD_PWD' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON crowdopenidserverdb.* TO '$CROWD_USER_NAME'@'localhost'
IDENTIFIED BY '$CROWD_PWD' WITH GRANT OPTION;
flush privileges;
EOL
  mysql -uroot -p$MYSQL_ROOT_PWD < crowd.sql || die "Could not set up database for Crowd.  Is your root password correct?"
}

installCrowd()
{
  askUser
  echo "Expanding `ls ./crowd*.tar.gz`..."
  tar xzf `ls ./crowd*.tar.gz` || die "Could not open crowd tgz file.  Aborting."

  # Add a symbolic link to whichever version of CROWD we're running.
  ln -s `ls -p | grep crowd*/` crowd
  cd crowd
  CROWD_HOME=`pwd`

  perl -pi -e s/HSQLDialect/MySQLDialect/g build.properties

  echo "Installing xml task"
  pushd apache-tomcat/tools/ant/lib/
  wget http://prdownloads.sourceforge.net/xmltask/xmltask-v1.15.1.jar
  popd

  echo "Configuring MySQL..."
  configureMysql

  echo "Installing JDBC..."
  installMySqlJdbc

  cd $CROWD_HOME

  echo "Building new settings..."
  ./build.sh

  echo "Starting server..."
  ./start_crowd.sh &
  echo "Waiting 20 seconds or so for the webapps to start up..."
  sleep 24

  echo ""
  echo ""
  echo ""
  echo "***************************************************************************************"
  echo "*"
  echo "*  Crowd install complete.  You can access the webapps at: "
  echo "*"
  echo "*  http://your_url:8095/crowd/"
  echo "*  http://your_url:8095/openidserver"
  echo "*"
  echo "***************************************************************************************"
}

installCrowd

