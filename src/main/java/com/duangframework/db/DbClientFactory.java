package com.duangframework.db;

import com.duangframework.db.mongodb.client.MongoClientAdapter;
import com.duangframework.db.mysql.client.MysqlClientAdapter;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.proxy.IProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Created by laotang
 * @date createed in 2018/6/26.
 */
public class DbClientFactory {


    private static List<IProxy> proxyList = new ArrayList<>();
    /**
     * 所有实例客户端的Pool，注意KEY的命令，要确保唯一性
     */
    private static ConcurrentMap<String, MongoClientAdapter> MONGODB_CLIENT_MAP = new ConcurrentHashMap<>();
    private static String DEFAULT_MONGODB_ID;
    /************************************************************************/
    /************************  MONGODB *********************************/
    /***********************************************************************/
    /**
     * 所有实例客户端的Pool，注意KEY的命令，要确保唯一性
     */
    private static ConcurrentMap<String, MysqlClientAdapter> MYSQL_CLIENT_MAP = new ConcurrentHashMap<>();
    private static String DEFAULT_MYSQL_ID;

    public static List<IProxy> getProxyList() {
        return proxyList;
    }

    public static void setProxyList(List<IProxy> proxyList) {
        DbClientFactory.proxyList = proxyList;
    }

    /**
     * 添加Mongodb数据库客户端到缓存池
     *
     * @param dbClient
     */
    public static void setMongoClient(MongoClientAdapter clientAdapter) {
        if (ToolsKit.isEmpty(clientAdapter)) {
            throw new NullPointerException("db client adapter  is null");
        }
        String dbClientId = clientAdapter.getId();
        if (!MONGODB_CLIENT_MAP.containsKey(dbClientId)) {
            MONGODB_CLIENT_MAP.put(dbClientId, clientAdapter);
        }
    }

    /**
     * 取MongoDB默认的ClientId<br/>
     * 如果没有设置默认值，则以第一个实例作默认客户端
     *
     * @return
     */
    public static String getMongoDefaultClientId() {
        return DEFAULT_MONGODB_ID;
    }

    public static void setMongoDefaultClientId(String defaultClientId) {
        DEFAULT_MONGODB_ID = defaultClientId;
    }

    /************************************************************************/
    /************************ MYSQL***************************************/
    /***********************************************************************/

    /**
     * 根据名称返回客户端实例
     *
     * @param dbClientId 保存在缓存池里的客户端的id
     * @return
     */
    public static MongoClientAdapter getMongoDbClient(String dbClientId) throws Exception {
        return MONGODB_CLIENT_MAP.get(dbClientId);
    }

    public static Map<String, MongoClientAdapter> getMongoDbClients() throws Exception {
        return MONGODB_CLIENT_MAP;
    }

    /**
     * 添加Mysql数据库客户端到缓存池
     *
     * @param dbClient
     */
    public static void setMysqlClient(MysqlClientAdapter clientAdapter) {
        if (ToolsKit.isEmpty(clientAdapter)) {
            throw new NullPointerException("db client adapter  is null");
        }
        String dbClientId = clientAdapter.getId();
        if (!MYSQL_CLIENT_MAP.containsKey(dbClientId)) {
            MYSQL_CLIENT_MAP.put(dbClientId, clientAdapter);
        }
    }

    /**
     * 取Mysql默认的ClientId<br/>
     * 如果没有设置默认值，则以第一个实例作默认客户端
     *
     * @return
     */
    public static String getMysqlDefaultClientId() {
        return DEFAULT_MYSQL_ID;
    }

    public static void setMysqlDefaultClientId(String defaultClientId) {
        DEFAULT_MYSQL_ID = defaultClientId;
    }

    /**
     * 根据名称返回客户端实例
     *
     * @param dbClientId 保存在缓存池里的客户端的id
     * @return
     */
    public static MysqlClientAdapter getMysqlDbClient(String dbClientId) throws Exception {
        return MYSQL_CLIENT_MAP.get(dbClientId);
    }

    public static Map<String, MysqlClientAdapter> getMysqlDbClients() throws Exception {
        return MYSQL_CLIENT_MAP;
    }
}
