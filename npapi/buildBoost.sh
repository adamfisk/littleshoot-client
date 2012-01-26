#!/bin/sh

#
# Check if we have the boost libs.
#
if [ -d "boost" ]
then
echo "Found boost libs locally..."

	if [ -d "boost/bin.v2" ]
		then
	echo "Detected bin.v2, not building boost libs..."
	exit 0
	fi
fi

#if [ ! -f "boost_1_37_0.tar.gz" ]
#	then

#echo "Downloading boost libs..."

#curl -O http://internap.dl.sourceforge.net/sourceforge/boost/boost_1_37_0.tar.gz

#fi

echo "Extracting dependencies..."

test -d boost || tar -xvf boost.tgz 

#mv boost_1_37_0 boost

echo "Complete!"

echo "Compliing boost..."

cd boost

function buildMac()
{
echo "Building Boost for Mac"
bjam debug release toolset=darwin link=static architecture=combined macosx-version-min=10.4 --macosx-version=10.4 threading=multi --layout=system --without-mpi --without-wave --without-test --without-serialization --without-graph --without-graph --without-signals --without-python --without-iostreams --without-math
}

function buildWin()
{
echo "Building Boost for Windows"
bjam debug release toolset=msvc link=static runtime-link=static threading=multi --layout=system --without-mpi --without-wave --without-test --without-serialization --without-graph --without-signals --without-python --without-iostreams --without-math
}

uname -a | grep CYGWIN && buildWin
uname -a | grep Darwin && buildMac
echo "Complete!"

