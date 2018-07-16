package com.duangframework.server.common;

import io.netty.util.AsciiString;

/**
 * Created by laotang on 2017/10/30.
 */
public class ServerConfig {

    //boss线程数，建议为1
    public static int MAX_BOSS_EXECUTORS_NUMBER = 1;
    public static final String BOSSGROUP_POOLTHREAD_NAME = "boss@";
    public static final String WORKERGROUP_POOLTHREAD_NAME = "worker@";

    public static long KEEP_ALIVETIME = 2L;

    public static int IO_RATIO_NUMBER = 100;

    public static int SO_BACKLOG = 32768;

    public static int IDLE_TIME_SECONDS = 180;  //180秒

    public static String DEFAULT_CHARSET = "UTF-8";

    public static String UNKNOWN= "unknown";

    public static final AsciiString X_FORWARDED_FOR = new AsciiString("x-forwarded-for");

    public static final AsciiString X_REAL_IP = new AsciiString("x-real-ip");

}
