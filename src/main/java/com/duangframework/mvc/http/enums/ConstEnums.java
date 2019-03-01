package com.duangframework.mvc.http.enums;


import com.duangframework.db.annotation.Entity;
import com.duangframework.mvc.annotation.*;

import java.lang.annotation.Annotation;

/**
 * Created by laotang on 2017/4/20.
 */
public enum ConstEnums {


    INPUTSTREAM_STR_NAME("duang_inputstream_str", "以JSON或XML方式提交参数时，暂存在Request里的key"),
    CLASS_URL_PROTOCOL_FILE_FIELD("file", "calss类文件URL对象的类型"),
    CLASS_URL_PROTOCOL_JAR_FIELD("jar", "calss类文件URL对象的类型"),
    FRAMEWORK_OWNER_FILED("framework-x-owenr", "laotang"),
    FRAMEWORK_MAPPING_KEY("duangframework", "包含该字段的映射路径不在控制台打印，一般都是作特殊用途"),
    FRAMEWORK_OWNER("duang", "框架名称"),
    FRAMEWORK_BASE_PACKAGE_PATH("com.duangframework", "框架包路径前缀"),
    RESPONSE_STATUS("status", "返回结果状态码"),
    DEFAULT_DATE_FORMAT("default.date.format", "返回结果状态码"),
    DEFAULT_DATE_FORMAT_VALUE("yyyy-MM-dd HH:mm:ss", "默认的时间格式"),
    HTTP_SCHEME_FIELD("http://", "请求协议"),
    HTTPS_SCHEME_FIELD("https://", "请求协议"),
    DEFAULT_CHAR_ENCODE("UTF-8", "编码格式"),
    DEFAULT_LINEBREAK("\n", "换行符"),
    REQUEST_ID_FIELD("requestid", "duang框架的请求ID，每一个请求都应该包含有该ID, 该值先取header头"),
    TERMINAL_FIELD("duang-x-terminal", "请求终端标识， 即请求来源，一般是指pc请求，phone请求之类的， 在请求头里应包含，默认为console"),
    DUANG_ENCRYPT("encrypt-param", "是否开启参数加密"),
    TOKENID_FIELD("tokenId", "令牌字段名称"),
    ;


