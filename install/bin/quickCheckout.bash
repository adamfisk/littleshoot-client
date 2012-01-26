#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

ARGS=1  # One arg to script expected.

if [ $# -ne "$ARGS" ]
then
    die "$0: Received $# args...true or false arg required for pro or free"
fi

# We get the CVS login out of the way because otherwise we'll be prompted in the
# middle of the install script, often causing it to hang.
#export CVSROOT=:pserver:guest@cvs.limewire.org:/cvs || die "Could not set CVS root"
#cvs login

is_pro=$1

pushd ../../../..

INSTALL_DIR=temp_install_dir
#cos="server bin client common etc install integration-tests mojo shoot src pom.xml site.bash"
#svnDirs="npapi boost libtorrent bin jni lib client common etc install integration-tests mojo p2p"
svnDirs="npapi boost libtorrent bin jni lib client common etc install integration-tests mojo"

echo "Unloading from launchd..."
launchctl unload /Library/LaunchAgents/org.littleshoot.littleshoot.plist

function checkout()
{
  echo "Checking out repository"
  local svnPath="http://svn.littleshoot.org/svn/littleshoot/trunk"
  mkdir $INSTALL_DIR
#  svn co http://svn.littleshoot.org/svn/littleshoot/trunk $INSTALL_DIR/trunk --depth files || die "Could not check out trunk files"

  git clone https://github.com/adamfisk/littleshoot-client.git || die "Could not check out client" 
  pushd $INSTALL_DIR/trunk || die "Could not move to trunk"
  for x in $svnDirs
  do
    echo "Updating $x" 
    svn up $x || die "Could not check out $x"
  done  
  popd

#  for x in $svnDirs
#  do
#    local svnPath="http://svn.littleshoot.org/svn/littleshoot/trunk"
#    echo "Checking out $svnPath/$x" 
    #svn co $svnPath temp_install_dir/trunk/$x || die "Could not checkout $x from repo"
    #svn co --ignore-externals $svnPath temp_install_dir/$x || die "Could not checkout $x from repo"
    #svn co $svnPath temp_install_dir/$x || die "Could not checkout $x from repo"
#    svn co $svnPath/$x temp_install_dir/trunk/$x || die "Could not checkout $x from repo"
#  done  
}

test -d $INSTALL_DIR || checkout 
test -d $INSTALL_DIR/trunk || checkout 

cd $INSTALL_DIR
#mkdir trunk
cd trunk

echo "Running in `pwd`"

echo "Removing install dir..."
#uname -a | grep ygwin && rm -rvf install/*.jar && rm -rvf install/*.exe 
#uname -a | grep Darwin && rm -rvf install
rm -rvf install
rm -rvf install
echo "Removing other dynamic dirs for good measure..."
#rm -rfv npapi/mozilla/windows/littleshoot.vcproj/Release/
#rm -rfv jni/

echo "Removing old dlls and jnilibs..."
#rm -rvf lib/*.dll
rm -rvf lib/*.jnilib

#svn up --ignore-externals
svn up
#for x in $cos
#do 
  # This is useful if we're testing and aren't doing full checkouts
#  svn up $x || die "Could not update app"
#done


# We can't do this yet because the DLLs aren't yet built!!
#echo "Copying dll files..."
#function copyDlls()
#{
#  cp lib/*.dll install/win || die "Could not copy dll files"
#}

#uname -a | grep ygwin && copyDlls 

function downloadTorrentJniLib()
{
  # Grab the compressed, pre-built JNI lib
  echo "Downloading pre-built LibTorrent jnilib"
  curl -O http://littleshoot.s3.amazonaws.com/jnl-all.tgz
  tar xzvf jnl-all.tgz
  rm jnl-all.tgz
}

function buildMac()
{
  echo "Installing all projects..."
  #./build.bash || die "Could not build app"
  mvn install -Dmaven.test.skip=true || die "Could not build app"
  cp client/site/target/lastbamboo-client-site-1.0-jar-with-dependencies.jar install/LittleShoot.jar || die "Could not copy jar file"
  pushd lib
  test -f libjnltorrent.jnilib || downloadTorrentJniLib 
  popd
}

#pushd common/util/common/src/main/java/org/lastbamboo/common/util/
#svn revert ShootUtils.java
# We do this twice so changing the default doesn't matter.
#perl -pi -e "s/IS_PRO = false/IS_PRO = $is_pro/g" ShootUtils.java
#perl -pi -e "s/IS_PRO = true/IS_PRO = $is_pro/g" ShootUtils.java
#popd

#./buildLime.bash $is_pro || die "Could not build LimeWire from `pwd`"

uname -a | grep Darwin && buildMac

popd

exit 0
