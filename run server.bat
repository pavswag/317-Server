@echo off
set JAR=grimoire-server-fat.jar
set HEAP_MIN=256m
set HEAP_MAX=1024m

echo Starting RSPS Server (headless)...
java -Xms%HEAP_MIN% -Xmx%HEAP_MAX% -Djava.awt.headless=true -jar %JAR%
pause
