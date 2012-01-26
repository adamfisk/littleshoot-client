#!/usr/bin/env bash
rm -rf ~/Library/Internet\ Plug-Ins/LittleShootPlugIn.plugin
cd ../npapi/mozilla
./build.sh
cp -R ./mac/build/Release/LittleShootPlugIn.plugin ~/Library/Internet\ Plug-Ins/
