#/bin/bash
echo "WARNING: THIS SHOULD ONLY BE USED DURING TESTING"
sudo rm -rf /Users/Shared/LittleShoot

if ! ./quickCheckout.bash
then
    echo "Could not check out"
    exit 1
fi

./osxInstall.bash 0.1111
