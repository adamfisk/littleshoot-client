#!/usr/bin/env bash

rm -rf /Applications/LittleShoot.app
rm -rf /Library/Internet\ Plug-Ins/LittleShootPlugIn.plugin
rm -rf /Library/Receipts/littleshoot*
rm -rf /Library/Application\ Support/LittleShoot
launchctl unload /Library/LaunchAgents/org.littleshoot.littleshoot.plist
sudo launchctl unload /Library/LaunchAgents/org.littleshoot.littleshoot.plist
sudo rm /Library/LaunchAgents/org.littleshoot.littleshoot.plist 
