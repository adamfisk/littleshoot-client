#!/usr/bin/env bash
perl -pi -e s/createTables=false/createTables=true/g ../server/site/src/main/webapp/WEB-INF/shoot.properties
