echo off
echo.set TMPCLASSPATH=%%1;%%TMPCLASSPATH%%>~tmp.bat
set TMPCLASSPATH=
for %%i in (lib\*.jar) do call ~tmp.bat "%CD%\%%i"
del /F ~tmp.bat
start javaw -classpath %TMPCLASSPATH% org.lucane.client.Client
set TMPCLASSPATH=