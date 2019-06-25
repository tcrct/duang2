package com.duangframework.utils;

import com.alibaba.fastjson.JSONArray;
import com.duangframework.kit.ToolsKit;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

/**
 * Created by laotang on 2019/1/8.
 */
public class TypeConverter {

    public static final Object convert(Class<?> type, Object objValue) throws ParseException {
        Object result = null;
        if (DataType.isString(type)) {
            result = objValue.toString();
        } else if (DataType.isInteger(type)) {
            String tmpValue = objValue.toString();
            int index = tmpValue.indexOf(".");
            if(index > -1){ tmpValue= objValue.toString().substring(0,index);}
            result =  Integer.parseInt(tmpValue);
        } else if (DataType.isIntegerObject(type)) {
            String tmpValue = objValue.toString();
            int index = tmpValue.indexOf(".");
            if(index > -1){ tmpValue= objValue.toString().substring(0,index);}
            result =  Integer.valueOf(tmpValue);
        } else if (DataType.isLong(type)) {
            result =  Long.parseLong(objValue.toString());
        } else if (DataType.isLongObject(type)) {
            result =  Long.valueOf(objValue.toString());
        }else if (DataType.isDouble(type)) {
            result =  Double.parseDouble(objValue.toString());
        } else if (DataType.isDoubleObject(type)) {
            result =  Double.valueOf(objValue.toString());
        } else if (DataType.isFloat(type)) {
            result =  Float.parseFloat(objValue.toString());
        } else if (DataType.isFloatObject(type)) {
            result =  Float.valueOf(objValue.toString());
        } else if (DataType.isShort(type)) {
            result =  Short.parseShort(objValue.toString());
        } else if (DataType.isShortObject(type)) {
            result =  Short.valueOf(objValue.toString());
        } else if (DataType.isBoolean(type)) {
            result =  Boolean.parseBoolean(objValue.toString());
        } else if (DataType.isBooleanObject(type)) {
            result =  Boolean.valueOf(objValue.toString());
        } else if (DataType.isChar(type)) {
            result =  objValue.toString().toCharArray();
        } else if (DataType.isCharObject(type)) {
            result =  objValue.toString().toCharArray();
        } else if (DataType.isArray(type)) {
            result =  objValue;
        } else if (DataType.isListType(type)) {
            List list = null;
            if(objValue instanceof JSONArray) {
                String jsonString = ((JSONArray)objValue).toJSONString();
                list = ToolsKit.jsonParseArray(jsonString, List.class);
            } else {
                list = (ArrayList) objValue;
            }
            result =  list;
        } else if (DataType.isSetType(type)) {
            List list = (ArrayList) objValue;
            result =  new HashSet(list);
        } else if (DataType.isMapType(type)) {
            Map map = (HashMap) objValue;
            result =  map;
        } else if (DataType.isQueueType(type)) {
            List list = (ArrayList) objValue;
            result =  new LinkedList(list);
        } else if (DataType.isDate(type)) {
            Date date = null;
            try{
                date = (Date) objValue;
            }catch(Exception e){
                String stringDate = (String)objValue;
                try{
                    date = ToolsKit.parseDate(stringDate, "yyyy-MM-dd HH:mm:ss");
                } catch(Exception e1) {
                    try{
                        date = ToolsKit.parseDate(stringDate, "yyyy-MM-dd HH:mm:ss.SSS");
                    } catch(Exception e2) {
                        date = new Date();
                        date.setTime(Long.parseLong(stringDate));
                    }
                }
            }
            if(null != date){
                result =  date;
            }
        } else if (DataType.isTimestamp(type)) {
            Date date = (Date) objValue;
            result =  new Timestamp(date.getTime());
        } else {
            result =  objValue; // for others
        }
        return result;
    }
}
