#!/bin/bash
#
# This is a quick script to speed up development that kills an installed JIRA 
# version.
#
./jira/bin/shutdown.sh
rm -rf atlassian-jira*;rm jira;rm -rf mysql-connector-java-5.1.5/;rm mysqlj.tgz;rm jira.sql
