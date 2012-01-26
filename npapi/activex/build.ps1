# afisk: Did not recognize config option, commenting out for now: vcbuild /r axlittleshootctrl.vcproj  Configuration="Release MinSize|Win32"
if ($args.count -ne 3)
{
    Write-Output "Usage: build.ps1 certPath certPass versionToken -- For Example: build.ps1 c:/mycert.p12 password '0,8,9,3'"
    exit 1
}

vcbuild /r axlittleshootctrl.vcproj
if (!$?)
{
    Write-Output "Could not build ActiveX control.  Exiting" 
    exit 1
}

# Just knock this one out since building the dll itself creates registry entries that can make testing hard.
rm ReleaseMinDependency/axlittleshoot.dll


pushd dist
./makecab.ps1 $args[0] $args[1] $args[2]
if (!$?)
{
    Write-Output "Could not make and sign cab file.  Exiting" 
    exit 1
}
Write-Output "Copying signed cab file to distribution directory"
cp axlittleshoot.cab ../../../server/appengine/static

if (!$?)
{
    Write-Output "Could not copy cab file.  Exiting" 
    exit 1
}

popd