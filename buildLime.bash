#!/usr/bin/env bash

#
# cvs -d :pserver:guest@cvs.limewire.org:/cvs checkout -P -r prod-5-5-0-java16 limewire
# cvs -d :pserver:guest@cvs.limewire.org:/cvs checkout -P -r prod-5-4-8-java16 limewire
#

function die()
{
  echo $*
  exit 1
}

ARGS=1  # One arg to script expected.

if [ $# -ne "$ARGS" ]
then
    echo "true or false arg required for pro or free"
    exit 1
fi
#ROOT_DIR=`pwd`/../../buildLime
ROOT_DIR=`pwd`/../../code/limewire/
echo "Root dir: $ROOT_DIR"
test -d $ROOT_DIR || mkdir $ROOT_DIR || die "Could not create root dir"
LIME_BASE_DIR=limewire-prod-5-4-8-java16-BACKUP
ETC_DIR=`pwd`/etc
echo "etc dir is: $ETC_DIR"

isPro=$1

# This is handled earlier to avoid the prompt holding up install scripts.
#export CVSROOT=:pserver:guest@cvs.limewire.org:/cvs || die "Could not set CVS root"
#cvs login 

function revertCopy()
{
echo "Reverting existing copy"
pushd $LIME_BASE_DIR || die "Could not cd??"
#cvs -d:pserver:guest@cvs.limewire.org:/cvs -q update -dCP -r prod-5-4-8-java16 || die "Could not create clean copy"
cvs update -P guest -C -r prod-5-4-8-java16 || die "Could not create clean copy"
popd
}

cd $ROOT_DIR || die "Could not change to root dir"
#test -d $LIME_BASE_DIR && revertCopy
#test -d $LIME_BASE_DIR || cvs -d:pserver:guest@cvs.limewire.org:/cvs -q checkout -P -r prod-5-4-8-java16 limewire || die "Could not checkout LW"

rm -rf lw_temp
mkdir lw_temp
cp lw.tgz lw_temp/ || die "Could not copy tgz"
cd lw_temp || die "Could not change to temp build dir"
tar xzvf lw.tgz || die "Could not expand tgz"

cd $LIME_BASE_DIR || die "Could not cd into lime dir??"

cp $ETC_DIR/patchfile.txt . || die "Could not copy patch file"
cp $ETC_DIR/hostcache_patch.txt . || die "Could not copy patch file"


# This is a separate LittleShoot file
cp $ETC_DIR/DownloadListener.java components/gnutella-core/src/main/java/com/limegroup/gnutella/downloader/ || die "Could not copy DownloadListener.jar"

#patch -N -p0 < patchfile.txt || die "Could not apply patch file"
# We just apply it ignoring the return value because -N fails if it doesn't apply anything, but we want to continue.
echo "Applying patch file..."
patch -N -p0 < patchfile.txt
echo "Applying host cache patch file" 
patch -N -p0 < hostcache_patch.txt 
echo "Patch files applied..."

# This is already patched.

#pushd components/gnutella-core/src/main/java/com/limegroup/gnutella/util || die "Could not change dirs"
#perl -pi -e "s/\@version\@/5.4.8/g" LimeWireUtils.java || die "Could not swap version"
#perl -pi -e "s/_isPro = false/_isPro = $isPro/g" LimeWireUtils.java
#perl -pi -e "s/_isPro = true/_isPro = $isPro/g" LimeWireUtils.java
#popd

echo "Building LimeWire..."
ant clean aggregate.jar || die "Could not build LW!!"

pushd components/gnutella-core/build/lib/runtime/jar || die "No runtime build dir?"

# Ivy does some automated naming of jars. We need to strip it to avoid having to change all our maven 
# dependencies every time we do this build.
year=`date +%Y`
for f in *-$year*
    do
    echo "Processing $f"
    filename=${f%-$year*}
    echo "Stripped file name: $filename"
    mv $f $filename.jar
done

for f in *.jar; 
    do 
    echo "Processing $f file.."; 
    #baseName=`echo "$f" | cut -d'.' -f1`

    # See: http://stackoverflow.com/questions/965053/extract-filename-and-extension-in-bash 
    baseName=${f%.*}
    #snapshot=$baseName-SNAPSHOT.jar 
    #mv $f $snapshot
    mvn deploy:deploy-file -DgroupId=org.limewire -DartifactId=$baseName -Dversion=5.4.8-SNAPSHOT -Dfile=$f -Dpackaging=jar -DgeneratePom=true -Durl=scpexe://dev.littleshoot.org/var/maven -DrepositoryId=littleshoot || die "Could not deploy file"
done

popd

cd components/gnutella-core/dist/main || die "Could not change to main dir??"
mvn deploy:deploy-file -DgroupId=org.limewire -DartifactId=gnutella-core -Dversion=5.4.8-SNAPSHOT -Dfile=gnutella-core.jar -Dpackaging=jar -DgeneratePom=true -Durl=scpexe://dev.littleshoot.org/var/maven -DrepositoryId=littleshoot || die "Could not deploy file"


#
# TODO: publish dependencies into mvn, for the rest of littleshoot to use.
# There are two ways to do this:
# 1) Publish ./LimeWire.jar, which is an aggregate compilation of gnutella-core + a bunch of other components
#    (some of which are unnecessary), and all the dependencies (see below for dependencies).
# 2) Publish *every* dependency - not the aggregate jar.
# All dependencies will be stored @ components/gnutella-core/build/lib/runtime/jar after ant completes.
# If you want to publish every dependency individually (option #2), just publish every file
# in that folder (ivy may somehow help with this) AND components/gnutella-core/dist/main/gnutella-core.jar
# If you want to publish the aggregate jar + dependencies (option #1), you can publish ./LimeWire.jar
# and all the files in components/gnutella-core/build/lib/runtime/jar EXCEPT the ones whose name matches
# the pattern "[name of a dir in components/]-[current datetime]"
#
# If you want a quick glance at the version of dependencies LimeWire requires, look at:
#    components/common/dependencies.props
# All the required jars are also contained in:
#    lib/jars
# Some native dependencies will be required also, and are in:
#    lib/native/
