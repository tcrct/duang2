package com.duangframework.kit;

import com.duangframework.utils.DataType;
import com.duangframework.utils.TypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.*;

/**
 * 对象操作工具类
 *
 * @author laotang
 * @since 1.0
 */
public class ObjectKit {

    private static final Logger logger = LoggerFactory.getLogger(ObjectKit.class);
    private static final Map<String, Map<String, Object>> FIELD_MAP = new HashMap<>();
    private static final Set<String> excludedMethodName = new HashSet<>();
    /**
     * 设置成员变量
     * @param obj       需要设置成员变量的对象
     * @param field     需要设置值的变量字段
     * @param fieldValue    变量字段的值
     * @param type              变量字段类型
     * @throws Exception
     */
    public static void setField(Object obj, Field field, Object fieldValue, Class<?> type) throws Exception {
        //如果为null,直接退出
        if( null ==fieldValue) {
            return;
        }
        field.setAccessible(true);
        field.set(obj, TypeConverter.convert(type, fieldValue));
        /*
        if (DataType.isString(type)) {
            field.set(obj, fieldValue.toString());
        } else if (DataType.isInteger(type)) {
            String tmpValue = fieldValue.toString();
            int index = tmpValue.indexOf(".");
            if(index > -1){ tmpValue= fieldValue.toString().substring(0,index);}
            field.set(obj, Integer.parseInt(tmpValue));
        } else if (DataType.isIntegerObject(type)) {
            String tmpValue = fieldValue.toString();
            int index = tmpValue.indexOf(".");
            if(index > -1){ tmpValue= fieldValue.toString().substring(0,index);}
            field.set(obj, Integer.valueOf(tmpValue));
        } else if (DataType.isLong(type)) {
            field.set(obj, Long.parseLong(fieldValue.toString()));
        } else if (DataType.isLongObject(type)) {
            field.set(obj, Long.valueOf(fieldValue.toString()));
        }else if (DataType.isDouble(type)) {
            field.set(obj, Double.parseDouble(fieldValue.toString()));
        } else if (DataType.isDoubleObject(type)) {
            field.set(obj, Double.valueOf(fieldValue.toString()));
        } else if (DataType.isFloat(type)) {
            field.setFloat(obj, Float.parseFloat(fieldValue.toString()));
        } else if (DataType.isFloatObject(type)) {
            field.set(obj, Float.valueOf(fieldValue.toString()));
        } else if (DataType.isShort(type)) {
            field.setShort(obj, Short.parseShort(fieldValue.toString()));
        } else if (DataType.isShortObject(type)) {
            field.set(obj, Short.valueOf(fieldValue.toString()));
        } else if (DataType.isBoolean(type)) {
            field.set(obj, Boolean.parseBoolean(fieldValue.toString()));
        } else if (DataType.isBooleanObject(type)) {
            field.set(obj, Boolean.valueOf(fieldValue.toString()));
        } else if (DataType.isChar(type)) {
            field.set(obj, fieldValue.toString().toCharArray());
        } else if (DataType.isCharObject(type)) {
            field.set(obj, fieldValue.toString().toCharArray());
        } else if (DataType.isArray(type)) {
            field.set(obj, fieldValue);
        } else if (DataType.isListType(type)) {
            List list = (ArrayList) fieldValue;
            field.set(obj, list);
        } else if (DataType.isSetType(type)) {
            List list = (ArrayList) fieldValue;
            field.set(obj, new HashSet(list));
        } else if (DataType.isMapType(type)) {
            Map map = (HashMap) fieldValue;
            field.set(obj, map);
        } else if (DataType.isQueueType(type)) {
            List list = (ArrayList) fieldValue;
            field.set(obj, new LinkedList(list));
        } else if (DataType.isDate(type)) {
            Date date = null;
            try{
                date = (Date) fieldValue;
            }catch(Exception e){
                String stringDate = (String)fieldValue;
                try{
                    date = ToolsKit.parseDate(stringDate, "yyyy-MM-dd HH:mm:ss.SSS");
                } catch(Exception e1) {
                    date = new Date();
                    date.setTime(Long.parseLong(stringDate));
                }
            }
            if(null != date){
                field.set(obj, date);
            }
        } else if (DataType.isTimestamp(type)) {
            Date date = (Date) fieldValue;
            field.set(obj, new Timestamp(date.getTime()));
        } else {
            field.set(obj, fieldValue); // for others
        }
        */
    }

