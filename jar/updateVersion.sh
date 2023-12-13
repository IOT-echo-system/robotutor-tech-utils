#!/bin/bash

pwd
version=$(cat ./jar/currentVersion.txt)
echo current version: $version
majorVersion=$(echo $version | cut -d '.' -f 1-2 )
minorVersion=$(echo $version | cut -d '.' -f 3)
((minorVersion++))

newVersion=$(echo $majorVersion.$minorVersion)
echo new version: $newVersion

mv ./build/libs/logging-starter-0.0.1-plain.jar ./jar/version-0/loggin-starter-$newVersion.jar
echo $newVersion > ./jar/currentVersion.txt
