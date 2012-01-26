#!/usr/bin/env bash

# This file backs up a database to a backup dir
# that will be synced with s3

if [ $# != "3" ]; then
    echo "Usage:"
    echo "      $0 [database] [username] [password]"
    exit 1
fi

echo "Starting backup at `date`"

backup()
{
local DATE=`date +%d`
local DB=$1
local USER=$2
local PASS=$3

local BACKUPDIR=/mnt/data-store

# create database backup
mysqldump --add-drop-table \
          -h localhost \
          --user=${USER} \
          --password=${PASS} \
          ${DB} \
          | bzip2 -c > ${BACKUPDIR}/${DB}_${DATE}.sql.bz2
}

backup $*
echo "Finished taking backup at `date`"
echo ""
