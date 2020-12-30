package com.duangframework.db.mongodb.convert.decode;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.duangframework.db.IdEntity;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.utils.DataType;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * @author Created by laotang
 * @date createed in 2018/4/20.
 */
public class MongodbDecodeValueFilter implements ValueFilter {
    @Override
    public Object process(Object object, String name, Object value) {
//        System.out.println(object+"           "+name+"           "+value+"      "+ value.getClass());
        if (IdEntity.ID_FIELD.equals(name) && ToolsKit.isNotEmpty(value)) {
            return (value instanceof ObjectId) ? ((ObjectId) value).toString() : value.toString();
        }
        if(DataType.isDate(value.getClass()) || DataType.isTimestamp(value.getClass())) {
            Date date = (Date)value;
            return ToolsKit.formatDate(date, ConstEnums.DEFAULT_DATE_FORMAT_VALUE.getValue());
        }
        // 处理Decimal128转化错误
        if (value.getClass().equals(org.bson.types.Decimal128.class) || value instanceof Decimal128){
            return ((Decimal128)value).toString();
        }
        return value;
    }
}
