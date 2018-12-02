package com.duangframework.utils;

import com.duangframework.db.IdEntity;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Bean;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.Date;

@SuppressWarnings("rawtypes")
public final class DataType {
    
	public static boolean isString(Class type){
        return type.equals(String.class);
    }
    
    public static boolean isInteger(Class type){
        return type.equals(int.class);
    }
    
    public static boolean isIntegerObject(Class type){
        return type.equals(Integer.class);
    }
    
    public static boolean isLong(Class type){
        return type.equals(long.class);
    }
    
    public static boolean isLongObject(Class type){
        return type.equals(Long.class);
    }
    
    public static boolean isShort(Class type){
        return type.equals(short.class);
    }
    
    public static boolean isShortObject(Class type){
        return type.equals(Short.class);
    }
    
    public static boolean isByte(Class type){
        return type.equals(byte.class);
    }
    
    public static boolean isByteObject(Class type){
        return type.equals(Byte.class);
    }
    
    public static boolean isFloat(Class type){
        return type.equals(float.class);
    }
    
    public static boolean isFloatObject(Class type){
        return type.equals(Float.class);
    }
    
    public static boolean isDouble(Class type){
        return type.equals(double.class);
    }
    
    public static boolean isDoubleObject(Class type){
        return type.equals(Double.class);
    }
    
    public static boolean isBoolean(Class type){
        return type.equals(boolean.class);
    }
    
    public static boolean isBooleanObject(Class type){
        return type.equals(Boolean.class);
    }
    
    public static boolean isChar(Class type){
        return type.equals(char.class);
    }
    
    public static boolean isCharObject(Class type){
        return type.equals(Character.class);
    }
    
    public static boolean isDate(Class type){
        return type.equals(java.util.Date.class);
    }
    
    public static boolean isTimestamp(Class type){
        return type.equals(java.sql.Timestamp.class);
    }

    public static boolean isArray(Class type){
        return type.isArray();
    }
    
    /**
     * 判断是否为List集合
     * @param type
     * @return 
     */
    public static boolean isListType(Class type){
        boolean isList = type.equals(java.util.List.class) || type.equals(java.util.ArrayList.class);
        if(!isList){
        	try{
        		isList = type.newInstance()  instanceof java.util.List;
        	}catch(Exception e){
        		return false;
        	}
        }
        return isList;
    }
    
    /**
     * 判断是否为Set集合
     * @param type
     * @return 
     */
    public static boolean isSetType(Class type){
        boolean isSet =  type.equals(java.util.Set.class) || type.equals(java.util.HashSet.class);
        if(!isSet){
        	try{
        		isSet = type.newInstance()  instanceof java.util.Set;
        	}catch(Exception e){
        		return false;
        	}
        }
        return isSet;
    }
    
    /**
     * 判断是否为Map集合
     * @param type
     * @return 
     */
    public static boolean isMapType(Class type){
        boolean isMap = type.equals(java.util.Map.class) || type.equals(java.util.HashMap.class);
        if(!isMap){
        	try {
        		isMap = type.newInstance() instanceof java.util.Map;
			} catch (Exception e) {
				return false;
			}
        }
        return isMap;
    }
    
    /**
     * Queue, Deque and LinkedList is supported now.
     * @param type
     * @return 
     */
    public static boolean isQueueType(Class type){
        boolean isQueue =  type.equals(java.util.Queue.class) || type.equals(java.util.Deque.class) || type.equals(java.util.LinkedList.class);
        if(!isQueue){
        	try {
        		isQueue = type.newInstance() instanceof java.util.Queue;
			} catch (Exception e) {
				return false;
			}
        }
        return isQueue;
    }

    /**
     * IdEntity
     * @param type
     * @return
     */
    public static boolean isIdEntityType(Class type){
        boolean isIdEntity =  type.equals(IdEntity.class);
        if(!isIdEntity){
            try {
                isIdEntity = type.newInstance() instanceof IdEntity;
            } catch (Exception e) {
                return false;
            }
        }
        return isIdEntity;
    }

