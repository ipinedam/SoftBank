@echo off
cls
set _SCRIPT_PATH=%~dp0
chcp 65001 > nul
cd %_SCRIPT_PATH%\..
java -Dfile.encoding=UTF-8 -jar "%_SCRIPT_PATH%..\target\SoftBank-1.0-SNAPSHOT-jar-with-dependencies.jar"
cd %_SCRIPT_PATH%
chcp 850 > nul