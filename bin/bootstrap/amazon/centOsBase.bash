#!/usr/bin/env bash
#
# This script sets up our server that contains our developer resources and also
# acts as our monitor for all other servers.  The monitor will detect when
# servers go down and will respond accordingly.
#

function die
{
  echo $*
  exit 1
}

LS_SVN="http://svn.littleshoot.org/svn/littleshoot/trunk littleshoot/trunk"

createAwsFile()
{
  local awsProps=~/.littleshoot/littleshoot.properties
  if [ -e "$awsProps" ]
  then
    echo "LittleShoot props configured..."
    return 0
  fi
  echo "Please enter your AWS access key ID"
  read MY_AWS_ACCESS_KEY_ID
  echo "Please enter your AWS access key"
  read MY_AWS_ACCESS_KEY

  if [ -d ~/.littleshoot ]; then
      echo "~/.littleshoot exists..."
  else
      mkdir ~/.littleshoot || die "Could not make directory ~/.littleshoot.  Are you sure you have the necessary permissions?"
  fi

  echo "accessKeyId=$MY_AWS_ACCESS_KEY_ID" >> $awsProps || die "Could not write access key ID"
  echo "accessKey=$MY_AWS_ACCESS_KEY" >> $awsProps || die "Could not write access key"
}


function installUsers()
{
  ## Need the users first because they'll need to take ownership of files early on.
  echo "Adding all users"
  FRESH_PATH="PATH=/usr/local/maven/bin:/usr/local/tomcat/bin:$PATH"
  users=("www" "tomcat" "shoot") 
  for user in ${users[@]}
  do
    # This also creates a group for the user on cent OS.
    adduser $user
    echo $FRESH_PATH >> /home/$user/.bashrc
    source /home/$user/.bashrc
    su $user -c "mkdir ~/.ssh"
    su $user -c "chmod 755 ~/.ssh"
    su $user -c "touch ~/.ssh/authorized_keys"
    su $user -c "chmod 600 ~/.ssh/authorized_keys"
    passwd $user
  done
}

function installMaven()
{
  pushd /usr/local/
  echo "Downloading maven"
  wget http://mirror.cc.columbia.edu/pub/software/apache/maven/binaries/apache-maven-2.0.8-bin.tar.gz
  tar xzf apache-maven-2.0.8-bin.tar.gz || die "Could not expand maven."
  rm apache-maven-2.0.8-bin.tar.gz
  ln -s /usr/local/apache-maven-2.0.8 maven
  ln -s /usr/local/maven/bin/mvn bin/mvn
  popd
}

# Installs Google's sitemap generation file. 
function installSiteMap()
{
  pushd /usr/local
  mkdir sitemap
  cd sitemap
  curl -o sitemap_gen.py $LS_SVN/bin/sitemap_gen.py
  chmod +x /usr/local/sitemap/sitemap_gen.py 
  curl -o config.xml $LS_SVN/etc/config.xml 
  local cronfile=/etc/cron.daily/sitemap.cron 
  cat <<EOL >> $cronfile
/usr/local/sitemap/sitemap_gen.py --config=/usr/local/sitemap/config.xml
EOL

  chmod +x $cronfile
  popd 
}

function installTomcat()
{
  echo "Downloading Tomcat"
  pushd /usr/local
  wget http://apache.tradebit.com/pub/tomcat/tomcat-6/v6.0.14/bin/apache-tomcat-6.0.14.tar.gz
  tar xzf apache-tomcat-6.0.14.tar.gz || die "Could not expand tomcat."
  rm apache-tomcat-6.0.14.tar.gz
  ln -s /usr/local/apache-tomcat-6.0.14 tomcat
  chown -R tomcat tomcat/
  chgrp -R tomcat tomcat/
  TC_HOME=/usr/local/tomcat
  popd
}

