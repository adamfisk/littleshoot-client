#!/bin/sh

echo "Extracting dependencies..."
test -d nsprpub || tar -xf nsprpub.tgz
test -d sun-java || tar -xf sun-java.tgz
test -d plugin || tar -xf plugin.tgz
echo "Finished Extracting Dependencies!"
