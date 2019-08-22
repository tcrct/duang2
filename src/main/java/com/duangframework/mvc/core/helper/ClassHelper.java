package com.duangframework.mvc.core.helper;

import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.mvc.scan.ScanClassFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

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
        String packagePath = PropKit.get(ConstEnums.PROPERTIES.BASE_PACKAGE_PATH.getValue());
        List<String> jarNames = PropKit.getList(ConstEnums.PROPERTIES.JAR_PREFIX.getValue());
        setClass2Map(ScanClassFactory.getAllClass(packagePath, jarNames));
    }

    private static void setClass2Map(List<Class<?>> clazzList) {
        for(Class<?> clazz : clazzList) {
            for(ConstEnums.ANNOTATION_CLASS classEnums : ConstEnums.ANNOTATION_CLASS.values()) {
                if (clazz.isAnnotationPresent(classEnums.getClazz())) {
                    String key = classEnums.getName();
                    List<Class<?>> tmpList = CLASS_MAP.get(key);
                    if(ToolsKit.isEmpty(tmpList)) {
                        CLASS_MAP.put(key, new ArrayList<Class<?>>(){ { this.add(clazz);} });
                    } else {
                        tmpList.add(clazz);
                    }
                    break;
                }
            }
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
    public static List<Class<?>> getControllerClassList() {
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
     * 返回所有Bean类
     * @return
     */
    public static List<Class<?>> getBeanClassList () {
        return CLASS_MAP.get(ConstEnums.ANNOTATION_CLASS.BEAN_ANNOTATION.getName());
    }

    /**
     * 返回所有Plugin类
     * @return
     */
    public static List<Class<?>> getPluginClassList () {
        return CLASS_MAP.get(ConstEnums.ANNOTATION_CLASS.PLUGIN_ANNOTATION.getName());
    }

    /**
     * 返回所有类集合
     * @return
     */
    public static List<Class<?>> getClassList (Class<? extends Annotation> annotationClass) {
        return CLASS_MAP.get(annotationClass.getName());
    }

    /**
     *  取出所有业务类代码，一般用于热替换(热部署)功能
     *  不包括jar包下的类，仅包括classes文件下的所有class文件，一般是业务代码class
     *
     * @param classList       包路径，在该路径下的所有Class会扫描
     * @return
     */
    public static void reSetAllBizClass(List<Class<?>> classList) {
        // 取出所有业务类之前，先将原有的
        CLASS_MAP.clear();
//        List<Class<?>> classList = ScanClassFactory.getAllBizClass(packagePath);
        // 将业务类按枚举名称作key，分类存放到CLASS_MAP中
        setClass2Map(classList);
    }

}
