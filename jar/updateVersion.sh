#!/bin/bash

handle_error() {
  echo "An error occurred. Exiting."
  exit 1
}
trap 'handle_error' ERR

version=$(cat ./jar/currentVersion.txt)
echo current version: $version
majorVersion=$(echo $version | cut -d '.' -f 1-2 )
minorVersion=$(echo $version | cut -d '.' -f 3)
((minorVersion++))

newVersion=$(echo $majorVersion.$minorVersion)

mv ./build/libs/logging-starter-0.0.1-plain.jar ./jar/version-0/logging-starter-$newVersion.jar
echo $newVersion > ./jar/currentVersion.txt
echo "create new jar ==> logging-starter-$newVersion.jar"
