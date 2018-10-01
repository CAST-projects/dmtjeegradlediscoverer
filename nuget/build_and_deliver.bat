:: ===================================================
:: Build tool for nuget extension
:: ===================================================
@if not defined LOGDEBUG set LOGDEBUG=off
@echo %LOGDEBUG%
SetLocal EnableDelayedExpansion

set WORKSPACE=%cd%
for %%a in (%0) do set CMDDIR=%%~dpa
for %%a in (%0) do set CMDNAME=%%~na

set CMDPATH=%0
set RETCODE=1

cd /d %WORKSPACE%

:: Checking arguments
set SRC_DIR=
set TOOLS_DIR=
set PACK_DIR=
set BUILDNO=

:LOOP_ARG
    set option=%1
    if not defined option goto CHECK_ARGS
    shift
    set value=%1
    if defined value set value=%value:"=%
    call set %option%=%%value%%
    shift
goto LOOP_ARG

:CHECK_ARGS
if not defined SRC_DIR (
	echo.
	echo No "src_dir" defined !
	goto endclean
)
if not defined TOOLS_DIR (
	echo.
	echo No "tools_dir" defined !
	goto endclean
)
if not defined PACK_DIR (
	echo.
	echo No "pack_dir" defined !
	goto endclean
)
if not defined BUILDNO (
	echo.
	echo No "buildno" defined !
	goto endclean
)

for %%a in (%SRC_DIR% %TOOLS_DIR%) do (
    if not exist %%a (
        echo.
        echo ERROR: Folder %%a does not exist
        goto endclean
    )
)

set THEDELIVDIR=upload
for %%a in (%PACK_DIR% %THEDELIVDIR%) do (
    if exist %%a rmdir /s /q %%a
    mkdir %%a
    if errorlevel 1 goto endclean
)

set ROBOPT=/ndl /njh /njs /np
set EXTNAME=dmtjeegradlediscoverer
set PROJNAME=CAST-Discoverer-JEE-Gradle

robocopy %ROBOPT% %SRC_DIR%\nuget\package_files %PACK_DIR% /e
if errorlevel 8 goto endclean

:: get extension version
for /f "tokens=1-4 delims=.-" %%a in ('dir /b %SRC_DIR%\%PROJNAME%\target\%EXTNAME%-*.jar') do set EXTVERS=%%b.%%c.%%d
if not defined EXTVERS (
	echo.
	echo ERROR: extension version cannot be retrieved ....
	goto endclean
)

robocopy %ROBOPT% %SRC_DIR%\%PROJNAME%\target %PACK_DIR%\TOOLS\Plugins\%EXTNAME%-%EXTVERS% %EXTNAME%-%EXTVERS%.jar 
if errorlevel 8 goto endclean
ren %PACK_DIR%\TOOLS\Plugins\%EXTNAME%-%EXTVERS%\%EXTNAME%-%EXTVERS%.jar %EXTNAME%-%EXTVERS%.mda
if errorlevel 1 goto endclean

:: ========================================================================================
:: Nuget packaging
:: ========================================================================================
set CMD=%TOOLS_DIR%\nuget_package_basics.bat outdir=%THEDELIVDIR% pkgdir=%PACK_DIR% buildno=%BUILDNO%
echo Executing command:
echo %CMD%
call %CMD%
if errorlevel 1 goto endclean

for /f "tokens=*" %%a in ('dir /b %THEDELIVDIR%\com.castsoftware.*.nupkg') do set PACKPATH=%THEDELIVDIR%\%%a
if not defined PACKPATH (
	echo .
	echo ERROR: No package was created : file not found %THEDELIVDIR%\com.castsoftware.*.nupkg ...
	goto endclean
)
if not exist %PACKPATH% (
	echo .
	echo ERROR: File not found %PACKPATH% ...
	goto endclean
)

set GROOVYEXE=groovy
%GROOVYEXE% --version 2>nul
if errorlevel 1 set GROOVYEXE="%GROOVY_HOME%\bin\groovy"
%GROOVYEXE% --version 2>nul
if errorlevel 1 (
	echo ERROR: no groovy executable available, need one!
	goto endclean
)

:: ========================================================================================
:: Nuget checking
:: ========================================================================================
set CMD=%GROOVYEXE% %TOOLS_DIR%\nuget_package_verification.groovy --packpath=%PACKPATH%
echo Executing command:
echo %CMD%
call %CMD%
if errorlevel 1 goto endclean

echo.
echo Extension creation in SUCCESS
set RETCODE=0

:endclean
cd /d %WORKSPACE%
exit /b %RETCODE%