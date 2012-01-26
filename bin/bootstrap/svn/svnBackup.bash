#!/usr/bin/env bash

die()
{
  echo $*
  echo "Exiting at `date`"
  exit 1
}

backup()
{
echo "Running dump"
local DATE=`date +%d`
local dumpName=svn.dump.${DATE}
local backupDir=/mnt/data-store
rm $dumpName
rm $dumpName.gz

test -d /mnt/srv/svn/littleshoot || die "No svn directory"
svnadmin dump /mnt/srv/svn/littleshoot/ > $dumpName || die "Could not create dump file"

echo "gzipping $dumpName"
gzip $dumpName || die "Could not gzip file."
rm $backupDir/$dumpName.gz

echo "Moving file to backups dir"
mv $dumpName.gz $backupDir || die "Could not move dump file to $backupDir"
echo "Finished taking svn backup at `date`"
echo ""
}
