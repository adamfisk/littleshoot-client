#!/usr/bin/env bash

function die
{
  echo $1
  exit 1
}

function askUser
{
echo "Please enter your MySQL root password:"
read MYSQL_ROOT_PWD

echo "Please enter the path to your my.cnf file:"
read MY_CNF_PATH

echo "You entered $MY_CNF_PATH.  Is that the correct path to use? [y/n]"
read CORRECT_PATH
case $CORRECT_PATH in
y)
  checkPath $MY_CNF_PATH || die "Could not set the path to my.cnf."
  ;;
Y)
  checkPath $MY_CNF_PATH || die "Could not set the path to my.cnf."
  ;;
*)
  die "OK, the path isn't correct.  Please run the script again."
  ;;
esac

}

function checkPath
{
ls $1 > /dev/null || die "Could not find the path you specified: $1.  Are you sure it exists?  Aborting."
}

ARGS=2
if [ $# -ne "$ARGS" ]
then
    if [ $# -ne "0" ]
    then
        echo "Usage: mysql.bash mysql_root_password path_to_my.cnf"
        echo "You can also just run ./mysql.bash to have the script guide you through the setup process."
        die
    else
        askUser
    fi
else
    MYSQL_ROOT_PWD=$1
    MY_CNF_PATH=$2
    checkPath $MY_CNF_PATH
fi

useradd mysql

echo "Enter a password for the MySQL user on the system."
passwd mysql

chown mysql:mysql $MY_CNF_PATH

function mysqlInstall
{
  yum install mysql-server
}


function mysqlConfig
{
  echo "Creating mysql data directories"
  mkdir /mnt/mysql_data
  mkdir /mnt/mysql_data/tmp
  mkdir /mnt/backups

  mkdir /usr/local/mysql
  mkdir /usr/local/mysql/var/
  chown -R mysql:mysql /mnt/mysql_data/
  chown -R mysql:mysql /home/mysql/
  chown -R mysql:mysql /usr/local/mysql/

  # Give mysql ownership of the backups dir for now 
  # Just not sure who else to give it to!
  chown -R mysql /mnt/backups/
  chgrp -R mysql /mnt/backups/

  cp $MY_CNF_PATH /etc/my.cnf

  # Grab the name of the localhost so we can remove anonymous root access
  # for the mysql account
  local HOST_NAME=`uname -n`
  perl -pi -e "s/log-bin/log-bin=$HOST_NAME-bin/g" /etc/my.cnf

  service mysqld start || die "Could not start mysql!!"

  echo "Setting MySQL root password..."
  /usr/bin/mysqladmin -u root password $MYSQL_ROOT_PWD || die "Could not set mysql root password!"

  echo "Setting default database settings..."
  cat <<EOL > defaults.sql
DROP USER ''@'$HOST_NAME';
DROP USER ''@'localhost';
SET PASSWORD FOR 'root'@'$HOST_NAME' = PASSWORD('$MYSQL_ROOT_PWD');
SET PASSWORD FOR 'root'@'localhost' = PASSWORD('$MYSQL_ROOT_PWD');
EOL

  mysql -uroot -p$MYSQL_ROOT_PWD < defaults.sql || die "Could not set up database"
  echo "Finished installing mysql!"
}

mysqlInstall
mysqlConfig
