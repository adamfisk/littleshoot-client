#/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

ARGS=2  # One arg to script expected.

if [ $# -ne "$ARGS" ]
then
        echo "Usage: release.bash version true|false, as in 'release.bash 0.07 true' for releasing pro"
        die "Unexpected command"
fi

APP_VERSION="$1";
PRO=$2;
sudo rm -rf /Users/Shared/LittleShoot

./quickCheckout.bash $PRO || die "Could not check out"

#./osxInstall.bash $APP_VERSION

#svn copy https://svn.littleshoot.org/svn/littleshoot/trunk https://svn.littleshoot.org/svn/littleshoot/tags/littleshoot-$APP_VERSION-release-osx -m "release tag"
