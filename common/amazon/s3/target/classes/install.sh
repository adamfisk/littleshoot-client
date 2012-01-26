#!/usr/bin/env sh

die()
{
  echo $*
  exit 1
}

awsProps=~/.littleshoot/littleshoot.properties
createAwsFile()
{
    echo "Please enter your AWS access key ID"
    read YOUR_AWS_ID
    echo "Please enter your AWS access key"
    read YOUR_AWS_KEY

    if [ -d ~/.littleshoot ]; then
        echo "~/.littleshoot exists..."
    else
        mkdir ~/.littleshoot || die "Could not make directory ~/.littleshoot.  Are you sure you have the necessary permissions?"
    fi
    echo "accessKeyId=$YOUR_AWS_ID" >> $awsProps || die "Could not write access key ID" 
    echo "accessKey=$YOUR_AWS_KEY" >> $awsProps || die "Could not write access key"
}

if [ -e "$awsProps" ]
then
    echo "LittleShoot props configured..."
else
    createAwsFile
fi

installDir=/usr/local/littleshoot
if [ -d $installDir ]; then
    echo "Install dir exists.  Overwriting files."
else
    echo "Making install dir.  You may be prompted for your root password for sudo."
    sudo mkdir $installDir || die "Could not make install dir"
fi

echo "Copying files to $installDir.  You may be prompted for your root password for sudo."
sudo cp * $installDir/

function link
{
for x
do 
    cd /usr/local/bin
    echo "Creating link: $x"
    if [ -L $x ]; then
        echo "Link exists.  Overwriting."
        sudo rm $x
    fi
    
    sudo ln -s $installDir/$x $x || die "Could not link file $x."
    newOwner=$USER
    sudo chown $newOwner $installDir/$x
    sudo chown -h $newOwner $x
done
}

pushd $installDir

echo "Linking files in /usr/local/bin"
link aws

popd

