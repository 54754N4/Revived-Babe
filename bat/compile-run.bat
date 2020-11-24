@echo off
echo Compiling...
cd "C:\Users\mazen\Desktop\Workspaces\Java\Eclipse Workspace 2020\Revived Babe\"
cmd /C .\gradlew shadowJar > shadowjar-build.log
find "FAILED" shadowjar-build.log >nul && pause || .\bat\start-bot.bat"
