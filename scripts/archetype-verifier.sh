#!/bin/bash
mvn install -DskipTests && cd /tmp
mvn archetype:generate -B -DarchetypeGroupId=qa.justtestlah -DarchetypeArtifactId=justtestlah-quickstart -DgroupId=martin -DartifactId=demo -DjtlVersion=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout) -Dheadless=true && cd demo
mvn test
cd .. & rm -rf demo