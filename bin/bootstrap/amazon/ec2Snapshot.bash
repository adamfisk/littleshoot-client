#!/usr/bin/env bash

die()
{
  echo $*
  exit 1
}

echo "Creating snapshot"
set EC2_HOME=/home/ec2
/home/ec2/bin/ec2-create-snapshot vol-c056b3a9 || die " Could not create snapshot"
