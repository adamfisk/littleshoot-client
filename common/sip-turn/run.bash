#!/usr/bin/env bash

java -server -Xmx700m -Dcom.sun.management.jmxremote.port=8190 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -jar target/sip-turn.jar
