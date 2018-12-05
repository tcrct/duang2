package com.duangframework.db.mongodb.client;

import com.duangframework.db.IClient;
import com.duangframework.exception.MongodbException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.utils.MD5;
import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/6/26.
 */
public class MongoClientAdapter implements IClient<MongoClient> {

    private static final Logger logger = LoggerFactory.getLogger(MongoClientAdapter.class);

    private MongoConnect mongoConnect;
    private MongoClient mongoClient;
    private boolean isDefaultClient;
    /**
     *链接客户端ID, 如多实例里，用于区分
     * 生成规则， MD5(this.toString())
     */
    private String id;

    public MongoClientAdapter(MongoConnect mongodbConnect){
        mongoConnect = mongodbConnect;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDefaultClient() {
        return isDefaultClient;
    }
    public void setDefaultClient(boolean isDefaultClient) {
        this.isDefaultClient = isDefaultClient;
    }


    @Override
    public String getId() {
        if(ToolsKit.isEmpty(id) && ToolsKit.isNotEmpty(mongoConnect)) {
            return MD5.MD5Encode(mongoConnect.toString());
        }
        return id;
    }

    @Override
    public MongoConnect getDbConnect() {
        return mongoConnect;
    }

    @Override
    public MongoClient getClient() throws Exception {
        if(null == mongoClient) {
            if (ToolsKit.isEmpty(mongoConnect)) {
                return null;
            }
            if (ToolsKit.isEmpty(mongoConnect.getUrl())) {
                try {
                    MongoClientOptions options = MongoClientOptions.builder().readPreference(ReadPreference.secondaryPreferred()).build();
                    mongoClient = ToolsKit.isEmpty(auth()) ? new MongoClient(hosts(), options) :  new MongoClient(hosts(), auth(), options);
                } catch (Exception e) {
                    throw new MongodbException("Can't connect mongodb: " + e.getMessage(), e);
                }
            } else {
                mongoClient = createMongoDBClientWithURI();
            }
        }
        return mongoClient;
    }

    @Override
    public void close() {
        if(null != mongoClient) {
            mongoClient.close();
        }
    }

    /*
     * 取链接地址
     * @return
     */
    private List<ServerAddress> hosts() {
        List<String> nodeTmpList = mongoConnect.getRepliCaset();
        List<ServerAddress> mongoNodeList = new ArrayList<>();
        if( ToolsKit.isNotEmpty(nodeTmpList) ) {
            for(String replicasetString : nodeTmpList) {
                String[] replicasetItemArray = replicasetString.split(":");
                if(ToolsKit.isEmpty(replicasetItemArray) || replicasetItemArray.length != 2){
                    throw new RuntimeException("replicasetItemArray is null or length != 2 ");
                }
                mongoNodeList.add(new ServerAddress(replicasetItemArray[0], Integer.parseInt(replicasetItemArray[1])));
                logger.warn("connect replicaset mongodb host: " + replicasetItemArray[0]+"           port: "+ replicasetItemArray[1]);
            }
        } else {
            if(ToolsKit.isNotEmpty(mongoConnect.getHost()) && mongoConnect.getPort()>-1) {
                mongoNodeList.add(new ServerAddress(mongoConnect.getHost(), mongoConnect.getPort()));
                logger.warn("connect single mongodb host: " + mongoConnect.getHost()+"           port: "+ mongoConnect.getPort());
            }
        }

        if(ToolsKit.isEmpty(mongoNodeList)) {
            throw new MongodbException("connect mongdb, host and port is null or empty");
        }
        return mongoNodeList;
    }

    /**
     *  数据库鉴权, 仅支持SHA1方式加密
     *
     */
    private MongoCredential auth() {
        MongoCredential credential = null;
        if(ToolsKit.isNotEmpty(mongoConnect.getUsername()) && ToolsKit.isNotEmpty(mongoConnect.getPassword()) ) {
            credential = MongoCredential.createScramSha1Credential(
                    mongoConnect.getUsername(),
                    mongoConnect.getDatabase(),
                    mongoConnect.getPassword().toCharArray());
        }
        return credential;
    }


    public MongoClient createMongoDBClientWithURI() {
        MongoClientURI mongoClientURI = new MongoClientURI(mongoConnect.getUrl());
        //        logger.warn("mongodb connection url: " + connectionString);
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        if(null == mongoClient){
            throw new MongodbException("can't connect mongodb database! crate client fail");
        }
        // 设置链接的数据库
        mongoConnect.setDatabase(mongoClientURI.getDatabase());
        return mongoClient;
    }


    public static class Builder {
        private String host = "127.0.0.1";
        private int port = 27017;
        private String database = "local";
        private String username;
        private String password;
        private String url;
        private boolean isDefault;

        public Builder host(String host) {
            this.host = host;
            return this;
        }
        public Builder port(int port) {
            this.port = port;
            return this;
        }
        public Builder database(String database) {
            this.database = database;
            return this;
        }
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder isDefault(boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public MongoClientAdapter build() {
            MongoClientAdapter adapter =  ToolsKit.isEmpty(url) ? new MongoClientAdapter(new MongoConnect(host, port, database, username, password)) : new MongoClientAdapter(new MongoConnect(url));
            adapter.setDefaultClient(isDefault);
            return adapter;
        }
    }
}
