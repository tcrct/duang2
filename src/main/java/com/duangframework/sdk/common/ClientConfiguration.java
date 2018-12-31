package com.duangframework.sdk.common;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by laotang on 2018/12/30.
 */
public class ClientConfiguration {

    public static final String DEFAULT_USER_AGENT = VersionInfoUtils.getDefaultUserAgent();

    public static final int DEFAULT_MAX_RETRIES = 3;

    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = -1;
    public static final int DEFAULT_CONNECTION_TIMEOUT = 50 * 1000;
    public static final int DEFAULT_SOCKET_TIMEOUT = 50 * 1000;
    public static final int DEFAULT_MAX_CONNECTIONS = 1024;
    public static final long DEFAULT_CONNECTION_TTL = -1;
    public static final long DEFAULT_IDLE_CONNECTION_TIME = 60 * 1000;
    public static final int DEFAULT_VALIDATE_AFTER_INACTIVITY = 2 * 1000;
    public static final int DEFAULT_THREAD_POOL_WAIT_TIME = 60 * 1000;
    public static final int DEFAULT_REQUEST_TIMEOUT = 5 * 60 * 1000;
    public static final long DEFAULT_SLOW_REQUESTS_THRESHOLD = 5 * 60 * 1000;

    public static final boolean DEFAULT_USE_REAPER = true;

    public static final String DEFAULT_CNAME_EXCLUDE_LIST = "aliyuncs.com,aliyun-inc.com,aliyun.com";

//    public static final SignVersion DEFAULT_SIGNATURE_VERSION = SignVersion.V1;

    protected String userAgent = DEFAULT_USER_AGENT;
    protected int maxErrorRetry = DEFAULT_MAX_RETRIES;
    protected int connectionRequestTimeout = DEFAULT_CONNECTION_REQUEST_TIMEOUT;
    protected int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    protected int socketTimeout = DEFAULT_SOCKET_TIMEOUT;
    protected int maxConnections = DEFAULT_MAX_CONNECTIONS;
    protected long connectionTTL = DEFAULT_CONNECTION_TTL;
    protected boolean useReaper = DEFAULT_USE_REAPER;
    protected long idleConnectionTime = DEFAULT_IDLE_CONNECTION_TIME;

    protected String proxyHost = null;
    protected int proxyPort = -1;
    protected String proxyUsername = null;
    protected String proxyPassword = null;
    protected String proxyDomain = null;
    protected String proxyWorkstation = null;

    protected boolean supportCname = true;
    protected List<String> cnameExcludeList = new ArrayList<String>();
    protected Lock rlock = new ReentrantLock();

    protected boolean sldEnabled = false;

    protected int requestTimeout = DEFAULT_REQUEST_TIMEOUT;
    protected boolean requestTimeoutEnabled = false;
    protected long slowRequestsThreshold = DEFAULT_SLOW_REQUESTS_THRESHOLD;

    protected Map<String, String> defaultHeaders = new LinkedHashMap<String, String>();

    protected boolean crcCheckEnabled = true;




}
