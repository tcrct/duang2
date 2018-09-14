package com.duangframework.db.mysql.utils;

import com.duangframework.db.DbClientFactory;
import com.duangframework.db.IdEntity;
import com.duangframework.db.annotation.Index;
import com.duangframework.db.mysql.MysqlBaseDao;
import com.duangframework.db.mysql.MysqlDao;
import com.duangframework.db.mysql.client.MysqlClientAdapter;
import com.duangframework.db.mysql.core.DBSession;
import com.duangframework.exception.MysqlException;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by laotang on 2017/11/25 0025.
 */
public class MysqlUtils {

    private static final Logger logger = LoggerFactory.getLogger(MysqlUtils.class);

    private static Map<String, Set<String>> ALL_TABLES = new HashMap<>();
    private static String defualExampleCode;
    private static final Object[] NULL_OBJECT = new Object[0];
    private static Map<String, MysqlDao<?>> MYSQLDAO_MAP = new ConcurrentHashMap<>();

    public static String getDefualExampleCode() {
        return defualExampleCode;
    }

    /**
     *
     * @param queryResultList
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T toList(List<Map<String,Object>> queryResultList) {
        if(ToolsKit.isEmpty(queryResultList)){
            logger.warn("list is null, returu null...");
            return null;
        }
        List<T>resultList = new ArrayList<T>(queryResultList.size());
        for(Iterator<Map<String,Object>> it = queryResultList.iterator(); it.hasNext();){
            Map<String,Object> map = it.next();
            if(ToolsKit.isEmpty(map)) {
                continue;
            }
            for(Iterator<Map.Entry<String,Object>> mapIt = map.entrySet().iterator(); mapIt.hasNext();){
                Map.Entry<String,Object> entry = mapIt.next();
                if(ToolsKit.isNotEmpty(entry)){
                    resultList.add((T)entry.getValue());
                }
            }
        }
        return (T)resultList;
    }

    public static Connection getConnection(String key) throws Exception {
        if(DbClientFactory.getMongoDbClients().isEmpty()) {
          throw new MysqlException("请先启动MysqlPlugin插件");
        }
        MysqlClientAdapter clientAdapter = DbClientFactory.getMysqlDbClient(key);
        if(ToolsKit.isNotEmpty(clientAdapter) && ToolsKit.isNotEmpty(clientAdapter.getClient())) {
            return clientAdapter.getClient().getConnection();
        } else {
            throw new MysqlException("根据["+key+"]取数据库链接失败");
        }
    }

    /**
     * 取表索引
     * @param dataBase         数据库名
     * @param entityClass         entity类
     * @return
     */
    public static List<String> getIndexs(String dataBase, Class<?> entityClass) {
        String tableName = ClassKit.getEntityName(entityClass, true);
        List<String> indexList = DBSession.getIndexs(dataBase, tableName);
        return ToolsKit.isEmpty(indexList) ? null : indexList;
    }

    /**
     * 取出数据库所有表,加载到Map
     */
    public static void getAllTable(String database) throws Exception {
        List<String> list = DBSession.getMysqlTables(database);
        if(ToolsKit.isNotEmpty(list)){
            ALL_TABLES.get(database).addAll(list);
        }
    }

    /**
     * 是否存在表
     *
     * @param cls
     *            Entiey类
     * @return 存在返回true, 否则反之
     */
    public static boolean isExist(String database, Class<? extends IdEntity> cls) throws Exception {
        String tableName = ClassKit.getEntityName(cls,true);
        Set<String> tableNameSet = ALL_TABLES.get(database);
        if(ToolsKit.isEmpty(tableNameSet)) {
            logger.warn("get "+ database +" table is empty!" );
            return false;
        }
        return tableNameSet.contains(tableName);
    }

    /**
     * 创建表
     * @param databaseName
     * @param tableName
     * @param entityClass
     */
    public static void createTables(String databaseName, String tableName, Class<?> entityClass ) {

    }

    /**
     * 创建索引
     * @param databaseName
     * @param tableName
     * @param entityClass
     */
    public static void createIndexs(String databaseName, String tableName, Class<?> entityClass ) {
        // 先去查表里已经存在的索引
        List<String> indexs = MysqlUtils.getIndexs(databaseName, entityClass);
        Field[] fields = ClassKit.getFields(entityClass);
        if (ToolsKit.isEmpty(fields)) {
            return;
        }
        StringBuilder indexSql = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            Index index = fields[i].getAnnotation(Index.class);
            String columnName = ToolsKit.getFieldName(fields[i]);
            if (ToolsKit.isNotEmpty(index)) {
                indexSql.delete(0,indexSql.length());
                String indexName = ToolsKit.isEmpty(index.name()) ? "_" + columnName + "_" : index.name();
                indexName = indexName.toLowerCase();
                //如果不存在则添加，存在则不作任何处理
                if(ToolsKit.isNotEmpty(indexs) && !indexs.contains(indexName)){
                    boolean unique = index.unique();
                    String order = ToolsKit.isEmpty(index.order()) ? "asc" : index.order();
                    indexSql.append("create ");
                    if(unique) {
                        indexSql.append("unique ");
                    }
                    indexSql.append(" index ").append(indexName).append(" on ").append(tableName).append("(").append(columnName);
                    if("desc".equalsIgnoreCase(order)){
                        indexSql.append(" ").append(order);
                    }
                    indexSql.append(");");
                    try {
                        System.out.println("indexSql: " + indexSql.toString());
//                        DBSession.execute(databaseName, indexSql.toString(), NULL_OBJECT);
                    } catch (Exception e) {
                        logger.warn("create["+databaseName+"."+tableName+"."+columnName+"] index["+indexName+"] is fail: " + e.getMessage(), e);
                    }
                }
            }
        }
    }



    /**
     * 根据Entity类取出MongoDao
     * @param clientId      客户端实例ID
     * @param cls               继承了IdEntity的类
     * @param <T>
     * @return
     */
    public static <T> MysqlDao<T> getMysqlDao(String clientId, Class<T> cls){
        String key = ClassKit.getEntityName(cls);
        key = ToolsKit.isNotEmpty(clientId) ? clientId+"_" + key : key;
        MysqlDao<?> dao = MYSQLDAO_MAP.get(key);
        if(null == dao){
            try {
                MysqlClientAdapter mysqlClient = DbClientFactory.getMysqlDbClient(clientId);
                dao = new MysqlDao<T>(mysqlClient, cls);
                MYSQLDAO_MAP.put(key, dao);
            } catch (Exception e) {
                throw new MysqlException("mysql client  is null");
            }
        }
        return (MysqlDao<T>)dao;
    }

//    public static CurdSqlModle builderSqlModle(CurdEnum curdEnum, Class<?> entityClass, Map<String, Object> paramMap, String idFieldName) {
//
//        String tableName = ClassUtils.getEntityName(entityClass);
//        CurdSqlModle modle = null;
//        if(ToolsKit.isNotEmpty(tableName) && ToolsKit.isNotEmpty(paramMap)) {
//            modle = new CurdSqlModle(curdEnum, tableName, paramMap, idFieldName);
//        }
//        return modle;
//    }

    /**
     * 排序
     */
    public static List<String> orderParamKey(Map<String,?> paramMap) {
        if (ToolsKit.isEmpty(paramMap)) {
            return null;
        }
        ArrayList<String> keyList = new ArrayList<>(paramMap.keySet());
        Collections.sort(keyList, String.CASE_INSENSITIVE_ORDER);
        return keyList;
    }
}
