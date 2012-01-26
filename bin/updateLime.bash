#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

version=prod-4-18-8-java15
curDir=`pwd`
limeRepo=~/.m2/repository/org/limewire
#rm -rf $limeRepo/*
limeHome=~/code/limewire/$version/limewire
cd $limeHome
rm diffs.txt
cvs diff > diffs.txt 
cp diffs.txt $curDir/../etc/
diffs=$curDir/../etc/diffs.txt
svn add $diffs
svn ci -m "updated diffs" $diffs || die "Could not commit diffs" 
mvn clean install -Dmaven.test.skip=true || die "Could not build LW"
rsync -e 'ssh -ax -i /Users/adamfisk/.ec2/id_rsa-gsg-keypair' -avz $limeRepo root@dev.littleshoot.org:/var/maven/org/

# The following is what needs to happen on the server to update the maven repo
#tar czvf maven_our_repo.tgz *
#aws -putp 04G2SEBTMTS8S59X1SR2-maven maven_our_repo.tgz

exit 0
