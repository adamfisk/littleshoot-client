echo "Building LittleShoot"

$env:LITTLSHOOT_ROOT="littleshoot"

# This is the Visual Studio command to run.
# This can also be "/build" to not clean
#$env:VS_COMMAND="/rebuild"

# For debug mode, change the following to "Debug"
#$env:BUILD_CONFIG="Release"


[Environment]::SetEnvironmentVariable("LS_VS_COMMAND", "/rebuild", "User")
[Environment]::SetEnvironmentVariable("LS_BUILD_CONFIG", "Debug", "User")

./buildBase.ps1

if (!$?)
{
    Write-Output "Could not build.  Exiting" 
    exit 1
}

# Note these won't necessarily go away until we restart.
[Environment]::SetEnvironmentVariable("LS_VS_COMMAND", $null, "User")
[Environment]::SetEnvironmentVariable("LS_BUILD_CONFIG", $null, "User")