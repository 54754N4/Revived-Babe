@echo off
echo Compiling...
cmd /C "C:\Users\mazen\Desktop\Workspaces\Java\Eclipse Workspace 2020\Revived Babe\gradlew" shadowJar > "C:\Users\mazen\Desktop\Workspaces\Java\Eclipse Workspace 2020\Revived Babe\shadowjar-build.log"
find "FAILED" "C:\Users\mazen\Desktop\Workspaces\Java\Eclipse Workspace 2020\Revived Babe\shadowjar-build.log" >nul && pause || "C:\Users\mazen\Desktop\Workspaces\Java\Eclipse Workspace 2020\Revived Babe\start-bot.bat"
