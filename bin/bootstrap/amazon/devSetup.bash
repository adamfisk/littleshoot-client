#!/usr/bin/env bash
#
# This script sets up our server that contains our developer resources and also
# acts as our monitor for all other servers.  The monitor will detect when
# servers go down and will respond accordingly.
#

echo "Hello $USER, we'll now start configuring a LittleShoot development server..."
echo "........................................"

function die
{
    echo $*
    exit 1
}

#LS_SVN=http://svn.littleshoot.org/svn/littleshoot
LS_CONFIG=http://littleshoot_config.s3.amazonaws.com

awsProps=~/.littleshoot/littleshoot.properties
createAwsFile()
{
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

if [ -e "$awsProps" ]
then
    echo "LittleShoot props configured..."
else
    createAwsFile
fi

echo "Please enter your MYSQL root passord:"
read MYSQL_ROOT_PWD

echo "Please enter the password for the littleshoot database user:"
read MYSQL_BAMBOO_PWD

installAwsCommandLine()
{
  curl -o aws.tgz http://littleshoot.s3.amazonaws.com/aws-0.3.tgz
  tar xzvf aws.tgz
  pushd aws-0.3
  ./install.sh
  popd
}

installAwsCommandLine

## Need the users first because they'll need to take ownership of files early on.
addUsers()
{
  echo "Adding all users"
  FRESH_PATH="PATH=/usr/local/maven/bin:/usr/local/tomcat/bin:$PATH"
  users=("www" "tomcat") 
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
    #passwd $user
  done
}

#addUsers

# Now put everything we need in /usr/local
cd /usr/local/
echo "Downloading maven"
mavenName=apache-maven-2.0.9-bin.tar.bz2
wget http://mirror.cc.columbia.edu/pub/software/apache/maven/binaries/apache-maven-2.0.9-bin.tar.bz2
#wget http://mirror.cc.columbia.edu/pub/software/apache/maven/binaries/apache-maven-2.0.8-bin.tar.gz
tar xjf $mavenName
rm $mavenName
ln -s apache-maven-2.0.9 maven

installTomcat()
{
  echo "Downloading Tomcat"
  local tcName=apache-tomcat-6.0.16.tar.gz
  wget http://apache.siamwebhosting.com/tomcat/tomcat-6/v6.0.16/bin/apache-tomcat-6.0.16.tar.gz
  #wget http://apache.tradebit.com/pub/tomcat/tomcat-6/v6.0.14/bin/apache-tomcat-6.0.14.tar.gz
  tar xzf $tcName
  rm $tcName
  ln -s apache-tomcat-6.0.16 tomcat
  chown -R tomcat tomcat/
  chgrp -R tomcat tomcat/
  TC_HOME=/usr/local/tomcat
}

#installTomcat

echo "Downloading Apache"
yum install httpd || die "Error installing Apache!!"
yum install mod_dav_svn || die "Error installing mod_dav_svn!!"
yum install mod_ssl || die "Error installing mod_ssl!"

# Now we need to modify the Apache config
echo "Configuring Apache's httpd.conf"
# Persistent connections give a significant performance boost"
#perl -p -i -e 's/KeepAlive Off/KeepAlive On/' /etc/httpd/conf/httpd.conf || die "Could not configure httpd.conf"

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

#echo "Configuring proxy_ajp.conf"
#cat <<EOL >> /etc/httpd/conf.d/proxy_ajp.conf
#ProxyPass /lastbamboo-common-bug-server/ ajp://localhost:8009/lastbamboo-common-bug-server/
#EOL

echo "Downloading and running automated backup setup script..."
cd
curl -o awsBackup.bash $LS_CONFIG/awsBackup.bash
chmod +x awsBackup.bash
./awsBackup.bash || die "Could not setup automated backup to S3"

# Just make sure we're back in home.
cd

echo "Downloading and running svn setup script..."
curl -o  svn.bash $LS_CONFIG/svn.bash
chmod +x svn.bash
curl -o  svnLittleShoot.bash $LS_CONFIG/svnLittleShoot.bash
chmod +x svnLittleShoot.bash
./svnLittleShoot.bash || die "Could not setup svn"

echo "Downloading and running MySQL setup script..."
curl -o mysql.bash $LS_CONFIG/mysql.bash
chmod +x mysql.bash
curl -o my.cnf $LS_CONFIG/my.cnf
./mysql.bash $MYSQL_ROOT_PWD ./my.cnf || die "Could not install and configure MySQL!!"


echo "Downloading atlassian scripts..."
curl -o atlassian.bash $LS_CONFIG/atlassian.bash
chmod +x atlassian.bash
source atlassian.bash
installAll

echo "Setting up our custom maven repo..."
# This one's shifty.  We need the maven repo to exist for the build step below.
# Our pom.xml, though, uses "localhost" as one of it's repos, so it should 
# find it no problem.
cd /var/
mkdir maven
cd maven
wget http://s3.amazonaws.com/04G2SEBTMTS8S59X1SR2-maven/maven_our_repo.tgz
tar xzf maven_our_repo.tgz

setupLittleShoot()
{
  echo "Checking out LittleShoot...will take awhile..."
  cd /home/tomcat
  su tomcat -c "svn co -q $LS_SVN"
  echo "Setting AWS access keys"
  su tomcat -c "mkdir ~/.ec2"
  cat <<EOL > /home/tomcat/.ec2/ec2.properties
accessKeyId=$MY_AWS_ACCESS_KEY_ID
accessKey=$MY_AWS_ACCESS_KEY
EOL

  echo "Setting db props"
  su tomcat -c "perl -pi -e s/createTables=false/createTables=true/g ~/lastbamboo/trunk/common/bug-server/src/main/webapp/WEB-INF/shoot.properties"

  chown -R tomcat:tomcat /home/tomcat

  echo "Compiling LittleShoot...will take awhile because it needs to download maven dependencies..."
  cd lastbamboo/trunk
  su tomcat -c "mvn -q install -Dmaven.test.skip=true" || die "LittleShoot build failed!!"

  echo "Copying bugs webapp war to tomcat"
  su tomcat -c "cp ~/lastbamboo/trunk/common/bug-server/target/lastbamboo-common-bug-server.war $TC_HOME/webapps"

  echo "Starting tomcat..."
  su tomcat -c "/usr/local/tomcat/bin/startup.sh" || die "Could not start tomcat"

  echo "Resetting db props"
  # Set the default to be to not overwrite existing tables
  echo "Waiting for the server to startup before turning off table creation..."
  sleep 14
  su tomcat -c "perl -pi -e s/createTables=true/createTables=false/g ~/lastbamboo/trunk/common/bug-server/src/main/webapp/WEB-INF/shoot.properties"
}

#setupLittleShoot
  
echo "Starting Apache"
service httpd start || die "Could not start Apache"

echo "Done!"

