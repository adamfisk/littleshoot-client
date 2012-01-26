#!/usr/bin/env bash

die()
{
echo "Script encountered an error at `date`"
echo "*************************" 
echo $*
echo "*************************" 
exit 1
}

echo ""
echo "Running backup at `date` script as user: $USER in `pwd`"

backupAll()
{
local backupsDir=/usr/local/bin/backups
for i in $( ls $backupsDir ); do
    echo Running backup script: $i
    source $backupsDir/$i

    # Each script in the backups dir MUST
    # "implement the backup interface".
    # We run the backup function on each 
    # one in turn.
    backup || echo "Error running backup script."
done
}
backupAll || die "Error running one or more backups!!"

# Send the backups
echo "About to upload backups..."
/usr/local/bin/aws -v -puta littleshoot_backups /mnt/data-store || die "Error uploading backups!!"

echo "All backups completed successfully!"
echo ""
exit 0
