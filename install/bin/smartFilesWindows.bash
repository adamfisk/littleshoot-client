#!/usr/bin/env bash

echo "Starting Windows install script..."

function die()
{
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

APP_VERSION="$1";
echo "Building windows installer with app version: $APP_VERSION"

INSTALL_DIR=../../../temp_install_dir

pushd $INSTALL_DIR/littleshoot-client
git pull origin master

echo "Cleaning and building all projects"
mvn clean
mvn -U install -Dmaven.test.skip=true

echo "Moving to client site dir"
pushd client/site/ || die "Could not move to client site?"
echo "Copying assembly jar from dir: `pwd`"
cp target/lastbamboo-*-jar-with-dependencies.jar ../../install/install4j/generated_install_files/LittleShoot.jar || die "Could not copy jar assembly"

#echo "Calling win install prep from dir `pwd`"
#mvn lastbamboo:win-install-prep || die "Could not prepare windows install"
#echo "About to run lastbamboo:win-launcher with appVersion: $APP_VERSION"
#mvn lastbamboo:win-launcher -DappVersion=$APP_VERSION || die "Could not build windows launcher"
#mvn lastbamboo:win-dist -DappVersion=$APP_VERSION || die "Could not create Windows distribution"

cd ../../install/install4j/generated_win_files
cp ../../../lib/jnltorrent.dll . || die "Could not copy dll"
#cp ../../../lib/nplittleshoot.dll . || die "Could not copy dll"
cp ../../../lib/static/*.dll . || die "Could not copy static dlls!"

cd ..

/Applications/install4j\ 5/bin/install4jc -D "appName=SmartFiles,shortName=smart,publisher=LittleShoot,publisherUrl=http://www.littleshoot.org" -m windows -r $APP_VERSION ./LittleShoot.install4j

echo "Uploading LittleShootPlugin.exe to Amazon!!"
aws -putp smartfiles LittleShootPlugin.exe

echo "Uploaded LittleShootPlugin.exe"

popd
popd
exit 0
