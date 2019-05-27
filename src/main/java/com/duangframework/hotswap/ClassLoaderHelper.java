package com.duangframework.hotswap;

import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.core.helper.ClassHelper;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.server.common.BootStrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by laotang on 2019/5/27.
 */
public class ClassLoaderHelper {

    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderHelper.class);

    private boolean isDev;
    String packagePath;
    private List<Class<?>> classList;
    private DuangClassLoader duangClassLoader;

    public ClassLoaderHelper() {
        init();
    }

    private void init() {
        packagePath = PropKit.get(ConstEnums.PROPERTIES.BASE_PACKAGE_PATH.getValue());
        isDev = BootStrap.getInstants().isDevModel();
        if(!isDev && ToolsKit.isEmpty(packagePath)) {
            logger.info("热部署功能只允许在开发环境下运行");
            return;
        }
        duangClassLoader = new DuangClassLoader();
//        duangClassLoader.loadClass();
        ClassHelper.reSetAllBizClass(packagePath);
    }

}
