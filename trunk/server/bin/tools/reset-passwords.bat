@echo off
                                                                                                                                                                             
rem go in the server directory
if not "%LUCANE_SERVER%" == "" (
  cd "%LUCANE_SERVER%"
  goto fileFound
)
if not "%LUCANE_HOME%" == "" (
  cd "%LUCANE_HOME%\server"
  goto fileFound
)
if exist reset-passwords.bat (
  cd ..\..
  goto fileFound
)
if exist server.bat (
  cd ..
  goto fileFound
)
if exist bin\server.bat (
  goto fileFound
)
if exist server\bin\server.bat (
  cd server
  goto fileFound
)
                                                                                                                                                                             
echo unable to find server, set LUCANE_HOME or LUCANE_SERVER !
goto end
                                                                                                                                                                             
:fileFound

rem classpath generation
echo.set TMPCLASSPATH=%%1;%%TMPCLASSPATH%%>~tmp.bat
set TMPCLASSPATH=
for %%i in (lib\*.jar) do call ~tmp.bat "%CD%\%%i"
del /F ~tmp.bat

rem run program
java -classpath %TMPCLASSPATH% org.lucane.server.tools.ResetPasswords %*

rem clean
set TMPCLASSPATH=

:end
