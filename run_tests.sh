#!/bin/sh
cd "$(dirname "$0")"
mkdir -p out out_test
javac -d out src/app/*.java src/dataset/*.java src/sorting/*.java src/performance/*.java src/gui/*.java && javac -cp out -d out_test test/SelfTest.java && java -cp out:out_test SelfTest