    /**
     * 设置成员变量
     * @param obj       需要设置成员变量的对象
     * @param field     需要设置值的变量字段
     * @param fieldValue    变量字段的值
     */
    public static void setField(Object obj, Field field, Object fieldValue) {
        try {
            field.setAccessible(true);
            field.set(obj, fieldValue);
        } catch (Exception e) {
            logger.error("设置成员变量出错！", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取成员变量
     * @param  obj 对象
     * @aram field  变量字段
     */
    public static Object getFieldValue(Object obj, Field field) {
        Object propertyValue = null;
        try {
            field.setAccessible(true);
            propertyValue = field.get(obj);
        } catch (Exception e) {
            logger.error("获取成员变量出错！", e);
            throw new RuntimeException(e);
        }
        return propertyValue;
    }

    /**
     * 复制所有成员变量
     */
    public static void copyFields(Object source, Object target) {
        try {
            for (Field field : source.getClass().getDeclaredFields()) {
                // 若不为 static 成员变量，则进行复制操作
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true); // 可操作私有成员变量
                    field.set(target, field.get(source));
                }
            }
        } catch (Exception e) {
            logger.error("复制成员变量出错！", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过反射创建实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className) {
        T instance;
        try {
            Class<?> commandClass = Class.forName(className);
            instance = (T) commandClass.newInstance();
        } catch (Exception e) {
            logger.error("创建实例出错！", e);
            throw new RuntimeException(e);
        }
        return instance;
    }

    /**
     * 通过反射创建实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className, String message, Class<?>... parameterTypes ) {
        T instance;
        try {
            Class<?> commandClass = Class.forName(className);
            Constructor<?> constructor = commandClass.getConstructor(parameterTypes);
            instance = (T) constructor.newInstance(message);
        } catch (Exception e) {
            logger.error("创建实例出错！", e);
            throw new RuntimeException(e);
        }
        return instance;
    }

    /**
     * 通过反射创建实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className, Object[] value, Class<?>... parameterTypes ) {
        T instance;
        try {
            Class<?> commandClass = Class.forName(className);
            Constructor<?> constructor = commandClass.getConstructor(parameterTypes);
            instance = (T) constructor.newInstance(value);
        } catch (Exception e) {
            logger.error("创建实例出错！", e);
            throw new RuntimeException(e);
        }
        return instance;
    }
    /**
     * 通过反射创建实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> commandClass) {
        T instance;
        try {
            instance = (T) commandClass.newInstance();
        } catch (Exception e) {
            logger.error("创建实例出错！", e);
            throw new RuntimeException(e);
        }
        return instance;
    }

    /**
     * 获取对象的字段映射（字段名 => 字段值），忽略 static 字段
     */
    public static Map<String, Object> getFieldMap(Object obj) {
        String key = obj.getClass().getName();
        Map<String, Object> fieldMap = FIELD_MAP.get(key);
        if(ToolsKit.isEmpty(fieldMap)) {
            fieldMap = getFieldMap(obj, true);
            FIELD_MAP.put(key, fieldMap);
        }
        return fieldMap;
    }

    /**
     * 获取对象的字段映射（字段名 => 字段值）
     */
    public static Map<String, Object> getFieldMap(Object obj, boolean isStaticIgnored) {
        Map<String, Object> fieldMap = new LinkedHashMap<String, Object>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (isStaticIgnored && Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Object fieldValue = ObjectKit.getFieldValue(obj, field);
            fieldMap.put(field.getName(), fieldValue);
        }
        return fieldMap;
    }

    /**
     * 获取对象的字段属性
     */
    public static Map<String, Field> getFieldMap(Class<?> clazz, boolean isStaticIgnored) {
        Map<String, Field> fieldMap = new LinkedHashMap<String, Field>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (isStaticIgnored && Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String fieldName = field.getName();
            fieldMap.put(fieldName, field);
        }
        return fieldMap;
    }

    /**
     * 构建过滤方法名集合，默认包含Object类里公共方法
     * @param excludeMethodClass  如果有指定，则添加指定类下所有方法名
     *
     * @return
     */
    public static Set<String> buildExcludedMethodName(Class<?>... excludeMethodClass) {
        if(excludedMethodName.isEmpty()) {
            Method[] objectMethods = Object.class.getDeclaredMethods();
            for (Method m : objectMethods) {
                excludedMethodName.add(m.getName());
            }
        }
        Set<String> tmpExcludeMethodName = null;
        if(null != excludeMethodClass) {
            tmpExcludeMethodName = new HashSet<>();
            for (Class excludeClass : excludeMethodClass) {
                Method[] excludeMethods = excludeClass.getDeclaredMethods();
                if (null != excludeMethods) {
                    for (Method method : excludeMethods) {
                        tmpExcludeMethodName.add(method.getName());
                    }
                }
            }
            tmpExcludeMethodName.addAll(excludedMethodName);
        }
        return (null == tmpExcludeMethodName) ? excludedMethodName : tmpExcludeMethodName;
    }

    /**
     *  过滤方法
     *  @param method 需要过滤的Method
     *  @param excludedMethodName 包含要过滤的方法名集合
     *  @return boolean 如果包含则返回true
     */
    public static boolean isExcludeMethod(Method method, Set<String> excludedMethodName) {
        return excludedMethodName.contains(method.getName());
        //如果是Object, Controller公用方法名并且有参数的方法, 则返回true
//        return (excludedMethodName.contains(method.getClientId()) && method.getParameterTypes().length ==0 );
    }

    /**
     * 是否正常公用的API方法
     * 正常方法是指访问权限是public的且不是抽像，静态，接口，Final的方法
     * @param mod       Modifier的mod
     * @return
     */
    public static boolean isNormalApiMethod(int mod) {
        return !(Modifier.isAbstract(mod) || Modifier.isStatic(mod) || Modifier.isFinal(mod) || Modifier.isInterface(mod) || Modifier.isPrivate(mod) || Modifier.isProtected(mod));
    }

}
