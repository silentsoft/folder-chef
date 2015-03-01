@echo off
setlocal enabledelayedexpansion

tasklist /fi "Imagename eq Folder Chef.exe" | find /I "Folder Chef.exe" > nul &&(
echo Sweeper Is Running, Kill process
taskkill /IM "Folder Chef.exe" /F ) || (echo Folder Chef is not running)

set AGENT_HOME=%~dp0
cd %AGENT_HOME%
set jredirectory=%AGENT_HOME%java
set configdirectory=%AGENT_HOME%conf
set libdirectory=%AGENT_HOME%lib

set applicationclass=org.silentsoft.folderchef.main.FolderChef

rem -----------------------------------------------------------------------------
rem ---------------- P O P U L A T E  J A V A  C L A S S P A T H ----------------
rem -----------------------------------------------------------------------------

   set javaclasspath="%configdirectory%"

   pushd %libdirectory%
   for %%i in (*.jar) do (
   	set javaclasspath=!javaclasspath!;"!libdirectory!"\%%i
   )
   popd

rem -----------------------------------------------------------------------------
rem --------------- C A L L  E D G  G U I  A P P L I C A T I O N  ---------------
rem -----------------------------------------------------------------------------

start /min "Folder Chef" "%jredirectory%\jre8\bin\Folder Chef.exe" -classpath %javaclasspath% %applicationclass% "%configdirectory%/config.xml" ^& exit
   

rem -----------------------------------------------------------------------------
rem -----------------------------------------------------------------------------
