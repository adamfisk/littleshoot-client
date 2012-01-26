#!/usr/bin/env bash 

die()
{
  echo $*
  if [ ! -z "$ami" ]
  then
      echo "Automatically terminating AMI: $ami"
      ec2-terminate-instances $ami
  fi

  exit 1
}

keyPair=~/.ec2/id_rsa-gsg-keypair

if [ ! -e $keyPair ]
then
  echo "No keypair at $keyPair.  Please enter the path to your key pair."
  read keyPair
  if [ ! -e $keyPair ]
  then
      die "Sorry, no pair at $keyPair either.  Exiting"
  fi
fi

test $# -lt 1 && \
echo "Usage $0 your_ami_id setupScript(optional)" && exit 1

echo "All args: $@"
echo "Telling EC2 to run an instance of AMI $1"
ami=$1
scriptToCopy=$2

# We have to shift past both the ami and the script to copy.
shift
shift
# We shift through the arguments here to allow callers to pass whatever
# params they want to run instances, such as the group.
extraArgs=$@
echo "Running extra arguments: $extraArgs"
ec2-run-instances $ami -k gsg-keypair $extraArgs || die "Could not run instance"

count=0
while [  $count -lt 100 ]; do
  echo "Running EC2 check # $count"
  status=`ec2-describe-instances | tail -n 1`
  echo "Status is: $status" 
  ami=`echo $status | cut -d" " -f2` 

  if ! echo $status | grep "pending"
  then
      echo "System appears to be running on"
      host=`echo $status | cut -d" " -f4`
      echo $host
      echo $status | grep "running" || die "Does not appear to be running, although not pending either.  Timeout?  Response change?"
      echo "Waiting a second to allow sshd to start..."
      sleep 30
      break
  fi
  sleep 8
  let count=count+1 
done

echo -en "\007"
echo -en "\007"

if echo $status | grep "pending"
then
    echo "System still appears to be pending.  Giving up."
    exit 1
fi

if [ ! -z "$scriptToCopy" ]
then
  test -e $scriptToCopy || die "Not a file: $scriptToCopy" 
  echo "Copying setup file $scriptToCopy"
  echo "Running: scp -i $keyPair $scriptToCopy root@$host:/root"
  scp -i $keyPair $scriptToCopy root@$host:/root || die "Could not copy script: $2 in $0"
fi

echo "Logging in to EC2"
ssh -i $keyPair root@$host

echo "Copying key for users from $0..."
#./putKey.sh www $host || die "Could not put key.  Tried: ./putKey.sh www $host"
#./putKey.sh tomcat $host || die "Could not put key.  Tried: ./putKey.sh tomcat $host"
#./putKey.sh shoot $host || die "Could not put key.  Tried: ./putKey.sh shoot $host"

echo "Hopefully everything went OK.  Your new AMI ID is '$ami', and your host is running at $host" 

