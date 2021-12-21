#!/bin/bash
set -ex
npm install -g appium
appium -v
appium &>/dev/null &
mvn install -DskipTests && cd justtestlah-demos
mvn test -DjtlProps=$(pwd)/target/test-classes/justtestlah-android.properties -Djava.version=17