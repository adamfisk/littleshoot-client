#!/usr/bin/env bash

mvn clean install -Dmaven.test.skip=true
tar czvf sip-turn.tgz target/sip-turn.jar run.bash runWrapper.bash README.txt
