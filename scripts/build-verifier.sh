#!/bin/bash
# maven.home needs to be set for the AWS Devicefarm unit test
mvn -B install --file pom.xml -Dmaven.home=$(which mvn | sed -e 's/\(\/bin\/mvn\)*$//g') && cd justtestlah-demos && mvn package -DskipTests=true