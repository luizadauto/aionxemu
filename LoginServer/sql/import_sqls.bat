@echo off
title Aion X Emu LoginServer Sql import console -- by Ares!

CLS
:MENU
ECHO.
ECHO.
ECHO.
ECHO    .................................................................
ECHO    .            Tool made by Ares for you lazy ass newbs!          .
ECHO    .            1 - Type in your mysql user and password           .
ECHO    .            2 - or quit to EXIT                                .
ECHO    .................................................................  
ECHO.
ECHO.
SET /P Ares=Type in your mysql DB_NAME or quit, then press ENTER:
IF %Ares%==quit GOTO EOF
IF NOT %Ares%==quit GOTO MENU2
:MENU2
ECHO.
ECHO.
SET /P Ares2=Type in your mysql USER or quit, then press ENTER:
IF %Ares2%==quit GOTO EOF
IF NOT %Ares2%==quit GOTO MENU3
:MENU3
ECHO.
ECHO.
SET /P Ares3=Type in your mysql PASSWORD or quit, then press ENTER:
IF %Ares3%==quit GOTO EOF
IF NOT %Ares3%==quit GOTO MENU4
:MENU4
ECHO.
ECHO.
ECHO.
ECHO    .................................................................
ECHO    .             1 - New full sql loginserver install               .
ECHO    .             2 - Quit                                          .
ECHO    .................................................................  
ECHO.
ECHO.
SET /P Ares4=Type 1 or 2, then press ENTER:
IF %Ares4%==1 GOTO FULLIMPORT
IF %Ares4%==2 GOTO EOF
:FULLIMPORT
mysqladmin --user %Ares2% --password=%Ares3% create %Ares%
ECHO CREATE DATABASE done!
mysqladmin --user %Ares2% --password=%Ares3% flush-privileges
ECHO flush-privileges done!
mysql --user %Ares2% --password=%Ares3% %Ares% < aengine_ls.sql
ECHO Import aengine_ls.sql done!
ECHO.
ECHO.
ECHO.
ECHO    .................................................................
ECHO    .                   Database installed!                         .
ECHO    .                    Install finished!                          .
ECHO    .                  Press any key to exit                        .
ECHO    .................................................................  
ECHO.
pause
GOTO EOF