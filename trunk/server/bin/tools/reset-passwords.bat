echo off
cd ..\..
echo.set TMPCLASSPATH=%%1;%%TMPCLASSPATH%%>~tmp.bat
set TMPCLASSPATH=
for %%i in (lib\*.jar) do call ~tmp.bat "%CD%\%%i"
del /F ~tmp.bat
java -classpath %TMPCLASSPATH% org.lucane.server.tools.ResetPasswords %*
set TMPCLASSPATH=
