package com.duangframework.mvc.core.helper;

import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.mvc.scan.ScanClassFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by laotang
 * @date createed in 2018/6/22.
 */
public class ClassHelper {

    private static final Logger logger = LoggerFactory.getLogger(ClassHelper.class);

    /**
     * 所有组件类[未实例]
     */
    private static final Map<String, List<Class<?>>> CLASS_MAP = new HashMap<>();

    /**
     *  加载所有类，包括接口类与抽象类，并按注解名作KEY分别缓存起来</br>
     *  <p>Map<BaseController, List<xxxController.class></p>
     *
     */
    static {
        String packagePath = PropKit.get(ConstEnums.BASE_PACKAGE_PATH.getValue());
        List<String> jarNames = PropKit.getList(ConstEnums.JAR_PREFIX.getValue());
        List<Class<?>> classList = ScanClassFactory.getAllClass(packagePath, jarNames);
        for(Class<?> clazz : classList) {
            for(ConstEnums.ANNOTATION_CLASS classEnums : ConstEnums.ANNOTATION_CLASS.values()) {
                if (clazz.isAnnotationPresent(classEnums.getClazz())) {
                    setClass2Map(classEnums.getName(), clazz);
                    break;
                }
            }
        }
    }


    public static void setClass2Map(String key, Class<?> clazz) {
        List<Class<?>> tmpList = CLASS_MAP.get(key);
        if(ToolsKit.isEmpty(tmpList)) {
            CLASS_MAP.put(key, new ArrayList<Class<?>>(){ { this.add(clazz);} });
        } else {
            tmpList.add(clazz);
        }
    }

    /**
     * 返回所有扫描过的类
     * @return
     */
    public static Map<String, List<Class<?>>> getClassMap() {
        return CLASS_MAP;
    }

    /**
     * 返回所有Controller类
     * @return
     */
    public static List<Class<?>> getClontrllerClassList () {
        return CLASS_MAP.get(ConstEnums.ANNOTATION_CLASS.CONTROLLER_ANNOTATION.getName());
    }

    /**
     * 返回所有Service类
     * @return
     */
    public static List<Class<?>> getServiceClassList () {
        return CLASS_MAP.get(ConstEnums.ANNOTATION_CLASS.SERVICE_ANNOTATION.getName());
    }

    /**
     * 返回所有Entity类
     * @return
     */
    public static List<Class<?>> getEntityClassList () {
        return CLASS_MAP.get(ConstEnums.ANNOTATION_CLASS.ENTITY_ANNOTATION.getName());
    }

    /**
     * 返回所有Plugin类
     * @return
     */
    public static List<Class<?>> getPluginClassList () {
        return CLASS_MAP.get(ConstEnums.ANNOTATION_CLASS.PLUGIN_ANNOTATION.getName());
    }

    /**
     * 返回所有Plugin类
     * @return
     */
    public static List<Class<?>> getClassList (Class<? extends Annotation> annotationClass) {
        return CLASS_MAP.get(annotationClass.getName());
    }

}
