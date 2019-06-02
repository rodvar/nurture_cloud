#!/bin/bash

## build and run tests
mvn clean package

java -jar target/backend-challenge-1.0-SNAPSHOT-jar-with-dependencies.jar
