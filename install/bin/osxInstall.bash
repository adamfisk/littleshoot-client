#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

ARGS=1  # One arg to script expected.
E_BADARGS=65

if [ $# -ne "$ARGS" ]
then
        echo "Usage: osxInstall.bash version, as in osxInstall.bash 0.893"
        exit $E_BADARGS
fi

source packJars.bash

echo "Making sure we have sudo"
# We just do this to test right away whether we've already got sudo permissions or not
sudo ls || die "Could not get sudo"

APP_VERSION="$1";

BASE_DIR=littleshoot
INSTALL_DIR=temp_install_dir

# The old files will corrupt the build, and they have root permissions.
sudo rm -rf /Users/Shared/LittleShoot || die "Could not remove old files"

tempBuildDir=~/$INSTALL_DIR/trunk/install/osx/temp_build_dir
rm -rf $tempBuildDir
mkdir $tempBuildDir || die "Could not create temp build dir for installing"

echo "Moving to client site dir"
cd ~/$INSTALL_DIR/trunk/client/site/

echo "Deleting old OSX app bundle..."
rm -rf ./target/LittleShoot.app/
#rm -rf ../../install/osx/LittleShoot/component/LittleShoot.app
#rm -rf ../../install/osx/temp_build_dir

# We now configure log4j paths in code before log4j ever runs
#echo "Customizing log4j config"
#pushd src/main/resources
#perl -pi -e "s,File=log.txt,File=/Library/Application\\\ Support/LittleShoot/log.txt,g" log4j.properties || die "Could not configure log4j"
#popd

echo "Building OSX app bundle..."
mvn clean
mvn lastbamboo:osxapp -DappVersion=$APP_VERSION || die "Could not build OSX app.  Exiting"

echo "Moving new app bundle to install dir"
mv -v target/LittleShoot.app $tempBuildDir/ || die "Could not move LittleShoot.app"

# Commented this out for build just prior to 1.0 release -- 11/2/10
#mv -v ../../npapi/mozilla/mac/build/Release/LittleShootPlugIn.plugin $tempBuildDir/ || die "Could not copy LittleShoot Plugin!"

#cp -Rv ../../npapi/mozilla/mac/build/Release/LittleShootPlugIn.plugin $tempBuildDir/ || die "Could not copy LittleShoot Plugin!"

echo "Building LittleShoot install package..."
cd ../../install/osx
perl -pi -e "s/postFlighVersionToReplace/$APP_VERSION/g" postflight || die "Could not set the version in the postflight script"
 
rm -rf dmg/LittleShoot.mpkg || die "Could not remove old install package.  Exiting."

BUNDLE_JAVA_DIR=$tempBuildDir/LittleShoot.app/Contents/Resources/Java
pushd $BUNDLE_JAVA_DIR
cp ../../../../../org.littleshoot.littleshoot.plist . || die "Could not copy plist file to bundle"
packJars
popd

cp ../../lib/libjnltorrent.jnilib $BUNDLE_JAVA_DIR || die "Could not copy jni libs"
cp ../../lib/static/*.jnilib $BUNDLE_JAVA_DIR || die "Could not copy static jni libs"

packagemaker --verbose --doc ./LittleShoot.pmdoc --id "org.littleshoot.littleshoot_en_US" --out ./dmg/LittleShoot.mpkg || die "Could not build PackageMaker file"

/Developer/Tools/SetFile -a C ./dmg/LittleShoot.mpkg

echo "Running makefile..."
make || die "Could not make DMG"

echo "Internet enabling DMG..."
hdiutil internet-enable -yes LittleShoot.dmg

echo "Internet enabled in dmg: `hdiutil internet-enable -query LittleShoot.dmg`"

echo "Copying dmg to upload directory"
cp LittleShoot.dmg ~/$BASE_DIR/trunk/client/site/LittleShootPlugin.dmg

echo "Copying dmg for easy testing"
cp LittleShoot.dmg ~/Desktop

#echo "Removing pkg directory..."
#rm -rf LittleShoot.pkg 
rm wc.dmg
rm -rf template
exit 0

