@echo off

del /q /s build

rem spring core
set libs=lib\spring-context-3.2.13.RELEASE.jar;lib\spring-beans-3.2.13.RELEASE.jar;lib\spring-core-3.2.13.RELEASE.jar

rem xml config
set libs=%libs%;lib\spring-expression-3.2.13.RELEASE.jar

rem spring jdbc
set libs=%libs%;lib\spring-jdbc-3.2.13.RELEASE.jar

rem logging
set libs=%libs%;lib\slf4j-api.jar;lib\slf4j-log4j12.jar;lib\log4j.jar;lib\jcl-over-slf4j.jar

rem misc
set libs=%libs%;lib\commons-cli-1.2.jar

rem db
set libs=%libs%;lib\h2-1.4.187.jar;lib\spring-tx-3.2.13.RELEASE.jar

javac -cp %libs% -d build src\*.java
if errorlevel 1 exit

copy db\init.sql build
copy log4j.xml build
copy context.xml build
copy h2db.properties build

jar cvfe main.jar Test -C build .

echo on

java -cp %libs%;main.jar Test %*