function installApache()
{
  echo "Downloading Apache"
  yum install httpd || die "Error installing Apache!!"
  yum install mod_dav_svn || die "Error installing mod_dav_svn!!"
  yum install mod_ssl || die "Error installing mod_ssl!"

  # Now we need to modify the Apache config
  echo "Configuring Apache's httpd.conf"
  # Persistent connections give a significant performance boost"
  
  local httpdConf=/etc/httpd/conf/httpd.conf
  perl -p -i -e 's/KeepAlive Off/KeepAlive On/' $httpdConf || die "Could not configure httpd.conf"
  perl -p -i -e 's/KeepAliveTimeout \d+/KeepAliveTimeout 3/' $httpdConf || die "Could not configure httpd.conf"

  # The changes below set the gzip compression by file type and change the default etag setting so we
  # have the same etags across multiple servers in a cluster with no real performance difference on a
  # single server.
  cat <<EOL >> $httpdConf 
AddOutputFilterByType DEFLATE text/html text/plain text/xml application/x-javascript text/css application/json
FileETag MTime Size
EOL

  chown -R www:www /var/www/html/
  chown www:www $httpdConf
}

# Adds a line in the ajp config file to tell Apache to forward the specified path.
function addProxyForward()
{
  echo "Configuring proxy_ajp.conf"
  cat <<EOL >> /etc/httpd/conf.d/proxy_ajp.conf
ProxyPass /$1/ ajp://localhost:8009/$1/
EOL
}

function installBackup()
{
  echo "Downloading and running automated backup setup script..."
  curl -o awsBackup.bash $LS_SVN/bin/bootstrap/amazon/awsBackup.bash
  chmod +x awsBackup.bash
  ./awsBackup.bash || die "Could not setup automated backup to S3"
}


function installSvn()
{
  echo "Downloading and running svn setup script..."
  curl -o  svn.bash $LS_SVN/bin/bootstrap/svn/svn.bash
  chmod +x svn.bash
  curl -o  svnLittleShoot.bash $LS_SVN/bin/bootstrap/svn/svnLittleShoot.bash
  chmod +x svnLittleShoot.bash
  ./svnLittleShoot.bash || die "Could not setup svn"
}

function installMysql()
{

  echo "Please enter your MYSQL root passord:"
  read MYSQL_ROOT_PWD

  echo "Downloading and running MySQL setup script..."
  curl -o mysql.bash $LS_SVN/bin/bootstrap/mysql/mysql.bash
  chmod +x mysql.bash
  curl -o my.cnf $LS_SVN/etc/my.cnf
  ./mysql.bash $MYSQL_ROOT_PWD ./my.cnf || die "Could not install and configure MySQL!!"
}

function installAtlassian()
{
  echo "Downloading atlassian scripts..."
  curl -o atlassian.bash $LS_SVN/bin/bootstrap/atlassian/atlassian.bash
  chmod +x atlassian.atlassian
  source atlassian.bash
  installAll
}

function installAwsCommandLine()
{
  curl -o aws.tgz http://littleshoot.s3.amazonaws.com/aws-0.3.tgz
  tar xzvf aws.tgz
  pushd aws-0.3
  ./install.sh
  popd
}


function customMavenRepo()
{
  echo "Setting up our custom maven repo..."
  # This one's shifty.  We need the maven repo to exist for the build step below.
  # Our pom.xml, though, uses "localhost" as one of it's repos, so it should 
  # find it no problem.
  cd /var/
  mkdir maven
  cd maven
  wget http://s3.amazonaws.com/04G2SEBTMTS8S59X1SR2-maven/maven_our_repo.tgz
  tar xzf maven_our_repo.tgz

  test -e /etc/httpd/conf/httpd.conf || die "/etc/httpd/conf/httpd.conf doesn't exist.  Can't setup Apache for maven repo."
  cat <<EOL >> /etc/httpd/conf/httpd.conf
Alias /maven/ "/var/maven/"

<Directory "/var/maven">
    Options Indexes MultiViews
    AllowOverride None
    Order allow,deny
    Allow from all
</Directory>
EOL
}