    public static boolean isBeanType(Class type){
        return DataType.isIdEntityType(type)
                ||  type.isAnnotationPresent(Bean.class);
    }

    /**
     * 是否基础类型
     * @param type
     * @return
     */
    public static boolean isBaseType(Class<?> type) {
		if (DataType.isString(type) 
				|| DataType.isInteger(type) || DataType.isIntegerObject(type)
				|| DataType.isLong(type) || DataType.isLongObject(type) 
				|| DataType.isDouble(type) || DataType.isDoubleObject(type) 
				|| DataType.isFloat(type) || DataType.isFloatObject(type)
				|| DataType.isShort(type) || DataType.isShortObject(type) 
				|| DataType.isByte(type) || DataType.isByteObject(type) 
				|| DataType.isBoolean(type) || DataType.isBooleanObject(type)
				|| DataType.isChar(type) || DataType.isCharObject(type) 
				|| DataType.isDate(type)
				|| DataType.isTimestamp(type)) {
			return true;
		}
		return false;
	}

    /**
     * 是否基础类型
     * @param type
     * @return
     */
    public static Class<?> conversionBaseType(String typeString) {
        if(typeString.contains(".")) {
            typeString = typeString.substring(typeString.lastIndexOf("."), typeString.length());
        }
        if(String.class.getSimpleName().equalsIgnoreCase(typeString)) {
            return String.class;
        }else if(Integer.class.getSimpleName().equalsIgnoreCase(typeString) || int.class.getSimpleName().equalsIgnoreCase(typeString)){
            return Integer.class;
        }else if(Long.class.getSimpleName().equalsIgnoreCase(typeString) || long.class.getSimpleName().equalsIgnoreCase(typeString)) {
            return Long.class;
        }else if (Double.class.getSimpleName().equalsIgnoreCase(typeString) || double.class.getSimpleName().equalsIgnoreCase(typeString)) {
            return Double.class;
        }else if(Float.class.getSimpleName().equalsIgnoreCase(typeString) || float.class.getSimpleName().equalsIgnoreCase(typeString)){
            return Float.class;
        } else if( Short.class.getSimpleName().equalsIgnoreCase(typeString) || short.class.getSimpleName().equalsIgnoreCase(typeString)) {
            return Short.class;
        } else if ( Byte.class.getSimpleName().equalsIgnoreCase(typeString) || byte.class.getSimpleName().equalsIgnoreCase(typeString)) {
            return Byte.class;
        } else if(Boolean.class.getSimpleName().equalsIgnoreCase(typeString) || boolean.class.getSimpleName().equalsIgnoreCase(typeString)) {
            return Boolean.class;
        } else if(Character.class.getSimpleName().equalsIgnoreCase(typeString) || char.class.getSimpleName().equalsIgnoreCase(typeString)) {
            return Character.class;
        } else if (Date.class.getSimpleName().equalsIgnoreCase(typeString) ){
            return Date.class;
        } else if (Timestamp.class.getSimpleName().equalsIgnoreCase(typeString)) {
            return Timestamp.class;
        }
        return null;
    }

    /**
     * 将可变参数确定类型返回
     * 如果int[]这种参数的话，在Object...里，会变成[[1,2,3]]，将int[]将Object数组里的第一个元素处理的。所以要处理一下
     * 如果是包装类的数据，即Integer[]则不会出现该问题
     * @param value
     * @return
     */
    public static Object conversionVariableType(Object value) {
        if (value != null && (value instanceof Object[])) {
            Object[] valueArray= (Object[])value;
            if(null != valueArray && valueArray.length ==1) {
                Object valueItem = valueArray[0];
                if (valueItem != null && valueItem.getClass().isArray()) {
                    int arrayLength = Array.getLength(valueItem);
                    Object[] objects = new Object[arrayLength];
                    for (int i = 0; i < arrayLength; ++i) {
                        objects[i] = Array.get(valueItem, i);
                    }
                    return objects;
                }
            }
        }
        return value;
    }
    
}
