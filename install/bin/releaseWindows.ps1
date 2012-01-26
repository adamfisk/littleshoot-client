
if ($args.count -ne 4) {
    Write-Output "Usage: releaseWindows.ps1 version certPath certPass, as in releaseWindows.ps1 0.8984 c:/somecert.p12 pwd isPro"
    exit 1
}
$version=$args[0]
$certPath=$args[1]
$certPass=$args[2]
$isPro=$args[3]
echo "Got pro arg: $isPro"

# Make sure we have the tools we need up front
signtool.exe  /?
if (!$?) {
    Write-Output "Could not locate signtool. Not on path? We have it at: C:\Program Files (x86)\Microsoft SDKs\Windows\v7.0A\bin" 
    exit 1
}
bash --version
if (!$?) {
    Write-Output "Could not locate bash. Not on path? You can just put the Cygwin bin directory on your path." 
    exit 1
}
#cvs --version
#if (!$?)
#{
#    Write-Output "Could not locate cvs. Not on path? You can just put the Cygwin bin directory on your path." 
#    exit 1
#}
#mvn --version
#if (!$?)
#{
#    Write-Output "Could not locate Maven. Not on path?" 
#    exit 1
#}
which devenv.exe
if (!$?) {
    Write-Output "Could not locate Visual Studio. Not installed or not on path?" 
    exit 1
}
which jsmoothgen.exe
if (!$?) {
    Write-Output "Could not locate JSmooth. Not installed or not on path?" 
    exit 1
}
which makensis.exe
if (!$?) {
    Write-Output "Could not locate NSIS. Not installed or not on path?" 
    exit 1
}

$env:LITTLSHOOT_ROOT="temp_install_dir"

# This is the Visual Studio command to run.
# This can also be "/build" to not clean
#$env:VS_COMMAND="/rebuild"

# For debug mode, change the following to "Debug", "Release" for release.
#$env:BUILD_CONFIG="Debug"
[Environment]::SetEnvironmentVariable("LS_VS_COMMAND", "/rebuild", "User")
[Environment]::SetEnvironmentVariable("LS_BUILD_CONFIG", "Release", "User")


bash --login -i -c 'echo `pwd`'
#bash --login -i -c 'cd littleshoot/trunk/install/bin;./quickCheckout.bash $isPro'
$fullCommand='cd littleshoot/trunk/install/bin;./quickCheckout.bash ' + $isPro
echo "Full command: $fullCommand"
bash --login -i -c $fullCommand
if (!$?) {
    Write-Output "Could not perform checkout.  Exiting" 
    exit 1
}

#echo "Performed checkout. Moving to temp trunk."
pushd ../../../../temp_install_dir/trunk
#cd ../..

#echo "Updating"
#svn up

echo "Building all code..."
./buildBase.ps1

if (!$?) {
    Write-Output "Could not build release.  Exiting" 
    exit 1
}

#pushd lib
#signtool sign /f $certPath /p $certPass /v /t http://timestamp.comodoca.com/authenticode ./nplittleshoot.dll
#popd

$bashCommands="cd littleshoot/trunk/install/bin;./windowsInstall.bash $version"
echo "Running bash $bashCommands"
bash --login -i -c $bashCommands

pushd install/win

echo "Packing files"
./packAll.ps1
if (!$?) {
    Write-Output "Could not pack jars.  Exiting" 
    exit 1
}

#rm *.jar
#if (!$?)
#{
#    Write-Output "Could not remove jars.  Exiting" 
#    exit 1
#}

echo "Signing all necessary files..."
./signAll.ps1 $certPath $certPass
if (!$?) {
    Write-Output "Could not sign files. Exiting." 
    exit 1
}

echo "Making NSIS..."
# Note this file is generated and is not in version control.
./makensis.ps1
if (!$?) {
    Write-Output "Could not build NSIS installer. Exiting." 
    exit 1
}
popd

pushd install/win
signtool sign /f $certPath /p $certPass /v /t http://timestamp.comodoca.com/authenticode ./LittleShootPlugin.exe
popd

cp ./install/win/LittleShootPlugin.exe ../../littleshoot/trunk/client/site
if (!$?) {
    Write-Output "Could not copy installer. Exiting." 
    exit 1
}
#java -jar c:/cygwin/usr/local/aws-0.3/s3.jar -putp littleshoot LittleShootPlugin.exe

popd

# Note these won't necessarily go away until we restart.
[Environment]::SetEnvironmentVariable("LS_VS_COMMAND", $null, "User")
[Environment]::SetEnvironmentVariable("LS_BUILD_CONFIG", $null, "User")

