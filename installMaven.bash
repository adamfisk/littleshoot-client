#!/usr/bin/env bash

function die() {
  echo $*
  exit 1
}

function installMaven() {
    cd
    #curl -L -O "http://www.ibiblio.org/pub/mirrors/apache//maven/binaries/apache-maven-2.2.1-bin.tar.gz"
    wget "http://www.ibiblio.org/pub/mirrors/apache//maven/binaries/apache-maven-2.2.1-bin.tar.gz"
    tar xzvf apache-maven-2.2.1-bin.tar.gz
    local cur=`pwd`
    echo "export PATH=$cur/apache-maven-2.2.1/bin:\$PATH" >> ~/.bashrc    
    source ~/.bashrc
    mvn -version || die "ERROR INSTALLING MAVEN!!"
    echo "Maven should be successfully installed"
}

which mvn || installMaven
