package com.duangframework.db.mysql.core;

import com.duangframework.db.IdEntity;
import com.duangframework.db.annotation.Id;
import com.duangframework.db.annotation.Vo;
import com.duangframework.db.annotation.VoColl;
import com.duangframework.db.mysql.client.MysqlClientAdapter;
import com.duangframework.db.mysql.common.options.CreateCollectionOptions;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.utils.DataType;
import com.duangframework.vtor.annotation.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by laotang on 2018/12/2.
 */
public class DB {

    private static final Logger logger = LoggerFactory.getLogger(DB.class);
    private static final Object[] NULL_OBJECT = new Object[0];
    private final ConcurrentHashMap<String, DBCollection> collectionCache;
    private final Set<String> collectionNameCache;
    private MysqlClientAdapter clientAdapter;

    public DB(MysqlClientAdapter clientAdapter) {
        this.clientAdapter = clientAdapter;
        this.collectionCache = new ConcurrentHashMap();
        this.collectionNameCache = new LinkedHashSet<>();
        getCollectionNames();
    }

    public DBCollection getCollection(String name) {
        DBCollection collection = collectionCache.get(name);
        if (collection != null) {
            return collection;
        }
        collection = new DBCollection(name, this);
        DBCollection old = collectionCache.putIfAbsent(name, collection);
        return old != null ? old : collection;
    }

    public String getId() {
        return clientAdapter.getId();
    }

    public String getName() {
        return clientAdapter.getDbName();
    }

    public boolean collectionExists(String collectionName) {
        return collectionNameCache.contains(collectionName);
    }

    public void dropDatabase() {

    }

    public Set<String> getCollectionNames() {
        try {
            if (!collectionNameCache.isEmpty()) {
                return collectionNameCache;
            }
            List<String> collectionNames = DBSession.getMysqlTables(clientAdapter.getId());
            if (ToolsKit.isNotEmpty(collectionNames)) {
                collectionNameCache.clear();
                collectionNameCache.addAll(collectionNames);
            }
            return collectionNameCache;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 创建表
     *
     * @param collectionName
     * @param options
     * @return
     */
    public synchronized DBCollection createCollection(Class<?> cls, CreateCollectionOptions options) {
        String collectionName = ClassKit.getEntityName(cls);
        if (collectionExists(collectionName)) {
            return getCollection(collectionName);
        }
        Field[] fields = ClassKit.getFields(cls);
        StringBuilder columnString = new StringBuilder();
        columnString.append(IdEntity.ID_FIELD).append(" varchar(50) primary key,");
        for (Field field : fields) {
            String name = field.getName();
            if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(Vo.class) || field.isAnnotationPresent(VoColl.class))
                continue;
            Class<?> typeClass = field.getType();
            String sqlType = convertToType(typeClass, field);
            if (ToolsKit.isEmpty(sqlType)) continue;
            columnString.append(name).append(" ").append(sqlType).append(",");
        }
        if (columnString.length() > 0) columnString.deleteCharAt(columnString.length() - 1);
        StringBuilder createTabelSql = new StringBuilder("create table ");
        createTabelSql.append(collectionName).append(" (").append(columnString).append(")");
        createTabelSql.append(" engine=").append(options.getEngine())
                .append(" default charset=").append(options.getCharset())
                .append(" collate=").append(options.getCollate()).append(";");
        try {
            int row = DBSession.execute(clientAdapter.getId(), createTabelSql.toString(), NULL_OBJECT);
            if (row >= 0) {
                logger.info("create " + collectionName + " table success... ");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return this.getCollection(collectionName);
    }

    private String convertToType(Class<?> type, Field field) {
        String sqlType = "";
        Length vtor = field.getAnnotation(Length.class);
        int len = 0;
        if (null != vtor) {
            len = vtor.value();
        }
        if (DataType.isString(type)) {
            sqlType = "varchar(" + ((len > 0) ? len : 50) + ")";
        } else if (DataType.isInteger(type) || DataType.isIntegerObject(type)) {
            sqlType = "int(" + ((len > 0) ? len : 7) + ")";
        } else if (DataType.isLong(type) || DataType.isLongObject(type)) {
            sqlType = "bigint(" + ((len > 0) ? len : 11) + ")";
        } else if (DataType.isDate(type)) {
            sqlType = "datetime";
        }
        return sqlType.toLowerCase().trim();
    }
}
