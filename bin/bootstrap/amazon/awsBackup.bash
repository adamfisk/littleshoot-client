#!/usr/bin/env bash

die()
{
  echo $*
  exit 1
}

function run
{
  cd
  local lsSvn=http://svn.littleshoot.org/svn/littleshoot
  #local bootstrapUrl=$lsSvn/trunk/bin/bootstrap
  local bootstrapUrl=http://littleshoot_config.s3.amazonaws.com
  test -d /mnt/data-store || mkdir /mnt/data-store || die "Could not make directory!!"
  #curl -o aws.tgz http://littleshoot.s3.amazonaws.com/aws-0.3.tgz
  #tar xzvf aws.tgz
  #pushd aws-0.3
  #./install.sh
  #popd

#  curl -o dbBackup.bash $bootstrapUrl/mysql/dbBackup.bash
#  curl -o bugsBackup.bash $bootstrapUrl/mysql/bugsBackup.bash
#  curl -o bambooBackup.bash $bootstrapUrl/mysql/bambooBackup.bash

# Note that JIRA backup is configured within JIRA itself
  curl -o svnBackup.bash $bootstrapUrl/svnBackup.bash
  curl -o backup.bash $bootstrapUrl/backup.bash
  curl -o ec2Snapshot.bash $bootstrapUrl/ec2Snapshot.bash
  chmod +x *.bash

  test -d /usr/local/bin/backups || mkdir /usr/local/bin/backups || die "Could not create backup scripts dir!!"
#  mv dbBackup.bash /usr/local/bin || die "Could not copy script!"
#  mv bugsBackup.bash /usr/local/bin/backups || die "Could not copy script!"
#  mv bambooBackup.bash /usr/local/bin/backups || die "Could not copy script!"
  mv svnBackup.bash /usr/local/bin/backups || die "Could not copy script!"
  mv backup.bash /usr/local/bin || die "Could not copy script!"
  mv ec2Snapshot.bash /usr/local/bin || die "Could not copy script!"

  echo "Creating the backups bucket..."
  # Should already be there, but just in case...
  aws -mkdir littleshoot_backups

  echo "Adding backup script to crontab..."

  # Grab the current crontab settings.
  crontab -u $USER -l > crontab.file.$USER
  # Append the new -- backing up every ten minutes...
  cat <<EOL >> crontab.file.$USER
*/10 * * * *   /usr/local/bin/backup.bash >> /tmp/backups.log 2>&1
5 0 * * *    /usr/local/bin/ec2Snapshot.bash >> /tmp/snapshots.log 2>&1
  
EOL
  # And update
  crontab -u $USER crontab.file.$USER
}
run
