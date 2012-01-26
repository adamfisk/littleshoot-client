#!/usr/bin/env bash
#
# This script sets up LittleShoot to run as a background process controlled by launchd.
# 
# See: http://developer.apple.com/documentation/DeveloperTools/Conceptual/SoftwareDistribution/Install_Operations/Install_Operations.html#//apple_ref/doc/uid/10000145i-CH14-SW1

echo "Starting postflight script"
app=SmartFiles
# This should get swapped during the build.
postFlightVersion=0.999

mkdir ~/Library/Logs/SmartFiles
rm ~/Library/Logs/SmartFiles/installer.log
function log() {
  echo "`date`: $@" >> ~/Library/Logs/SmartFiles/installer.log
}

function appendToFile() {
  local escaped=`echo $3 | perl -MURI::Escape -lne 'print uri_escape($_)'`
  echo "Got escaped: "$escaped

  echo $2=$escaped >> $1
}

function reportErrors() {
  local tmpBase=`basename $0`
  local tmpDir=`mktemp -d /tmp/${tmpBase}.XXXXXX`

  local errFile="$tmpDir/errFile"

  #appendToFile $errFile "message" "$@"
  #appendToFile $errFile "host" "`uname -a`"
  #appendToFile $errFile "disk" "`df -h`"
  #appendToFile $errFile "lineNumber" "${BASH_LINENO[1]}"

  local message=`echo "$@" | perl -MURI::Escape -lne 'print uri_escape($_)'`
  local uName=`uname -a`
  local host=`echo "$uName" | perl -MURI::Escape -lne 'print uri_escape($_)'`
  local diskInfo="`df -h`"
  local disk=`echo "$diskInfo" | perl -MURI::Escape -lne 'print uri_escape($_)'`
  local bashLineNumber=${BASH_LINENO[1]}
  local lineNumber=`echo "$bashLineNumber" | perl -MURI::Escape -lne 'print uri_escape($_)'`
  local littleShootVersion=`echo "$postFlightVersion" | perl -MURI::Escape -lne 'print uri_escape($_)'`

  local groupInfo=`id -Gn`
  local group=`echo "$groupInfo" | perl -MURI::Escape -lne 'print uri_escape($_)'`  

  #curl -d @$errFile  "http://littleshootbugs.appspot.com/submitMacInstallerBug" || echo "Message (probably) not sent successfully." 2>&1
  curl  -d message=$message \
        -d host=$host \
        -d lineNumber=$lineNumber \
        -d group=$group \
        -d version=$littleShootVersion \
        -d user=$USER \
        "http://littleshootbugs.appspot.com/submitMacInstallerBug" || echo "Message (probably) not sent successfully." 2>&1
}

function die() {
  log "Failure: $@ ... Reporting errors"
  reportErrors "$@"  
  exit 1
}

function warn() {
  echo "Something fishy: $@ ... Reporting warning"
  reportErrors "$@"
}



#echo "Checking Java version"
# This is just a dummy check since the installer doesn't always seem to catch a failure.
#java -version | grep 1.4 && die "Cannot install LittleShoot when the Java version is 1.4"

log "Running as `whoami`"
log "User is $USER"

#OLD_APP_DIR=~/Applications
APP_DIR=/Applications
#test -d "$APP_DIR" || die "No app dir at $APP_DIR"
#log "Created app dir"

PLIST_DIR=~/Library/LaunchAgents

LS_HOME=~/.littleshoot

sharedDir=~/Shared
su $USER -c "mkdir $LS_HOME"
su $USER -c "mkdir $LS_HOME/stream"
su $USER -c "mkdir $sharedDir"

test -d /Library/Application\ Support/$app || su $USER -c "mkdir /Library/Application\ Support/$app" || mkdir /Library/Application\ Support/$app || warn "Could not create application suppor dir!!"

function removeLittleShoot() {
  echo "Listing loaded apps"
  launchctl list | grep littleshoot && warn "Warning: LittleShoot still running as `whoami`"
  su $USER -c "launchctl list | grep littleshoot" && warn "Warning: LittleShoot still running as $USER"
}

removeLittleShoot

function switchToJava6() {
  pushd "$APP_DIR"/$app.app/Contents
  perl -pi -e "s/1.5\+/1.6+/g" Info.plist || die "Could not change plist file"
  popd
}

# Use Java 1.6 if it's there.
/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home/bin/java -version && switchToJava6

log "Copying plist file from: $APP_DIR/SmartFilesHII.app/Contents/Resources/app/org.littleshoot.smartfiles.plist"
cp -f "$APP_DIR"/SmartFilesHII.app/Contents/Resources/app/org.littleshoot.smartfiles.plist $PLIST_DIR || die "Could not copy SmartFiles plist file"
#cp -f "$APP_DIR"/LittleShoot.app/Contents/Resources/Java/org.littleshoot.smartfiles.plist $PLIST_DIR || die "Could not copy plist file"
LAUNCHD_PLIST="$PLIST_DIR"/org.littleshoot.smartfiles.plist
chmod 644 $LAUNCHD_PLIST || die "Could not change permissions on plist"

#JAVA_FILE="$APP_DIR"/$app.app/Contents/MacOS/LittleShoot
#test -f "${JAVA_FILE}" || die "Java file does not exist at $JAVA_FILE"


# We need to run as non-root here because otherwise it completely screws up the LittleShoot instance locking scheme 
# that's based on lock files in the user's home directory
#su $USER -c "launchctl load -F $LAUNCHD_PLIST" || die "Could not load plist -- got `su $USER -c launchctl load -F $LAUNCHD_PLIST`"
log "Starting LittleShoot..." 
launchctl load -F $LAUNCHD_PLIST || die "Could not load plist"

log "Sleeping to give launchd a head start"
sleep 4
launchctl list | grep littleshoot || die "LittleShoot still not listed via launchctl" 

# We do this to make sure to update the modification time to tell Launch Services to reload the app.
touch /Applications/$app.app

log "Opening www.littleshoot.org"

log "End postflight script"
exit 0
