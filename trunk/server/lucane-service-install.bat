echo off

echo Building the classpath

echo.set TMPCLASSPATH=%%1;%%TMPCLASSPATH%%>~tmp.bat
set TMPCLASSPATH=
for %%i in (lib\*.jar) do call ~tmp.bat "%CD%\%%i"
del /F ~tmp.bat

echo Trying to remove the service if it exists

lucane-service.exe -uninstall Lucane-server

echo .
echo Installing the service

lucane-service.exe -install Lucane-server %JAVA_HOME%\jre\bin\server\jvm.dll -Djava.class.path=%TMPCLASSPATH% -start org.lucane.server.Server -params "%cd%" -out "%CD%\lucane-service.out.log" -err "%CD%\lucane-service.err.log"
rem TODO write the stop (-stop org.lucane.client.Client -params shutdown shutdown  can't be used)

set TMPCLASSPATH=
pause