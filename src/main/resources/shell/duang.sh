#!/bin/bash

# setting java home
export APP_JAVA_HOME='/app/java/jdk1.8.0_162'
#project name need unique
AppName=message-center
#main class
StartClass=com.zat.message.Application
#setting jvm
Xms=1024
Xmx=1024

# ------------------------------------------------------------------------------------------------------------

#setting the java version of the program runtime
export PATH=$APP_JAVA_HOME/bin:$PATH;
export CLASSPATH=.:$APP_JAVA_HOME/lib/tools.jar:$APP_JAVA_HOME/lib/dt.jar;
source /etc/profile
#jvm parameter
APP_HOME=`pwd`
START_RUN_DATE=`date '+%Y-%m-%d'`
START_RUN_TIME=`date '+%H:%M:%S'`
export JVM_OPTS="-Xms${Xms}M -Xmx${Xmx}M -XX:PermSize=256M -XX:MaxPermSize=512M -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDateStamps  -XX:+PrintGCDetails -XX:NewRatio=1 -XX:SurvivorRatio=30 -XX:+UseParallelGC -XX:+UseParallelOldGC"
export JVM_OPTS="$JVM_OPTS -Dname=$AppName -Dapp.start.date=$START_RUN_DATE -Dapp.start.time=$START_RUN_TIME -Duser.timezone=Asia/Shanghai"
LOG_PATH=$APP_HOME/logs
mkdir -p $LOG_PATH

if [ "$1" = "" ];
then
    echo -e "\033[0;31m operation name is not exist \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
    exit 1
fi

if [ "$AppName" = "" ];
then
    echo -e "\033[0;31m app name is not exist \033[0m"
    exit 1
fi

for i in $APP_HOME/WEB-INF/lib/*.jar;
do
    LIB_PATH=${LIB_PATH}:$i
done

function start()
{
    PID=`ps -ef |grep java|grep $AppName|grep -v grep|awk '{print $2}'`

	if [ x"$PID" != x"" ]; then
	    echo "$AppName is running..."
	else
		nohup java -server $JVM_OPTS -classpath $CLASSPATH:$LIB_PATH:$APP_HOME/WEB-INF/classes $StartClass >> $LOG_PATH/$AppName.log 2>&1 &
		PID=`ps -ef |grep java|grep $AppName|grep -v grep|awk '{print $2}'`
		echo "Start $AppName (pid:$PID) success..."
	fi
}

function stop()
{
  echo "Stop $AppName"
	PID=""
	query(){
		PID=`ps -ef |grep java|grep $AppName|grep -v grep|awk '{print $2}'`
	}

	query
	if [ x"$PID" != x"" ]; then
		kill -TERM $PID
		echo "$AppName (pid:$PID) exiting..."
		while [ x"$PID" != x"" ]
		do
			sleep 1
			query
		done
		echo "$AppName exited."
	else
		echo "$AppName already stopped."
	fi
}

function restart()
{
    stop
    sleep 2
    start
}

function status()
{
    PID=`ps -ef |grep java|grep $AppName|grep -v grep|wc -l`
    if [ $PID != 0 ];then
        echo "$AppName (pid:$PID) is running..."
    else
        echo "$AppName (pid:$PID) is not running..."
    fi
}

case $1 in
    start)
    start;;
    stop)
    stop;;
    restart)
    restart;;
    status)
    status;;
    *)

esac
