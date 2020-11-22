@echo off
echo Launching Babe ...
cd "C:\Users\mazen\Desktop\Workspaces\Java\Eclipse Workspace 2020\Revived Babe"
type shadowjar-build.log
java -jar ".\build\libs\Revived Babe-2.0-all.jar"
if "%ERRORLEVEL%" NEQ "0" .\compile-run.bat
exit