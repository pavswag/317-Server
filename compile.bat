@echo off
setlocal

set SRC_DIR=src
set OUT_DIR=out
set JAR_NAME=grimoire-server.jar
set MAIN_CLASS=io.xeros.Server
set MANIFEST_FILE=manifest.txt

echo Creating output directory...
mkdir %OUT_DIR%

echo Compiling Java files...
javac -d %OUT_DIR% %SRC_DIR%\io\xeros\*.java

echo Writing manifest...
echo Main-Class: %MAIN_CLASS% > %MANIFEST_FILE%

echo Building JAR...
jar cfm %JAR_NAME% %MANIFEST_FILE% -C %OUT_DIR% .

echo Done. Launch with:
echo java -jar %JAR_NAME%

endlocal
pause
