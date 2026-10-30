@echo off
cd /d "%~dp0"
if not exist out mkdir out
if not exist out_test mkdir out_test
javac -d out src\app\*.java src\dataset\*.java src\sorting\*.java src\performance\*.java src\gui\*.java
javac -cp out -d out_test test\SelfTest.java
java -cp out;out_test SelfTest
pause
