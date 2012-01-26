#!/bin/bash

ARGS=1  # One arg to script expected.
E_BADARGS=65

function die()
{
echo $*
exit 1
}

if [ $# -ne "$ARGS" ]
then
        echo "Usage: release.bash version, as in release.bash 0.07"
        exit $E_BADARGS
fi


APP_VERSION="$1";

./quickCheckout.bash || die "Error checking out code"

echo "Building Windows installer"
./windowsInstall.bash $APP_VERSION || die "Error building Windows installer"
echo "Built Windows installer"


cd ../../client/site

echo "About to upload installer"
java -jar ../../../../../../usr/local/littleshoot/s3.jar -putp littleshoot LittleShootPlugin.exe
#mvn lastbamboo:s3-upload || die "Error uploading installers"

exit $?
