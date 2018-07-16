package com.duangframework.db.mongodb.client;


import com.duangframework.db.DBConnect;
import com.duangframework.kit.ToolsKit;

import java.util.Arrays;
import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/6/26.
 */
public class MongoConnect extends DBConnect {

    public MongoConnect(String host, int port, String database) {
        this(host, port, database, null, null);
    }

    public MongoConnect(String host, int port, String database, String username, String password) {
        this(host, port, database, username, password, null);
    }

    protected MongoConnect(String host, int port, String database, String username, String password, String url) {
        super(host, port, database, username, password, null);
    }

    public MongoConnect(String url) {
        super(null, 0, null, null, null, url);
    }

    /**
     * 取集群地址<br/>
     * 此时host字符串的格式为： ip:port,ip1:port1,ip2:port2，注意小写逗号分隔
     * @return
     */
    public List<String> getRepliCaset() {
        List<String> repliCaset = null;
        String[] hostArray = host.split(",");
        // 集群必须由两个节点组成
        if(ToolsKit.isNotEmpty(hostArray) && hostArray.length > 1) {
            repliCaset = Arrays.asList(hostArray);
        }
        return repliCaset;
    }
}
