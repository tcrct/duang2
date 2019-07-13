@echo off
SET  now_path=%cd%
set path= %path%;%now_path%\lib;%now_path%\service_lib;
set classpath= %classpath%;%now_path%\lib;%now_path%\service_lib;
set JAVA_OPTS=-Xms512m -Xmx1024m
java -jar duang-xxxxx.jar
pause