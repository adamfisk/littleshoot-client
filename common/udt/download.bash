#!/usr/bin/env bash

java -cp target/lastbamboo-common-udt-1.0.jar udt.util.ReceiveFile 192.168.1.127 7777 pom.xml downloadedPom.xml
