#!/usr/bin/env bash
#
# This script installs Atlassians Bamboo standalone for MySQL.  It expands the
# Bamboo tar.gz file, sets up the database and takes care of setting the 
# Bamboo configuration directory.
#
# The run it, simply type ./bamboo.bash in the directory containing 
# the Bamboo tar.gz file, such as 'atlassian-bamboo-1.2.4-standalone.tar.gz'.
#

function die
{
echo $*
exit 1
}

function fileCheck()
{
echo "The bamboo tar.gz file must be in the current directory.  \
Press any key when you've successfully downloaded bamboo to this directory."
read ANY_KEY
ls altassian-bamboo*.tar.gz > /dev/null || die "Still could not find bamboo.  Exiting."
}

ls atlassian-bamboo*.tar.gz > /dev/null || fileCheck 

netstat -na | grep 3306 > /dev/null || die "MySQL does not appear to be running on port 3306.  JIRA cannot be installed without MySQL running"

echo "............................................................"
echo "  Hello $USER, let's start setting up Bamboo standalone."
echo "............................................................"

function askUser
{
  echo "Please enter your bamboo database user name (for example: 'bamboouser'):"
  read BAMBOO_USER_NAME

  echo "Please enter your bamboo database password:"
  read BAMBOO_PWD

  test -s "$MYSQL_ROOT_PWD" || \
  echo "Please enter your MySQL root password.  This is needed to create the bamboo databases." && \
  read MYSQL_ROOT_PWD

  echo "Where would your like to Bamboo to store your configuration files?  Please enter an absolute path."
  read BAMBOO_PATH

  echo "You entered $BAMBOO_PATH.  Is that the correct path to use? [y/n]"
  read CORRECT_PATH

case $CORRECT_PATH in
y)
  setPath $BAMBOO_PATH || die "Could not set the Bamboo configuration path."
  ;;
Y)
  setPath $BAMBOO_PATH || die "Could not set the Bamboo configuration path."
  ;;
*)
  die "OK, the path isn't correct.  Please run the script again."
  ;;
esac

}

function setPath
{
test -d $1 || die "Could not find the Bamboo path you specified.  Are you sure it exists?  Aborting."

echo "Setting bamboo.home to $1"
cat <<EOL >> $BAMBOO_HOME/webapp/WEB-INF/classes/bamboo-init.properties 
bamboo.home=$1
EOL
}  


installMySqlJdbc()
{
  echo "Downloading MYSQL JDBC connector..."
  # Somewhat bad to hard code this.
  curl -o mysqlj.tgz http://mirrors.24-7-solutions.net/pub/mysql/Downloads/Connector-J/mysql-connector-java-5.1.5.tar.gz
  tar xzf mysqlj.tgz
  mv mysql-connector-java-5.1.5/mysql-connector-java-5.1.5-bin.jar $BAMBOO_HOME/webapp/WEB-INF/lib || die "Could not move myql jdbc jar"
  rm mysqlj.tgz
}

configureMysql()
{
  cat <<EOL > bamboo.sql
create database if not exists atlassian_bamboo character set utf8;
GRANT ALL PRIVILEGES ON atlassian_bamboo.* TO '$BAMBOO_USER_NAME'@'localhost'
IDENTIFIED BY '$BAMBOO_PWD' WITH GRANT OPTION;
flush privileges;
EOL
  mysql -uroot -p$MYSQL_ROOT_PWD < bamboo.sql || die "Could not set up database for Bamboo.  Is your root password correct?"
}


function runBamboo
{
  echo "Expanding Bamboo file `ls ./atlassian-bamboo-*.tar.gz`"
  tar xzf `ls ./atlassian-bamboo-*.tar.gz` || die "Could not open Bamboo tgz file.  Aborting."

  pushd Bamboo
  BAMBOO_HOME=`pwd`
  popd

  askUser

  echo "Configuring MySQL..."
  configureMysql

  echo "Installing JDBC..."
  installMySqlJdbc

  echo "Starting Bamboo..."
  cd $BAMBOO_HOME
  ./bamboo.sh start || die "Could not start bamboo"
  echo "Waiting 20 seconds or so for the webapps to start up..."
  sleep 24

  echo ""
  echo ""
  echo ""
  echo "***************************************************************************************"
  echo "*"
  echo "*  Bamboo install complete.  The database is called atlassian_bamboo."
  echo "*  You can access the webapp at: "
  echo "*"
  echo "*  http://your_url:8085"
  echo "*"
  echo "***************************************************************************************"

}

runBamboo

exit 0
