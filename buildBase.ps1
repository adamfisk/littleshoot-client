echo "Building LittleShoot Base"

function download([string]$url, [string]$path) {
	$wc = New-Object net.webclient
	echo "Downloading file from $url"
	$wc.DownloadFile("$url", "$path")
	if (!$?) {
		echo "Could not download $url!!"
		exit 1
	}
}

if (!(Test-Path -path .\lib\nplittleshoot.dll)) {
	echo "Downloading nplittleshoot.dll"
	$wc = New-Object net.webclient
		
	$url = "http://littleshoot.s3.amazonaws.com/nplittleshoot.dll"
	$path = "c:/nplittleshoot.dll"
	echo "Downloading file from $url"
	$wc.DownloadFile($url, $path)
	
	echo "Moving file"
	mv $path .\lib\nplittleshoot.dll
	if (!$?) {
		echo "Could not move dll file!!"
		exit 1
	}
}
Else {
    echo "Not downloading nplittleshoot.dll!"
}

echo "Building boost"
pushd boost
if (!$?) {
    Write-Output "Could not move to boost dir.  Exiting" 
    exit 1
}

.\build.ps1
if (!$?) {
    Write-Output "Could not build boost.  Exiting" 
    exit 1
}
popd

pushd libtorrent
echo "Updating and Building LibTorrent"
.\build.ps1
if (!$?) {
    Write-Output "Could not build LibTorrent.  Exiting" 
    exit 1
}
popd

if (!(Test-Path -path .\npapi\mozilla\sun-java\stubs )) { 
	echo "Running script to extract dependencies"
	bash --login -i -c 'cd $LITTLSHOOT_ROOT/trunk/npapi/mozilla;./depends.sh'
	if (!$?) {
		Write-Output "Could not expand dependencies. Bash on path?" 
		exit 1
	}
}

#pushd npapi/mozilla/windows
#echo "Building NPAPI"
#.\build.ps1
#if (!$?)
#{
#    Write-Output "Could not build NPAPI.  Exiting" 
#    exit 1
#}
#popd



echo "Building Maven plugin"
pushd mojo/lastbamboo-maven-plugin
cmd /c "mvn install"
popd

cmd /c "mvn install -D maven.test.skip=true"
if (!$?) {
    Write-Output "Could not build Java.  Exiting" 
    exit 1
}


pushd jni/jlibtorrent
.\build.ps1
if (!$?) {
    Write-Output "Could not build JNI for LT. Exiting." 
    exit 1
}
popd