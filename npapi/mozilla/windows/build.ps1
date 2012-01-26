#vcbuild.exe /rebuild ./littleshoot.vcproj/littleshoot.vcproj

$LS_BUILD_CONFIG=[Environment]::GetEnvironmentVariable("LS_BUILD_CONFIG", "User")
$LS_VS_COMMAND=[Environment]::GetEnvironmentVariable("LS_VS_COMMAND", "User")

if (!$LS_BUILD_CONFIG)
{
	$LS_BUILD_CONFIG="Debug"
}

if (!$LS_VS_COMMAND)
{
	$LS_VS_COMMAND="/rebuild"
}

#devenv littleshoot.vcproj/littleshoot.sln littleshoot.vcproj/littleshoot.vcproj $LS_VS_COMMAND $LS_BUILD_CONFIG
devenv littleshoot.vcproj/littleshoot.sln $LS_VS_COMMAND $LS_BUILD_CONFIG


if (!$?)
{
    Write-Output "Could not build NPAPI. Exiting." 
    exit 1
}

cp littleshoot.vcproj\$LS_BUILD_CONFIG\nplittleshoot.dll ../../../lib

if (!$?)
{
    Write-Output "Could not copy DLL from littleshoot.vcproj\$BUILD_CONFIG\nplittleshoot.dll. Exiting." 
    exit 1
}