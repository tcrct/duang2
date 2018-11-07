package com.duangframework.kit;

import com.duangframework.db.annotation.Entity;
import com.duangframework.exception.MvcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Created by laotang
 * @date createed in 2018/6/14.
 */
public final class ClassKit {

    private static final Logger logger = LoggerFactory.getLogger(ClassKit.class);

    private static final ConcurrentMap<String, Field[]> FIELD_MAPPING_MAP = new ConcurrentHashMap<String, Field[]>();

    public static ClassLoader getClassLoader() {
        ClassLoader ret = Thread.currentThread().getContextClassLoader();
        return ret != null ? ret : ClassKit.class.getClassLoader();
    }

    /**
     * 实例化类文件, 默认实例化
     * @param className  类文件，包括包路径
     * @return
     */
    public static Class<?> loadClass(String className) {
        if (ToolsKit.isEmpty(className)) {
            return null;
        }
        try {
            return getClassLoader().loadClass(className);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 实例化类文件, 默认实例化
     * @param clazz  类文件
     * @return
     */
    public static Class<?> loadClass(Class<?> clazz) {
        return loadClass(clazz, true);
    }
    /**
     * 实例化类文件
     * @param clazz  类文件
     * @param isInit  是否实例
     * @return
     */
    public static Class<?> loadClass(Class<?> clazz, boolean isInit) {
        if (ToolsKit.isEmpty(clazz)) {
            return null;
        }
        try {
            // 要初始化且支持实例化
            if(isInit && supportInstance(clazz)) {
                clazz = Class.forName(clazz.getName(), isInit, ClassKit.getClassLoader());
            }
        } catch (ClassNotFoundException e) {
            logger.warn("Load class is onError:" + clazz.getName(), e);
            throw new MvcException(e.getMessage(), e);
        } catch (Exception e) {
            logger.warn("Load class is onError:" + clazz.getName(), e);
            throw new MvcException(e.getMessage(), e);
        }
        return clazz;
    }


    /**
     * 检查该类是否支持实例化
     * <p>如果是抽象类或接口类则返回false</p>
     *
     * @param clazz
     * @return
     */
    public static boolean supportInstance(Class<?> clazz) {
        if(null == clazz) {
            return false;
        }
        //
        if(Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
            return false;
        }
//        BaseController controller = clazz.getAnnotation(BaseController.class);
//        if(null != controller && !controller.autowired()){
//            return false;
//        }
//        Service service = clazz.getAnnotation(Service.class);
//        if(null != service && !service.autowired()){
//            return false;
//        }
        return true;
    }


    @SuppressWarnings("rawtypes")
    public static boolean isExtends(Class<?> cls, String topClassName) {
        String clsName = cls.getCanonicalName();
        if("java.lang.Object".equals(clsName)) {
            return true;
        }
        Class parent = cls.getSuperclass();
        if(ToolsKit.isNotEmpty(parent)){
            String name = parent.getCanonicalName();
            if(name.equals(topClassName)) {
                return true;
            }
            while(ToolsKit.isNotEmpty(parent)){
                parent = parent.getSuperclass();
                if(parent == null) {
                    return false;
                }
                name = parent.getCanonicalName();
                if(name.equals(topClassName)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 取得Bean的名字,如果有指定则用指定的,没有则用小写的类名作表名用
     * @param cls
     * @return
     */
    public static String getEntityName(Class<?> cls) {
        return getEntityName(cls, false);
    }

    /**
     * 取类的简短名称
     * @param cls			类对象
     * @param isLowerCase	是否返回小写,true时返回小写
     * @return				简短名称
     */
    public static String getClassSimpleName(Class<?> cls, boolean isLowerCase) {
        String name = cls.getSimpleName();
        return isLowerCase ? name.toLowerCase() : name;
    }

    public static String getEntityName(Class<?> cls, boolean isLowerCase) {
        Entity entity = cls.getAnnotation(Entity.class);
        // TODO 兼容Duang2.0版的Entity

        String name= ( null == entity )? getClassSimpleName(cls, isLowerCase) : entity.name();
        return isLowerCase ? name.toLowerCase() : name;
    }

    /**
     * 取出类的全名，包括包名
     * @param cls                       类
     * @param isLowerCase       是否转为小写
     * @return
     */
    public static String getClassName(Class<?> cls, boolean isLowerCase) {
        String name = cls.getName();
        return isLowerCase ? name.toLowerCase() : name;
    }

    /**
     * 取出类的全名，包括包名
     * @param cls
     * @return
     */
    public static String getClassName(Class<?> cls) {
        return getClassName(cls ,true);
    }

    /**
     * 根据class对象反射出所有属性字段，静态字段除外
     * @param cls
     * @return
     */
    public static Field[] getFields(Class<?> cls){
        String key = getClassName(cls);
        Field[] field = null;
        if(FIELD_MAPPING_MAP.containsKey(key)){
            field = FIELD_MAPPING_MAP.get(key);
        }else{
            field = getAllFields(cls);
            FIELD_MAPPING_MAP.put(key, field);
        }
        return (null == field) ? null : field;
    }

    /**
     * 取出类里的所有字段
     * @param cls
     * @return	Field[]
     */
    private static Field[] getAllFields(Class<?> cls) {
        List<Field> fieldList = new ArrayList<Field>();
        fieldList.addAll(filterStaticFields(cls.getDeclaredFields()));
        Class<?> parent = cls.getSuperclass();
        //查找父类里的属性字段
        while(null != parent && parent != Object.class){
            fieldList.addAll(filterStaticFields(parent.getDeclaredFields()));
            parent = parent.getSuperclass();
        }
        return fieldList.toArray(new Field[fieldList.size()]);
    }

    /**
     * 过滤静态方法
     * @param fields
     * @return
     */
    private static List<Field> filterStaticFields(Field[] fields){
        List<Field> result = new ArrayList<Field>();
        for (Field field : fields) {
            if(!Modifier.isStatic(field.getModifiers())){		//静态字段不取
                field.setAccessible(true);	//设置可访问私有变量
                result.add(field);
            }
        }
        return result;
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
     * 如无法找到, 返回Object.class.
     * eg.
     * public UserDao extends HibernateDao<User>
     *
     * @param clazz The class to introspect
     * @return the first generic declaration, or Object.class if cannot be determined
     */
    public static <T> Class<T> getSuperClassGenricType(final Class clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
     * 如无法找到, 返回Object.class.
     *
     * 如public UserDao extends MongodbBaseDao<User,String>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be determined
     */
    public static Class getSuperClassGenricType(final Class clazz, final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) params[index];
    }


}
