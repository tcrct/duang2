package com.duangframework.db.mysql.client;

import com.duangframework.db.IClient;
import com.duangframework.db.mysql.core.ds.DruidDataSourceFactory;
import com.duangframework.db.mysql.core.ds.IDataSourceFactory;
import com.duangframework.exception.MongodbException;
import com.duangframework.exception.MysqlException;
import com.duangframework.kit.ObjectKit;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.utils.MD5;
import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/6/26.
 */
public class MysqlClientAdapter implements IClient<DataSource> {

    private static final Logger logger = LoggerFactory.getLogger(MysqlClientAdapter.class);

    private MysqlConnect mysqlConnect;
    private DataSource dataSource;

    private boolean isDefaultClient;
    /**
     * 链接客户端ID, 如多实例里，用于区分
     * 生成规则， MD5(this.toString())
     */
    private String id;

    public MysqlClientAdapter(MysqlConnect mysqlConnect) {
        this.mysqlConnect = mysqlConnect;
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
        if (ToolsKit.isEmpty(id) && ToolsKit.isNotEmpty(mysqlConnect)) {
            return MD5.MD5Encode(mysqlConnect.toString());
        }
        return id;
    }

    @Override
    public MysqlConnect getDbConnect() {
        return mysqlConnect;
    }

    @Override
    public DataSource getClient() throws Exception {
        if(null == dataSource) {
            IDataSourceFactory dsFactory = null;
            try {
                String dataSourceFactoryClassName = PropKit.get(ConstEnums.PROPERTIES.MYSQL_DATASOURCE.getValue());
                if (ToolsKit.isEmpty(dataSourceFactoryClassName)) {
                    dsFactory = ObjectKit.newInstance(DruidDataSourceFactory.class);
                } else {
                    dsFactory = ObjectKit.newInstance(dataSourceFactoryClassName);
                }
            } catch (Exception e) {
                throw new MysqlException("Can't connect mysql: " + e.getMessage(), e);
            }
            dataSource = dsFactory.getDataSource(mysqlConnect);
        }
        return dataSource;
    }

    @Override
    public void close() {
        if (null != dataSource) {
            try {
                dataSource.getConnection().close();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
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
            this.host = password;
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

        public MysqlClientAdapter build() {
            MysqlClientAdapter adapter = ToolsKit.isEmpty(url) ? new MysqlClientAdapter(new MysqlConnect(host, port, database, username, password)) : new MysqlClientAdapter(new MysqlConnect(url));
            adapter.setDefaultClient(isDefault);
            return adapter;
        }
    }
}
