package com.duangframework.hotswap;

import com.duangframework.kit.PathKit;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.core.helper.BeanHelper;
import com.duangframework.mvc.core.helper.ClassHelper;
import com.duangframework.mvc.core.helper.IocHelper;
import com.duangframework.mvc.core.helper.RouteHelper;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.server.common.BootStrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by laotang on 2019/5/27.
 */
public class ClassLoaderHelper {

    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderHelper.class);

    private boolean isDev;
    private DuangClassLoader duangClassLoader;

    public static ClassLoaderHelper getInstance() {
        return new ClassLoaderHelper();
    }

    private ClassLoaderHelper() {
        init();
    }

    private void init() {
        isDev = BootStrap.getInstants().isDevModel();
        if(!isDev) {
            logger.info("热部署功能只允许在开发环境下运行");
            return;
        }
        duangClassLoader = new DuangClassLoader();
        Set<String> classKeySet = duangClassLoader.getClassKeySet();
        List<Class<?>> classList = new ArrayList<>(classKeySet.size());
        try {
            for (String classKey : classKeySet) {
                classList.add(duangClassLoader.loadClass(classKey));
            }
            ClassHelper.reSetAllBizClass(classList);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public void hotSwap() {
        try {
            // 清除旧的BeanMap
            BeanHelper.getBeanMap().clear();
            // 生成bean并缓存到Map
            BeanHelper.createBean2Map();
            // 清除旧的IocBeanMap
            BeanHelper.clearIocBeanMap();
            // ioc
            IocHelper.iocBean4Map();
            // route
            RouteHelper.getRouteMap().clear();
            RouteHelper.getRestfulRouteMap().clear();
            RouteHelper.createRoute2Map();
            logger.warn("hotswap is success");
        } catch (Exception e) {
            logger.warn("hotswap is fail: " + e.getMessage(),e);
        }
    }

}
