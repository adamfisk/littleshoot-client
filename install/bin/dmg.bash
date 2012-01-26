#!/bin/bash
sudo ls
rm -rf /Library/Receipts/LittleShoot.pkg/
rm -rf ~/Library/LaunchAgents/org.lastbamboo.littleshoot.plist
rm ~/Desktop/LittleShoot.dmg
INST_BASE=~/temp_install_dir/trunk/install
INST_DIR=$INST_BASE/osx
rm $INST_DIR/dmg/LittleShoot.dmg
cp ../osx/Makefile $INST_DIR
cp ../osx/dmg/LittleShoot.dmg.bz2 $INST_DIR/dmg
#CONT_DIR=$INST_DIR/pkg/component/LittleShootUninstaller.app/Contents/
#LOC_DIR=../osx/pkg/component/LittleShootUninstaller.app/Contents

./osxInstall.bash 0.1
