#!/usr/bin/env bash

function die
{
  echo $*
  exit 1
}

cd common
projects=("p2p" "xmpp" "sip" "ice" "turn" "util")
for p in ${projects[@]}
do
    pushd $p
    mvn clean
    mvn deploy -Dmaven.test.skip=true || die "Could not deploy $p"
    popd
done

pushd p2p
mvn clean
mvn deploy -Dmaven.test.skip=true || die "Could not deploy p2p"
popd

