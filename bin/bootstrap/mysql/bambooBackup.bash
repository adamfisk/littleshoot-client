#!/usr/bin/env bash

backup()
{
# We need to use the full path because this is typically run
# under cron where no path is set.
/usr/local/bin/dbBackup.bash bamboo bamboo bamboo
}
