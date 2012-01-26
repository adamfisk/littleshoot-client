
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
install4jc.exe -V
if (!$?) {
    Write-Output "Could not locate signtool. Not on path? We have it at: C:\Program Files (x86)\Microsoft SDKs\Windows\v7.0A\bin" 
    exit 1
}

bash --version
if (!$?) {
    Write-Output "Could not locate bash. Not on path? You can just put the Cygwin bin directory on your path." 
    exit 1
}

#mvn --version
#if (!$?) {
#    Write-Output "Could not locate Maven. Not on path?" 
#    exit 1
#}
which devenv.exe
if (!$?) {
    Write-Output "Could not locate Visual Studio. Not installed or not on path?" 
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

$bashCommands="cd littleshoot/trunk/install/bin;./windowsInstallInstall4J.bash $version"
echo "Running bash $bashCommands"
bash --login -i -c $bashCommands

pushd install/install4j/generated_win_files

echo "Signing all necessary files..."
./signAll.ps1 $certPath $certPass
if (!$?) {
    Write-Output "Could not sign files. Exiting." 
    exit 1
}

# Call install4j to create the new version.
cd ..
install4jc.exe -D "appName=HII,shortName=hii,publisher=Human Interface Initiative,publisherUrl=http://www.hiinterface.org" -m windows -r $version .\LittleShoot.install4j

#mv hii_windows_0_1.exe LittleShootPlugin.exe
signtool sign /f $certPath /p $certPass /v /t http://timestamp.comodoca.com/authenticode ./LittleShootPlugin.exe
if (!$?) {
    Write-Output "Could not sign installer. Exiting." 
    exit 1
}

cp LittleShootPlugin.exe ../../../..
if (!$?) {
    Write-Output "Could not copy installer. Exiting." 
    exit 1
}
#java -jar c:/cygwin/usr/local/aws-0.3/s3.jar -putp littleshoot LittleShootPlugin.exe

popd
popd

# Note these won't necessarily go away until we restart.
[Environment]::SetEnvironmentVariable("LS_VS_COMMAND", $null, "User")
[Environment]::SetEnvironmentVariable("LS_BUILD_CONFIG", $null, "User")

