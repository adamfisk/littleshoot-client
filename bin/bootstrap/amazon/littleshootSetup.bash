#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

curl -o centOsBase.bash http://svn.littleshoot.org/svn/littleshoot/trunk/bin/bootstrap/amazon/centOsBase.bash
chmod +x centOsBase.bash || die "Could not set permissions."
source ./centOsBase.bash || die "Could not source setup file."

# OK, we need to tell the Cent OS base script everything
# we want to load in the correct order.
createAwsFile || die "Could not create AWS auth file"
installUsers || die "Could not create users!"
installMaven || die "Could not install maven!"
installTomcat || die "Could not install Tomcat!"
installApache || die "Could not install Apache!"
addProxyForward lastbamboo-server-site || die "Could not add forward!"
addProxyForward lastbamboo-common-bug-server || die "Could not add forward!"

installBackup || die "Could not set up backups!"
#installSvn || die "Could not install SVN!"
installMysql || die "Could not install MySQL"
#installAtlassian || die "Could not install Atlassian"
#customMavenRepo || die "Could not set up LittleShoot"

# This also starts tomcat
setupLittleShoot || die "Could not set up LittleShoot"
loadStaticSite || die "Could not load static site!!!"

# This automatically installs a sitemap file for search engines
# and runs it daily under cron.
installSiteMap || die "Could not create sitemap script"

echo "Starting Apache"
service httpd start || die "Could not start Apache"
