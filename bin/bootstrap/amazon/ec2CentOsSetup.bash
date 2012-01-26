#!/bin/bash
echo "Hello $USER, let's start configuring everything to run LittleShoot..."
echo "........................................"

echo "Please enter your AWS access key ID"
read MY_AWS_ACCESS_KEY_ID

echo "Using access key id: $MY_AWS_ACCESS_KEY_ID"
echo "Please enter your AWS access key"
read MY_AWS_ACCESS_KEY
echo "Using secret access key: $MY_AWS_ACCESS_KEY"

echo "Create the database tables from scratch, overwriting existing data on this machine?  Please enter true or false."
read CREATE_MYSQL_TABLES
echo "Create MYSQL tables set to: $CREATE_MYSQL_TABLES"

echo "Please enter your MYSQL root passord:"
read MYSQL_ROOT_PASS

echo "Please enter the password for the littleshoot database user:"
read MYSQL_BAMBOO_PASS

function die
{
    echo $1
    exit 1
}

## Need the users first because they'll need to take ownership of files early on.
echo "Adding all users"
FRESH_PATH="PATH=/usr/local/maven/bin:/usr/local/tomcat/bin:$PATH"
users=("www" "tomcat" "mysql")
for user in ${users[@]}
do
    adduser $user
    echo $FRESH_PATH >> /home/$user/.bashrc
    source /home/$user/.bashrc
    su $user -c "mkdir ~/.ssh"
    su $user -c "chmod 755 ~/.ssh"
    su $user -c "touch ~/.ssh/authorized_keys"
    su $user -c "chmod 600 ~/.ssh/authorized_keys"
done

# Now put everything we need in /usr/local
cd /usr/local/
echo "Downloading maven"
wget http://mirror.cc.columbia.edu/pub/software/apache/maven/binaries/apache-maven-2.0.8-bin.tar.gz
tar xzf apache-maven-2.0.8-bin.tar.gz
rm apache-maven-2.0.8-bin.tar.gz
ln -s apache-maven-2.0.8 maven

echo "Downloading Tomcat"
wget http://apache.tradebit.com/pub/tomcat/tomcat-6/v6.0.14/bin/apache-tomcat-6.0.14.tar.gz
tar xzf apache-tomcat-6.0.14.tar.gz
rm apache-tomcat-6.0.14.tar.gz
ln -s apache-tomcat-6.0.14 tomcat
chown -R tomcat tomcat/
chgrp -R tomcat tomcat/
TC_HOME=/usr/local/tomcat

echo "Downloading Apache"
yum install httpd || die "Error installing Apache!!"
   

#wget http://www.ip97.com/apache.org/httpd/httpd-2.2.8.tar.gz
#tar xzvf httpd-2.2.8.tar.gz
#rm httpd-2.2.8.tar.gz
#cd httpd-2.2.8
#./configure --prefix=/etc/httpd
#make
#make install

# Now we need to modify the Apache config
echo "Configuring Apache's httpd.conf"
# Persistent connections give a significant performance boost"
perl -p -i -e 's/KeepAlive Off/KeepAlive On/' /etc/httpd/conf/httpd.conf || die "Could not configure httpd.conf"

# The additions below allow use to configure the maven repository path
# separately from the other static files since they're updateed differently.
#
# We also set gzip compression by file type.
cat <<EOL >> /etc/httpd/conf/httpd.conf
Alias /maven/ "/var/maven/"

<Directory "/var/maven">
    Options Indexes MultiViews
    AllowOverride None
    Order allow,deny
    Allow from all
</Directory>

AddOutputFilterByType DEFLATE text/html text/plain text/xml application/x-javascript text/css application/json

EOL

echo "Configuring proxy_ajp.conf"
cat <<EOL >> /etc/httpd/conf.d/proxy_ajp.conf
ProxyPass /lastbamboo-server-site/ ajp://localhost:8009/lastbamboo-server-site/
ProxyPass /lastbamboo-common-bug-server/ ajp://localhost:8009/lastbamboo-common-bug-server/

EOL

echo "Setting up our custom maven repo..."
# This one's shifty.  We need the maven repo to exist for the build step below.
# Our pom.xml, though, uses "localhost" as one of it's repos, so it should 
# find it no problem.
cd /var/
mkdir maven
cd maven
wget https://s3.amazonaws.com:443/04G2SEBTMTS8S59X1SR2-maven/maven_our_repo.tgz
tar xzf maven_our_repo.tgz

