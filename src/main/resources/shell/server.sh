#!/usr/bin/env bash
basepath=$(cd `dirname $0`; pwd)
echo $basepath
export PATH=$JAVA_HOME/bin:$PATH:
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$basepath/lib/*:

set JAVA_OPTS=-Xms128m -Xmx256m
java -jar original-server-0.0.0-RELEASE.jar
echo 'my qq is 330937205,if you need help,please contact me'