package com.duangframework.cache.ds;

import com.duangframework.db.DBConnect;
import com.duangframework.db.IClient;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.utils.MD5;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

/**
 * @author Created by laotang
 * @date createed in 2018/7/5.
 */
public class RedisAdapter extends AbstractCacheSource<JedisPool> implements IClient<JedisPool> {

    private String id;
    private int database;
    private String host;
    private String username;
    private String password;
    private int port;
    private int timeout;
    private String url;
    private RedisConnect redisConnect;

    private RedisAdapter(String id, int database, String host, String username, String password, int port, int timeout, String url) {
        this.id = id;
        this.database = database;
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.timeout = timeout;
        this.url = url;
        redisConnect = new RedisConnect(host, port, database+"", username, password, url);
    }

    public static class Builder {
        private int database;
        private String host;
        private String username;
        private String password;
        private int port;
        private String url;
        private int timeout;
        private String id;

        public Builder() {

        }

        public Builder database(int database) {
            this.database = database;
            return this;
        }
        public Builder host(String host) {
            this.host = host;
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
        public Builder port(int port) {
            this.port = port;
            return this;
        }
        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }
        public Builder url(String url) {
            this.url = url;
            return this;
        }
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public RedisAdapter build() {
            return new RedisAdapter(id, database, host, username, password, port, timeout, url);
        }
    }



    @Override
    protected JedisPool builderDataSource() {
        JedisPool pool = null;
        // 建立连接池配置参数
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxIdle(100);
        config.setMinIdle(10);
        config.setMaxTotal(100);
        config.setMaxWaitMillis(5000);
        config.setTestWhileIdle(false);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(false);
        config.setNumTestsPerEvictionRun(10);
        config.setMinEvictableIdleTimeMillis(1000);
        config.setSoftMinEvictableIdleTimeMillis(10);
        config.setTimeBetweenEvictionRunsMillis(10);
        config.setLifo(false);
        // 创建连接池
        try{
            database = ToolsKit.isEmpty(database) ? PropKit.getInt(ConstEnums.PROPERTIES.REDIS_DATABASE.getValue(),0) : database;
            host = ToolsKit.isEmpty(database) ? PropKit.get(ConstEnums.PROPERTIES.REDIS_HOST.getValue(),"127.0.0.1") : host;
            password =ToolsKit.isEmpty(database) ? PropKit.get(ConstEnums.PROPERTIES.REDIS_PWD.getValue(),"") : password;
            port = ToolsKit.isEmpty(database) ? PropKit.getInt(ConstEnums.PROPERTIES.REDIS_PORT.getValue(),6371) : port;
            timeout = ToolsKit.isEmpty(database) ? PropKit.getInt(ConstEnums.PROPERTIES.REDIS_PORT.getValue(),2000) : timeout;
            if(ToolsKit.isEmpty(password)) {
                if(host.contains(":")) {
                    String[] hostArray = host.split(":");
                    if(ToolsKit.isNotEmpty(hostArray)) {
                        try{
                            host = hostArray[0];
                            port = Integer.parseInt(hostArray[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                pool = new JedisPool(config, host, port, timeout);
            } else {
                pool = new JedisPool(config, host, port, timeout, password, database);
            }
            System.out.println("Connent  " + host + ":"+port +" Redis is Success...");
            return pool;
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisException(e.getMessage(), e);
        }
    }

    @Override
    public String getId() {
        return ToolsKit.isEmpty(id) ? MD5.MD5Encode(toString()) : id;
    }

    @Override
    public DBConnect getDbConnect() {
        return redisConnect;
    }

    @Override
    public JedisPool getClient() throws Exception {
        return getSource();
    }

    @Override
    public void close() throws Exception {
        getClient().close();
    }

    @Override
    public String toString() {
        return "RedisAdapter{" +
                "database=" + database +
                ", host='" + host + '\'' +
                ", password='" + password + '\'' +
                ", port=" + port +
                ", timeout=" + timeout +
                '}';
    }
}