    private final String value;
    private final String desc;
    /**
     * Constructor.
     */
    private ConstEnums(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * Get the value.
     * @return the value
     */
    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 注解类枚举
     * 注解类，是否需要实例化(BeanHelper时使用)，说明
     */
    public enum ANNOTATION_CLASS {
        CONTROLLER_ANNOTATION(Controller.class, true,"所有Controller类的注解，必须在类添加该注解否则框架忽略扫描"),
        SERVICE_ANNOTATION(Service.class, true,"所有Service类的注解，必须在类添加该注解否则框架忽略扫描"),
        ENTITY_ANNOTATION(Entity.class, false,"所有Entity类的注解，必须在类添加该注解否则框架忽略扫描"),
        WEBSOCKET_ANNOTATION(WebSocket.class, true,"所有WebSocket类的注解，必须在类添加该注解否则框架忽略扫描"),
        PLUGIN_ANNOTATION(Plugin.class, false, "所有Plugin类的注解，必须在类添加该注解否则框架忽略扫描, 但不需要在BeanHelper里进行实例化"),
        HANDLER_ANNOTATION(Handler.class, false, "所有Handler类的注解，必须在类添加该注解否则框架忽略扫描, 但不需要在BeanHelper里进行实例化"),
        LISTENER_ANNOTATION(Listener.class, true, "所有Listener类的注解，必须在类添加该注解否则框架忽略扫描"),
        ;

        Class<? extends Annotation> clazz;
        String name;
        // 是否需要实例化， true为需要
        boolean instance;
        String desc;

        private ANNOTATION_CLASS(Class<? extends Annotation> clazz, boolean instance, String desc) {
            this.clazz = clazz;
            this.instance = instance;
            this.desc = desc;
        }

        public Class<? extends Annotation> getClazz() {
            return clazz;
        }

        public String getName() {
            return clazz.getName();
        }

        /**
         * 是否需要实例化， true为需要
         * @return
         */
        public boolean getInstance() {
            return instance;
        }

        public String desc() {
            return desc;
        }
    }


    public enum LOCATION {
        BEFORE, AFTER;
    }


    public enum PROPERTIES {
        FILE_NAME("duang.properties", "框架配置文件名"),
        USE_ENV("use.env", "开发环境"),
        DEFAULT_ENCODING("utf-8", "默认的编码格式"),
        BASE_PACKAGE_PATH("base.package.path", "要扫描的类路径"),
        REQUEST_TIMEOUT("3000", "默认的请求过期时间毫秒"),
        PRODUCT_APPID("product.appid", "项目APPID"),
        PRODUCT_CODE("product.code", "项目简单称"),
        PRODUCT_URI_PREFIX("product.uri.prefix", "项目api映射前缀，如果存在则强制添加"),
        JAR_PREFIX("jar.prefix", "是扫描的jar包文件名前缀"),
        SERVER_HOST("server.host", "服务器地址"),
        SERVER_PORT("server.port", "服务器端口"),
        TOKENID_FIELD("token.key", "令牌ID字段名称"),
        TOKEN_TIMEOUT_FIELD("token.timeout", "令牌过期时间"),
        BASE_AUTH_USERNAME("base.auth.username", "HTTP基本认证，即请求头参数authorization，Basic Auth的用户名，默认为duang"),
        BASE_AUTH_PASSWORD("base.auth.password", "HTTP基本认证， 即请求头参数authorization，Basic Auth的密码，默认为duangduangduang"),
        SECURIT_REALM_CLASS("security.realm", "安全验证处理实现类，全路径，包括包名"),

        /**mongodb*/
        MONGODB_USERNAME("mongodb.username", "用户名"),
        MONGODB_PASSWORD("mongodb.password", "密码"),
        MONGODB_URL("mongodb.url","url链接字符串"),
        MONGODB_HOST("mongodb.host", "host地址"),
        MONGODB_PORT("mongodb.port", "端口"),
        MONGODB_DATABASE("mongodb.database", "链接的数据库名称"),

        /**mysql*/
        MYSQL_USERNAME("mysql.username", "用户名"),
        MYSQL_PASSWORD("mysql.password", "密码"),
        MYSQL_URL("mysql.jdbc.url","url链接字符串"),
        MYSQL_HOST("mysql.host", "host地址"),
        MYSQL_PORT("mysql.port", "端口"),
        MYSQL_DATABASE("mysql.database", "链接的数据库名称"),
        MYSQL_DATASOURCE("mysql.datasource", "数据库链接池类名称"),


        /**redis*/
        REDIS_DATABASE("redis.database", "redis默认数据库"),
        REDIS_HOST("redis.host", "redis地址"),
        REDIS_USERNAME("redis.username", "redis用户名"),
        REDIS_PASSWORD("redis.password", "redis密码"),
        REDIS_PORT("redis.port", "redis端口"),
        REDIS_TIMEOUT("redis.timeout", "redis超时时间，秒作单位"),
        REDIS_TTL("redis.ttl", "redis存活时间，毫秒作单位"),

        /** security*/
        SECURITY_URI_PREFIX("security.uri.prefix", "安全验证的URI前缀，左匹配方式"),
        SECURITY_CENTER_URL("security.center.url", "安全中心，即权限系统请求地址"),
        AUTHORIZATION_PREFIX("security.authorization.prefix", "header请求头authorization的内容前缀"),
        FILTER_URI_FIELD("security.filter.uri", "允许没有tokenId值请求的URI,多个时以小写,号分隔"),

        /** cors*/
        CORS_ORIGINS("cors.origins", "允许跨域的域名，如有多个以小写,号分隔"),
        CORS_ALLOW_HEADERS("cors.allow.headers", "允许跨域的header头信息，如有多个以小写,号分隔"),

        /** uploadfile */
        UPLOADFILE_DIRECTORY ("uploadfile.directory", "上传文件存放目录，相对于根路径"),

        /** email */
        SMTP_HOST("smtp.host", "邮箱SMTP服务器地址"),
        SMTP_PROT("smtp.prot", "邮箱SMTP服务器端口"),
        EMAIL_ACCOUNT("email.account","发送人邮箱帐号"),
        EMAIL_PASSWORD("email.password","发送人邮箱密码"),

        ;

        private final String value;
        private final String desc;
        /**
         * Constructor.
         */
        private PROPERTIES(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        /**
         * Get the value.
         * @return the value
         */
        public String getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }


    public enum SOCKET {
        CLIENT_IS_NOT_EXTIS("500", "客户端不存在"),
        WEBSOCKET_CONTEXT_FIELD("duang_websocket_context", ""),
        WEBSOCKET_SCHEME_FIELD("ws://", "WebSocket请求协议"),
        ;

        private final String value;
        private final String desc;
        /**
         * Constructor.
         */
        private SOCKET(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        /**
         * Get the value.
         * @return the value
         */
        public String getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }


    public enum REGEXP {
        USERNAME ("^[a-zA-Z0-9]\\w{3,19}$","4-20位的数字字母下划线组合,且不能以下划线开头"),
        MD5PADDWD("^[a-zA-Z0-9]{32}$", "MD5加密后的密码格式"),
        CAPTCHA("^[a-zA-Z0-9]{4}$", "四位字母数字组合验证码"),
        SOURCENAME("^[\\u4E00-\\u9FA5A-Za-z0-9_]{3,17}$", "4--18位字母数字汉字下划线组合"),
        ROLENAME ("^[\\u4E00-\\u9FA5A-Za-z0-9_]{3,17}$", "4--18位字母数字汉字下划线组合"),
        ORDERCOLUMN("^[a-zA-Z0-9_]{0,19}$", "1--20位数字字母下划线组合"),
        ORDERDIR ("^[a-zA-Z0-9_]{0,19}$", "1--20位数字字母下划线组合"),
        PAGE("^[1-9]\\d*$", "正整数"),
        LIMIT("^[1-9]\\d*$", "正整数"),
        ID("^[1-9]\\d*$", "正整数"),
        NUM("^[0-9]*$", "数字"),
        ROUTER("^[A-Za-z0-9/._-]+$", "路由"),
        FUNNAME("^[A-Za-z0-9/._-]+$", "路由"),
        ICONFONT("^[A-Za-z0-9-]+$", "icon"),
        ROLERESOURCE("^(\\d+,)*\\d+$", "1,2,3,4"),
        NICKNAME("^[\\u4E00-\\u9FA5A-Za-z0-9_]{1,11}$", "2--12位字母数字汉字下划线组合"),
        SEARCH("^[\\u4E00-\\u9FA5A-Za-z0-9_]+$", "2--12位字母数字汉字下划线组合"),
        ;

        private final String value;
        private final String desc;
        /**
         * Constructor.
         */
        private REGEXP(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        /**
         * Get the value.
         * @return the value
         */
        public String getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum MQTT {

        CLIENT_ID ("mqtt.client","mqtt客户端ID"),
        ACCOUNT ("mqtt.account","登录帐号"),
        PASSWORD ("mqtt.password","登录密码"),
        ;

        private final String value;
        private final String desc;
        private MQTT(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public String getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum JWT {

        KEY_ID ("kid","kid"),
        ISSUSR ("iss","iss"),
        SUBJECT ("sub","sub"),
        EXPIRES_AT("exp","exp"),
        NOT_BEFORE ("nbf","nbf"),
        ISSUED_AT ("iat","iat"),
        JWT_ID ("jti","jti"),
        AUDIENCE("aud","aud"),
        RSA_PUBLICKEY("rsa.publickey", ""),
        RSA_PRIVATEKEY("rsa.privatekey", ""),
        ;

        private final String value;
        private final String desc;
        private JWT(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public String getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }

}
