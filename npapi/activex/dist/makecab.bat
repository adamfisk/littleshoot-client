

copy ..\ReleaseMinSize\axlittleshoot.dll ./axlittleshoot.dll

cabarc -s 6144 n ./axlittleshoot.cab ./axlittleshoot.dll ./axlittleshoot_local.inf ./lsLoader.exe

makecert -sv "cert.pvk" -n "CN=LastBamboo" cert.cer

cert2spc cert.cer cert.spc

pvk2pfx -pvk cert.pvk -pi Pass1 -spc cert.spc -pfx cert.pfx -po Pass2 -f



REM signtool -v cert.pvk -spc mycert.spc axlittleshoot.cab /t http://timestamp.verisign.com/scripts/timstamp.dll



signtool sign /f ./cert.pfx /p Pass2 /v ./axlittleshoot.cab 
REM /t http://timestamp.verisign.com/scripts/timestamp.dll