function setupSipTurn()
{
  
  cd /home/shoot

  echo "Checking out LittleShoot.  This will take awhile."
  svn co -q $LS_SVN || die "Could not checkout LittleShoot."

  chown -R shoot:shoot /home/shoot
  cd littleshoot/trunk
  su shoot -c "mvn install -Dmaven.test.skip=true" || die "Could not compile."
  cd common/sip-turn || die "Could not change directories."

  echo "About the build LittleShoot..."
  su shoot -c "mvn lastbamboo:runscript"  || die "Could not create runscript"
 
  echo "Starting SIP and TURN server..." 
  su shoot -c "nohup ./run.sh &"   
}

function setupLittleShoot()
{
  echo "Checking out LittleShoot in quiet mode...will take awhile..."
  cd /home/tomcat
  
  mkdir .littleshoot
  cp ~/.littleshoot/littleshoot.properties .littleshoot

  su tomcat -c "svn co -q $LS_SVN"

  echo "Setting db props"
  perl -pi -e s/createTables=false/createTables=true/g littleshoot/trunk/server/site/src/main/webapp/WEB-INF/shoot.properties
  perl -pi -e s/createTables=false/createTables=true/g littleshoot/trunk/common/bug-server/src/main/webapp/WEB-INF/shoot.properties

  chown -R tomcat:tomcat /home/tomcat

  echo "Please enter the password for the littleshoot database user:"
  read MYSQL_BAMBOO_PWD

  cat <<EOL > littleshoot.sql
create database if not exists bamboo;
create database if not exists bamboo_bugs;
GRANT ALL PRIVILEGES ON bamboo.* TO 'bamboo'@'localhost'
IDENTIFIED BY '$MYSQL_BAMBOO_PWD' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON bamboo_bugs.* TO 'bamboo'@'localhost'
IDENTIFIED BY '$MYSQL_BAMBOO_PWD' WITH GRANT OPTION;
flush privileges;
EOL

  mysql -uroot -p$MYSQL_ROOT_PWD < littleshoot.sql || die "Could not set up database for LittleShoot.  Is your root password correct?"

  echo "Compiling LittleShoot...will take awhile because it needs to download maven dependencies..."
  cd littleshoot/trunk
  su tomcat -c "mvn -q install -Dmaven.test.skip=true" || die "LittleShoot build failed!!"
  
  echo "Copying site webapp war to tomcat"
  su tomcat -c "cp ~/littleshoot/trunk/server/site/target/lastbamboo-server-site.war $TC_HOME/webapps"

  echo "Copying bugs webapp war to tomcat"
  su tomcat -c "cp ~/littleshoot/trunk/common/bug-server/target/lastbamboo-common-bug-server.war $TC_HOME/webapps"

  echo "Starting tomcat..."
  su tomcat -c "/usr/local/tomcat/bin/startup.sh" || die "Could not start tomcat"

  echo "Resetting db props"
  # Set the default to be to not overwrite existing tables
  echo "Waiting for the server to startup before turning off table creation..."
  sleep 30
  echo "Echo, resetting table creation..."
  perl -pi -e s/createTables=true/createTables=false/g ~/littleshoot/trunk/common/bug-server/src/main/webapp/WEB-INF/shoot.properties
  perl -pi -e s/createTables=true/createTables=false/g ~/littleshoot/trunk/server/site/src/main/webapp/WEB-INF/shoot.properties

  # Just go back home.
  cd
}

function loadStaticSite()
{
  echo "Now copying static site"
  local STATIC_SITE_PATH=/var/www/html/
  cp -R /home/tomcat/littleshoot/trunk/server/static/release/src/main/webapp/* $STATIC_SITE_PATH
  chown -R www $STATIC_SITE_PATH
  chgrp -R www $STATIC_SITE_PATH
  su www -c "chmod -R o+rX $STATIC_SITE_PATH"
}

echo "Base functions loaded."

