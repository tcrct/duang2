#!/bin/sh
source /etc/profile
#setting project value
PRODUCT_APPID=20190123
PRODUCT_CODE=signet-openapi
MVC_CONFIG=com.signetz.openapi.Duang
SERVER_PORT=9090
SERVER_HOST=0.0.0.0
# value:dev(locat)  uat(obt)  pro(api)
USE_ENV=dev

#setting jvm
Xms=500
Xmx=500
NewSize=300
MaxNewSize=300

#get project current path
PROJECT_PATH=`pwd`
pid=""

#get log path
LOG_DIR=$PROJECT_PATH/logs
mkdir -p $LOG_DIR

#get project pid
if [ -f $PROJECT_PATH/duang.pid ]
then
  pid=`cat $PROJECT_PATH/duang.pid`
fi

# if project is run,kill it
if [ "$pid" = "" ]
then
  echo "run ${PRODUCT_CODE}..."
else
  echo "${PRODUCT_CODE} is run, need kill "$pid
  kill -9 $pid
  sleep 1
  echo "kill $pid is success, reruning ${PRODUCT_CODE}..."
fi

# load project all jar file
for i in $PROJECT_PATH/WEB-INF/lib/*.jar;
do
    LIB_PATH=${LIB_PATH}:$i
done

# setting opts
export JAVA_OPTS="-Xms${Xms}m -Xmx${Xmx}m -Xss256k -XX:NewSize=${NewSize}m -XX:MaxNewSize=${MaxNewSize}m -XX:SurvivorRatio=8 -XX:-ReduceInitialCardMarks"
export JAVA_OPTS="$JAVA_OPTS -XX:+UseParNewGC -XX:ParallelGCThreads=4 -XX:MaxTenuringThreshold=9 -XX:+UseConcMarkSweepGC -XX:+DisableExplicitGC -XX:+UseCMSInitiatingOccupancyOnly -XX:+ScavengeBeforeFullGC -XX:+CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=60 -XX:+CMSClassUnloadingEnabled -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+ExplicitGCInvokesConcurrent -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationConcurrentTime -XX:+PrintHeapAtGC -XX:+HeapDumpOnOutOfMemoryError -XX:-OmitStackTraceInFastThrow -Duser.timezone=Asia/Shanghai -Dclient.encoding.override=UTF-8 -Dfile.encoding=UTF-8"
export JAVA_OPTS="$JAVA_OPTS -Dserver.host=$SERVER_HOST -Dserver.port=$SERVER_PORT -Duse.env=$USE_ENV -Dlogging.file=$LOG_DIR/$PRODUCT_CODE.log -Xloggc:$LOG_DIR/heap_trace.txt -XX:HeapDumpPath=$LOG_DIR/HeapDumpOnOutOfMemoryError/ -Dproduct.appid=$PRODUCT_APPID -Duse.env=$USE_ENV"

# run the project
java -server $JAVA_OPTS -classpath $CLASSPATH:$LIB_PATH:$PROJECT_PATH/WEB-INF/classes $MVC_CONFIG >> $LOG_DIR/$PRODUCT_CODE.log 2>&1 &
