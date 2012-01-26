#!/usr/bin/env bash
perl -pi -e s/createTables=true/createTables=false/g ../server/site/src/main/webapp/WEB-INF/shoot.properties
