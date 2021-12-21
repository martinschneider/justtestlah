#!/bin/bash
npm install -g appium
appium -v
appium &>/dev/null &
mvn install -DskipTests && cd justtestlah-demos
mvn test -DjtlProps=$(pwd)/target/test-classes/android-verifier.properties -Djava.version=17