package com.duangframework.db.mysql.convert.encoder;

import com.duangframework.db.IdEntity;
import com.duangframework.db.annotation.Id;
import com.duangframework.db.mysql.core.DBObject;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.ObjectKit;
import com.duangframework.kit.ToolsKit;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

public abstract class Encoder {

    public static final String EMPTY_SPACE= " ";

    public static final String INSERT_FIELD =  "insert into";
    public static final String UPDATE_FIELD = "update";
    public static final String SET_FIELD = "set";
    public static final String VALUES_FIELD = "values";
    public static final String WHERE_FIELD ="where";
    public static final String DELETE_FILED = "delete";

    protected String key;
    protected Object value;
    protected DBObject dbObject;
    protected LinkedHashMap<String, Object> kvMap = new java.util.LinkedHashMap<>();

    public Encoder(DBObject dbObject){
        this.dbObject = dbObject;
        init();
    }

    public Encoder(String key, Object value){
        this.key = key;
        this.value = value;
        init();
    }

    private void init() {
        Object entity = dbObject.getEntity();
        Field[] fields = ClassKit.getFields(entity.getClass());
        for(Field field : fields) {
            Object value = ObjectKit.getFieldValue(entity, field);
            if(ToolsKit.isEmpty(value)) {
                continue;
            }
            String fieldName = ToolsKit.getFieldName(field);
//            // 如果有@Id注解
//            if(null != field.getAnnotation(Id.class)) {
//                fieldName = IdEntity.ENTITY_ID_FIELD;
//            }
            kvMap.put(fieldName, value);
        }
    }

    public boolean isNull(){
        return ToolsKit.isEmpty(kvMap) ;
    }

    protected String getTableName() {
        return ClassKit.getEntityName(dbObject.getEntity().getClass());
    }


    public abstract String getSql();

    public abstract Object[] getParams();
	
}
