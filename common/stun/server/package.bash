#!/usr/bin/env bash

mvn clean install -Dmaven.test.skip=true
tar czvf stun.tgz target/*with-dependencies.jar run.bash runWrapper.bash README.txt
