package com.duangframework.mvc.scan;

import com.duangframework.mvc.http.enums.ConstEnums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 类扫描工厂
 * Created by laotang on 2018/6/15.
 */
public class ScanClassFactory {

    private static final Logger logger = LoggerFactory.getLogger(ScanClassFactory.class);


    /**
     * 根据指定的包路径及jar文件名前缀(左匹配)取所有Class
     * @param packagePath       包路径，在该路径下的所有Class会扫描
     * @param jarNames              jar文件名前缀集合
     * @return
     */
    public static List<Class<?>> getAllClass(String packagePath, List<String> jarNames) {
        return new ClassTemplate(packagePath, jarNames){
            @Override
            public void checkAndAddClass(Class<?> clazz, List<Class<?>> classList) {
                classList.add(clazz);
            }
        }.getList();
    }

    /**
     * 根据指定的包路径及jar文件名前缀(左匹配)取所有ControllerClass
     * @param packagePath       包路径，在该路径下的所有Class会扫描
     * @param jarNames              jar文件名前缀集合
     * @return
     */
    public static List<Class<?>> getAllClass(String packagePath, List<String> jarNames, final Class<? extends Annotation> annotation) {
        return new ClassTemplate(packagePath, jarNames){
            @Override
            public void checkAndAddClass(Class<?> clazz, List<Class<?>> classList) {
                for(ConstEnums.ANNOTATION_CLASS classEnums : ConstEnums.ANNOTATION_CLASS.values()) {
                    Class<?> enumsClass = classEnums.getClazz();
                    if (clazz.isAnnotationPresent(annotation) ) {
                        classList.add(clazz);
                        break;
                    }
                }
            }
        }.getList();
    }

    /**
     *  取出所有业务类代码，一般用于热替换(热部署)功能
     *  不包括jar包下的类，仅包括classes文件下的所有class文件，一般是业务代码class
     *
     * @param packagePath       包路径，在该路径下的所有Class会扫描
     * @return
     */
    public static List<Class<?>> getAllBizClass(String packagePath) {
        return new ClassTemplate(packagePath){
            @Override
            public void checkAndAddClass(Class<?> clazz, List<Class<?>> classList) {
                classList.add(clazz);
            }
        }.getList();
    }

}
