#!/usr/bin/env bash
function die()
{
  echo $*
  exit 1
}

function killPLists()
{
  # Not used any more, but make sure we get rid of any old ones.
  local plist1=~/Library/LaunchAgents/org.lastbamboo.littleshoot.plist
  launchctl unload $plist1
  rm -f $plist1
 
  local plist2=/Library/LaunchAgents/org.lastbamboo.littleshoot.plist
  launchctl unload $plist2
  rm -f $plist2
 
  local plist3=~/.littleshoot/org.lastbamboo.littleshoot.plist
  launchctl unload $plist3
  rm -f $plist3
}

function killAppDirs()
{
  APP_DIR=/Applications/LittleShoot
  rm -rf "$APP_DIR" 
  OLD_APP_DIR=~/Applications
  rm -rf "$OLD_APP_DIR"/LittleShoot*
}

function killPlugin()
{
  PLUGIN_PATH=/Library/Internet\ Plug-Ins
  rm -rf "$PLUGIN_PATH"/LittleShootPlugIn.plugin

}

function killReceipts()
{
  rm -rf /Library/Receipts/littleshoot*
  rm -rf ~/Library/Receipts/littleshoot*
}


killPLists()
killAppDirs()
killPlugin()
killReceipts()
