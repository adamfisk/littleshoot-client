#!/bin/bash

if [ $# -ne "2" ]
then
  echo "You need to include a user name and a host such as 'www littleshoot.org'"
  exit 1
fi

userName=$1
host=$2
keyPath=/home/$userName/.ssh/authorized_keys
commandToRun="cat - >> /home/$userName/.ssh/authorized_keys"

echo "Running command: $commandToRun"
cat ~/.ssh/id_rsa.pub | ssh -i ~/.ec2/id_rsa-gsg-keypair root@$host $commandToRun
