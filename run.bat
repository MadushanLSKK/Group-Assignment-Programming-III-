@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -d out src\app\*.java src\dataset\*.java src\sorting\*.java src\performance\*.java src\gui\*.java
if errorlevel 1 pause & exit /b 1
java -cp out app.Main
