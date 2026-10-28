#!/bin/sh
# Compile and run (needs JDK 8 or newer)
cd "$(dirname "$0")"
mkdir -p out
javac -d out src/app/*.java src/dataset/*.java src/sorting/*.java src/performance/*.java src/gui/*.java && java -cp out app.Main
