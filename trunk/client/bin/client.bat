@echo off
                                                                                                                                                                             
rem go in the client directory
if not "%LUCANE_CLIENT%" == "" (
  cd "%LUCANE_CLIENT%"
  goto fileFound
)
if not "%LUCANE_HOME%" == "" (
  cd "%LUCANE_HOME%\client"
  goto fileFound
)
if exist client.bat (
  cd ..
  goto fileFound
)
if exist bin\client.bat (
  goto fileFound
)
if exist client\bin\client.bat (
  cd client
  goto fileFound
)
                                                                                                                                                                             
echo unable to find client, set LUCANE_HOME or LUCANE_CLIENT !
goto end
                                                                                                                                                                             
:fileFound

rem classpath generation
echo.set TMPCLASSPATH=%%1;%%TMPCLASSPATH%%>~tmp.bat
set TMPCLASSPATH=
for %%i in (lib\*.jar) do call ~tmp.bat "%CD%\%%i"
del /F ~tmp.bat

rem run program
set VM_OPTIONS=-Djava.library.path=lib
start javaw %VM_OPTIONS% -classpath %TMPCLASSPATH% org.lucane.client.Client %1 %2

rem clean
set TMPCLASSPATH=

:end
