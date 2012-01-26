#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

echo "Updating to latest code..."
export MAVEN_OPTS="-Xmx512M -XX:MaxPermSize=512M"

svn up
test -f lib/jnltorrent.dll || wget http://cloudfront.littleshoot.org/jnltorrent.dll
test -f jnltorrent.dll && mv ./jnltorrent.dll lib/ 

pushd mojo
mvn install -Dmaven.test.skip=true || die "Could not build mojo"
popd
mvn clean
mvn install -Dmaven.test.skip=true || die "Could not build LS"

cd client/site
mvn lastbamboo:runscript || die "Could not create runscript"
rm log.txt
./run.sh
