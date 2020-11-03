package com.duangframework.db.mongodb.convert.encode;

import com.alibaba.fastjson.serializer.BeanContext;
import com.alibaba.fastjson.serializer.ContextValueFilter;
import com.duangframework.db.IdEntity;
import com.duangframework.utils.DataType;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * @author Created by laotang
 * @date createed in 2018/4/20.
 */
public class MongodbEncodeValueFilter implements ContextValueFilter {
    @Override
    public Object process(BeanContext context, Object object, String name, Object value) {
//        if(object != null && null != value) {
//            System.out.println(object + "           " + name + "           " + value + "      " + value.getClass());
//        }
        if (null == value) {
            return value;
        }
        Class type = value.getClass();
        if (DataType.isString(type) &&
                (IdEntity.ID_FIELD.equals(name) || IdEntity.ENTITY_ID_FIELD.equals(name))) {
            String idString = (String) value;
            if (ObjectId.isValid(idString)) {
                return new ObjectId(idString);
            }
            return idString;
        } else if (DataType.isShort(type) || DataType.isShortObject(type)) {
            return Short.parseShort(value.toString());
        } else if (DataType.isInteger(type) || DataType.isIntegerObject(type)) {
            return Integer.parseInt(value.toString());
        } else if (DataType.isLong(type) || DataType.isLongObject(type)) {
            return Long.parseLong(value.toString());
        } else if (DataType.isDouble(type) || DataType.isDoubleObject(type)) {
            return Double.parseDouble(value.toString());
        } else if (DataType.isDate(value.getClass()) || DataType.isTimestamp(value.getClass())) {
            return (Date) value;
        }
        return value;
    }

}