echo "Checking out LittleShoot...will take awhile..."
cd /home/tomcat
su tomcat -c "svn co -q http://svn.littleshoot.org/svn/littleshoot"
echo "Setting AWS access keys"
su tomcat -c "mkdir ~/.ec2"
cat <<EOL > /home/tomcat/.ec2/ec2.properties
accessKeyId=$MY_AWS_ACCESS_KEY_ID
accessKey=$MY_AWS_ACCESS_KEY

EOL

echo "Setting db props"
su tomcat -c "perl -pi -e s/createTables=false/createTables=true/g ~/lastbamboo/trunk/common/bug-server/src/main/webapp/WEB-INF/shoot.properties"
su tomcat -c "perl -pi -e s/createTables=false/createTables=true/g ~/lastbamboo/trunk/server/site/src/main/webapp/WEB-INF/shoot.properties"

chown -R tomcat:tomcat /home/tomcat

echo "Compiling LittleShoot...will take awhile because it needs to download maven dependencies..."
cd lastbamboo/trunk
su tomcat -c "mvn -q install -Dmaven.test.skip=true" || die "LittleShoot build failed!!"

echo "Copying site webapp war to tomcat"
su tomcat -c "cp ~/lastbamboo/trunk/server/site/target/lastbamboo-server-site.war $TC_HOME/webapps"

echo "Copying bugs webapp war to tomcat"
su tomcat -c "cp ~/lastbamboo/trunk/common/bug-server/target/lastbamboo-common-bug-server.war $TC_HOME/webapps"

function mysqlInstall
{
  ## We manually install msyql below because the yum packages don't properly configure the service.
  ## We wait until here to install mysql because we need the my.cnf file from the litttleshoot repository.
  echo "Manually downloading and installing mysql rpms"
  wget http://dev.mysql.com/get/Downloads/MySQL-5.0/MySQL-server-community-5.0.45-0.rhel5.i386.rpm/from/http://mirror.trouble-free.net/mysql_mirror/
  rpm -Uvh MySQL-server-community-5.0.45-0.rhel5.i386.rpm || die "Could not install mysql!"

  wget http://dev.mysql.com/get/Downloads/MySQL-5.0/MySQL-client-community-5.0.45-0.rhel5.i386.rpm/from/http://mirror.trouble-free.net/mysql_mirror/
  rpm -Uvh MySQL-client-community-5.0.45-0.rhel5.i386.rpm || die "Could not install mysql client!"

  echo "Stopping mysql so we can configure it..."
  service mysql stop || die "Could not stop mysql!"
}

function mysqlYumInstall
{
  yum install mysql-server
  # This doesn't actually start the service.  We'll start it later.
}

function mysqlConfig
{
  echo "Creating mysql data directories"
  mkdir /mnt/mysql_data
  mkdir /mnt/mysql_data/tmp
  mkdir /mnt/backups
  mkdir /mnt/backups/mysql
  
  # Creates a directory for s3sync config
  mkdir /etc/s3conf
  mkdir /home/mysql/s3sync
  mkdir /home/mysql/s3sync/certs

  # We could actually create certs here, but we're not worried about 
  # using ssl for just backups to s3
  cat <<EOL > /etc/s3conf/s3config.yml
aws_access_key_id: $MY_AWS_ACCESS_KEY_ID
aws_secret_access_key: $MY_AWS_ACCESS_KEY
ssl_cert_dir: /home/mysql/s3sync/certs
EOL

  mkdir /usr/local/mysql
  mkdir /usr/local/mysql/var/
  chown -R mysql:mysql /mnt/mysql_data/
  chown -R mysql:mysql /home/mysql/
  chown -R mysql:mysql /usr/local/mysql/

  # Give mysql ownership of the backups dir for now 
  # Just not sure who else to give it to!
  chown -R mysql:mysql /mnt/backups/

  cp /home/tomcat/lastbamboo/trunk/etc/my.cnf /etc/my.cnf

  # Grab the name of the localhost so we can remove anonymous root access
  # for the mysql account
  HOST_NAME=`uname -n`
  perl -pi -e "s/log-bin/log-bin=$HOST_NAME-bin/g" /etc/my.cnf

  service mysqld start || die "Could not start mysql!!"

  echo "Setting MYSQL root password..."
  /usr/bin/mysqladmin -u root password $MYSQL_ROOT_PASS || die "Could not set mysql root password!"

  echo "Using host name: $HOST_NAME"
  echo "Creating and configuring databases..."
  # We drop a fair number of possible anonymous accounts below because we've
  # seen different mysqls create different accounts by default
  cat <<EOL > db.sql
create database if not exists bamboo;
create database if not exists bamboo_bugs;
GRANT ALL PRIVILEGES ON *.* TO 'bamboo'@'localhost'
IDENTIFIED BY '$MYSQL_BAMBOO_PASS' WITH GRANT OPTION;
DROP USER ''@'$HOST_NAME';
DROP USER ''@'localhost';
SET PASSWORD FOR 'root'@'$HOST_NAME' = PASSWORD('$MYSQL_ROOT_PASS');
SET PASSWORD FOR 'root'@'localhost' = PASSWORD('$MYSQL_ROOT_PASS');
EOL

  # If any of the above commands fail, all the ones after them fail.  That
  # would not be acceptable, so we get out.
  mysql -uroot -p$MYSQL_ROOT_PASS < db.sql || die "Could not set up database"
  echo "Finished installing mysql!"
}

