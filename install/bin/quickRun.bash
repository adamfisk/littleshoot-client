#!/bin/bash
sudo ls
pushd ~/temp_install_dir/trunk/
svn up
if ! mvn clean install -Dmaven.test.skip=true
then
   echo "BUILD FAILED"
   exit 74
fi

popd
rm -rf /Library/Receipts/LittleShoot.pkg/
rm -rf ~/Library/LaunchAgents/org.lastbamboo.littleshoot.plist
rm ~/Desktop/LittleShoot.dmg
INST_BASE=~/temp_install_dir/trunk/install
INST_DIR=$INST_BASE/osx
cp ../osx/Makefile $INST_DIR
#CONT_DIR=$INST_DIR/pkg/component/LittleShootUninstaller.app/Contents/
#LOC_DIR=../osx/pkg/component/LittleShootUninstaller.app/Contents

./osxInstall.bash 0.1
