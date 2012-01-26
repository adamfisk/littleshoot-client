#!/usr/bin/env bash

echo "Starting Windows install script..."

echo "Make sure we have sudo"
sudo ls
function die() {
  echo $*
  exit 1
}

ARGS=1  # One arg to script expected.
E_BADARGS=65

if [ $# -ne "$ARGS" ]
then
  echo "Usage: windowsInstall.bash version"
  exit $E_BADARGS
fi

startDir=`pwd`
APP_VERSION="$1";
echo "Building windows installer with app version: $APP_VERSION"

./quickCheckout.bash true || die "Could not check out"

INSTALL_DIR=../../../../temp_install_dir

pushd $INSTALL_DIR/trunk
svn up || die "Could not update svn"

echo "Cleaning and building all projects"
mvn clean
#mvn -U install -Dmaven.test.skip=true
mvn install -Dmaven.test.skip=true

echo "Moving to client site dir"
pushd client/site/
cp target/lastbamboo-*-jar-with-dependencies.jar $INSTALL_DIR/trunk/install/install4j/generated_install_files/LittleShoot.jar

#echo "Calling win install prep from dir `pwd`"
#mvn lastbamboo:win-install-prep || die "Could not prepare windows install"
#echo "About to run lastbamboo:win-launcher with appVersion: $APP_VERSION"
#mvn lastbamboo:win-launcher -DappVersion=$APP_VERSION || die "Could not build windows launcher"
#mvn lastbamboo:win-dist -DappVersion=$APP_VERSION || die "Could not create Windows distribution"

cd ../../install/install4j/generated_osx_files
cp ../../../lib/libjnltorrent.jnilib . || die "Could not copy dll"
#cp ../../../lib/nplittleshoot.dll . || die "Could not copy dll"
#cp ../../../lib/static/*.dll . || die "Could not copy static dlls!"

cd ..

/Applications/install4j\ 5/bin/install4jc -D "appName=SmartFiles,shortName=smartfiles,publisher=LittleShoot,publisherUrl=http://www.littleshoot.org" -m macos -r $APP_VERSION ./SmartFiles.install4j || die "Could not built installers"
smartFilesName=SmartFiles-$APP_VERSION.dmg
mv ~/temp_install_dir/trunk/install/install4j/smartfiles_macos.dmg $startDir/$smartFilesName

echo "Uploading $smartFilesName to Amazon!!"
#aws -putp smartfiles $smartFilesName

echo "Uploaded $smartFilesName"

popd
popd
exit 0
