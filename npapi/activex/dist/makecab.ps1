if ($args.count -ne 3)
{
    Write-Output "Usage: makecab.ps1 certPath certPass versionToken -- For Example: makecab.ps1 c:/mycert.p12 password '0,8,9,3'"
    exit 1
}

$certPath=$args[0]
$certPass=$args[1]
$versionToken=$args[2]

mv ../ReleaseMinSize/axlittleshoot.dll .
if (!$?)
{
    Write-Output "Could not copy dll.  Exiting" 
    exit 1
}

signtool sign /f $certPath /p $certPass /v /t http://timestamp.comodoca.com/authenticode ./axlittleshoot.dll
if (!$?)
{
    Write-Output "Could not sign ActiveX control.  Exiting" 
    exit 1
}

# The lsloader is committed as a signed exe
#signtool sign /f $certPath /p $certPass /v /t http://timestamp.comodoca.com/authenticode ./lsloader.exe
#if (!$?)
#{
#    Write-Output "Could not sign stub installer.  Exiting" 
#    exit 1
#}

# Set the version number.
(Get-Content ./axlittleshoot.inf) | Foreach-Object {$_ -replace "FileVersionToken", "$versionToken"} | Set-Content ./axlittleshoot.inf
if (!$?)
{
    Write-Output "Could not write version.  Exiting" 
    exit 1
}

cabarc -s 6144 n ./axlittleshoot.cab ./axlittleshoot.dll ./axlittleshoot.inf ./lsloader.exe
if (!$?)
{
    Write-Output "Could not create cab file.  Exiting" 
    exit 1
}

signtool sign /f $certPath /p $certPass /v /t http://timestamp.comodoca.com/authenticode ./axlittleshoot.cab 

if (!$?)
{
    Write-Output "Could not sign cab file.  Exiting" 
    exit 1
}

rm axlittleshoot.dll
svn revert axlittleshoot.inf