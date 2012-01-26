$certPath=$args[0]
$certPass=$args[1]

#pushd install/win
signtool sign /f $certPath /p $certPass /v /t http://timestamp.comodoca.com/authenticode LittleShoot.exe
if (!$?)
{
    Write-Output "Could not sign.  Exiting" 
    exit 1
}
signtool sign /f $certPath /p $certPass /v /t http://timestamp.comodoca.com/authenticode jnltorrent.dll
if (!$?)
{
    Write-Output "Could not sign.  Exiting" 
    exit 1
}
signtool sign /f $certPath /p $certPass /v /t http://timestamp.comodoca.com/authenticode nplittleshoot.dll
if (!$?)
{
    Write-Output "Could not sign.  Exiting" 
    exit 1
}

#signtool sign /f $certPath /p $certPass /v /t http://timestamp.comodoca.com/authenticode noConsole.exe
#if (!$?)
#{
#    Write-Output "Could not sign.  Exiting" 
#    exit 1
#}


#dir *.dll | signtool sign /f $certPath /p $certPass /v /t http://timestamp.comodoca.com/authenticode
#popd