mysqlYumInstall || die "Could not install mysql with yum!!"
mysqlConfig || die "Could not configure mysql!!"

echo "Now copying static site"
STATIC_SITE_PATH=/var/www/html/
cp -R /home/tomcat/lastbamboo/trunk/server/static/release/src/main/webapp/* $STATIC_SITE_PATH 
chown -R www $STATIC_SITE_PATH
chgrp -R www $STATIC_SITE_PATH

su www -c "chmod -R o+rX $STATIC_SITE_PATH"

echo "Starting tomcat..."
su tomcat -c "/usr/local/tomcat/bin/startup.sh" || die "Could not start tomcat"

echo "Starting Apache"
service httpd start || die "Could not start Apache"

echo "Resetting db props"
# Set the default to be to not overwrite existing tables
su tomcat -c "perl -pi -e s/createTables=true/createTables=false/g ~/lastbamboo/trunk/common/bug-server/src/main/webapp/WEB-INF/shoot.properties"
su tomcat -c "perl -pi -e s/createTables=true/createTables=false/g ~/lastbamboo/trunk/server/site/src/main/webapp/WEB-INF/shoot.properties"

chown tomcat:tomcat /home/tomcat/db.properties

# Now setup the automated backup to s3
mkdir s3Temp
cd s3Temp
wget http://s3.amazonaws.com/ServEdge_pub/s3sync/s3sync.tar.gz || die "Could not download s3sync"
tar xzf s3sync.tar.gz

# Not sure what the tgz expands to, so just copy all the ruby files to usr/local/bin
mv `find . -name "*.rb"` /usr/local/bin

cp /home/tomcat/lastbamboo/trunk/bin/dbBackup.sh /usr/local/bin || die "Could not copy script from $0!"
cp /home/tomcat/lastbamboo/trunk/bin/s3Backup.sh /usr/local/bin || die "Could not copy script from $0!"

chmod +x /usr/local/bin/*

echo "Changing bucket to use our unique ID..."
perl -pi -e "s/S3BUCKET=/S3BUCKET=$MY_AWS_ACCESS_KEY_ID-/g" /usr/local/bin/s3Backup.sh

echo "Creating the backups bucket..."
./s3cmd.rb createbucket $MY_AWS_ACCESS_KEY_ID-backups

cat <<EOL > /usr/local/bin/backup.sh
#!/bin/sh

# Create database backups
dbBackup.sh bamboo bamboo $MYSQL_BAMBOO_PASS
dbBackup.sh bamboo_bugs bamboo $MYSQL_BAMBOO_PASS

# Send the backups
s3backup.sh
EOL

echo "Adding backup script to crontab..."

# Grab the current crontab settings.
crontab -u $USER -l > crontab.file.$USER
# Append the new -- backing up every ten minutes...
echo "*/10 * * * *   /usr/local/bin/backup.sh" >> crontab.file.$USER
# And update
crontab -u $USER crontab.file.$USER

echo "Done!"

