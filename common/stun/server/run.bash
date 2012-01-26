#!/usr/bin/env bash

java -server -Xmx400m -Dcom.sun.management.jmxremote.port=5001 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -jar target/lastbamboo-common-stun-server-1.0-jar-with-dependencies.jar
