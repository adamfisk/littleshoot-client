$jars = dir *.jar
foreach ($jar in $jars)
{
    echo Packing $jar.Name
    $f=$jar.Name.ToString()
    $fPack=$f+".pack"
    pack200.exe --modification-time=latest --deflate-hint="false" --no-gzip $fPack $f
    if (!$?)
    {
        Write-Output "Could not pack jar $f.  Exiting" 
        exit 1
    }
}
