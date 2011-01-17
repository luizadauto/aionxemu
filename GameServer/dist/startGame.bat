:######################################################################## 
:# File name: startGame.bat
:# Edited Last By: Magenik 
:# V 1.0 1
:######################################################################## 

@echo off
title Aion-Engine Game Server Console
:start
echo  ______                          ______                                         
echo /\  _  \  __                    /\  ___\                                        
echo \ \ \ \ \/\_\    ___     ___    \ \ \____    ___      __   __    ___      __    
echo  \ \  __ \/\ \  / __`\ /' _ `\   \ \  ___\ /' _ `\  /'__`\/\_\ /' _ `\  /'__`\  
echo   \ \ \/\ \ \ \/\ \ \ \/\ \/\ \   \ \ \____/\ \/\ \/\ \ \ \/\ \/\ \/\ \/\  __/  
echo    \ \_\ \_\ \_\ \____/\ \_\ \_\   \ \_____\ \_\ \_\ \___, \ \ \ \_\ \_\ \____\ 
echo     \/_/\/_/\/_/\/___/  \/_/\/_/    \/_____/\/_/\/_/\/_____ \ \_\/_/\/_/\/____/ 
echo                                                        /\____\/_/               
echo                                                        \/____/                  
echo.
echo Starting Aion-Engine Game Server.
echo.

set JAVA6="auto"
set JAR=ae-game-1.0.1.jar

set X86="%ProgramFiles(x86)%"
if %JAVA6% == "auto" (
  echo Autodetecting java
  if %X86% NEQ "" (
    echo Testing x86 folder
    for /D %%j in ("%ProgramFiles(x86)%\jre6*" "%ProgramFiles(x86)%\jre1.6.*" "%ProgramFiles(x86)%\jdk1.6.*" "%ProgramFiles(x86)%\Java\jre6*" "%ProgramFiles(x86)%\Java\jre1.6.*" "%ProgramFiles(x86)%\Java\jdk1.6.*") do (
      echo Checking %%j folder
      if exist "%%j\bin\java.exe" (
        echo Found java in %%j folder
        set JAVA6="%%j\bin\java.exe"
      )
    )
  ) else (
    echo Testing default folder
    if defined ProgramFiles (
    for /D %%j in ("%ProgramFiles%\jre6*" "%ProgramFiles%\jre1.6.*" "%ProgramFiles%\jdk1.6.*" "%ProgramFiles%\Java\jre6*" "%ProgramFiles%\Java\jre1.6.*" "%ProgramFiles%\Java\jdk1.6.*") do (
        echo Checking %%j folder
        if exist "%%j\bin\java.exe" (
          echo Found java in %%j folder
          set JAVA6="%%j\bin\java.exe"
        )
      )
    )
  )
)

:rerun
if %JAVA6% == "auto" (
  echo ERROR: Java not found!
  echo Please download and install from java.com
  pause
  exit
) else (
  echo Starting java from %JAVA6%
  %JAVA6% -Xms512m -Xmx1536m -ea -Xbootclasspath/p:./libs/jsr166.jar -cp ./libs/*;%JAR% gameserver.GameServer

  if errorlevel 11 (
    if not exist %JAR% (
      echo ========================================
      echo Warning: %JAR% not not found.
      echo ========================================
    )

    echo ERROR: Failed to run %JAR%
    echo JAVA6=%JAVA6%
    pause
    exit
  )

  if errorlevel 10 (
    goto rerun
  )

  if errorlevel 1 (
    if not exist %JAR% (
      echo ========================================
      echo Warning: %JAR% not found.
      echo ========================================
    )

    echo ERROR: Failed to run %JAR%
    echo JAVA6=%JAVA6%
    pause
    exit
  )
)