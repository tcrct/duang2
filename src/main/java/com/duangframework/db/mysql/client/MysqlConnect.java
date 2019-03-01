package com.duangframework.db.mysql.client;


import com.duangframework.db.DBConnect;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;


/**
 * @author Created by laotang
 * @date createed in 2018/4/17.
 */
public class MysqlConnect extends DBConnect {


    public MysqlConnect(String host, int port, String dataBase) {
        this(host, port, dataBase, "", "");
    }

    public MysqlConnect(String host, int port, String dataBase, String userName, String passWord) {
        this(host, port, dataBase, userName, passWord,"");
    }

    public MysqlConnect(String url) {
        this("", 0, "", "", "", url);
    }

    private MysqlConnect(String host, int port, String dataBase, String userName, String passWord, String url) {
        super(host, port, dataBase, userName, passWord, url);
//        setDataSourceFactoryClassName(DruidDataSourceFactory.class.getName());
    }

    @Override
    public String getUrl() {
        if(ToolsKit.isEmpty(url)) {
            String host = this.getHost().toLowerCase().replace(ConstEnums.HTTP_SCHEME_FIELD.getValue(), "").replace(ConstEnums.HTTPS_SCHEME_FIELD.getValue(), "").replace("*", "");
            int endIndex = host.indexOf(":");
            host = host.substring(0, endIndex > -1 ? endIndex : host.length());
            StringBuilder jdbc = new StringBuilder();
            jdbc.append("jdbc:mysql://")
                    .append(host).append(":").append(getPort()).append("/").append(getDatabase()).append("?")
                    .append("?user=").append(getUsername())
                    .append("&password=").append(getPassword())
                    .append("&characterEncoding=").append(ConstEnums.DEFAULT_CHAR_ENCODE.getValue().toLowerCase().replace("-",""))
                    .append("&useUnicode=true&autoReconnect=true&failOverReadOnly=false");
//                    .append("&useSSL=true");
//            return "jdbc:mysql://" + host + ":" + getPort() + "/" + getDatabase() +"?user="+getAccount()+"&password="+getPassword()+"&useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false";
            url = jdbc.toString();
        }
        return url;
    }
}
