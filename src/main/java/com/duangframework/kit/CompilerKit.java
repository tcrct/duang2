package com.duangframework.kit;
import com.duangframework.ext.ConstEnum;
import com.duangframework.hotswap.ClassLoaderHelper;
import com.duangframework.hotswap.CompilerUtils;
import com.duangframework.hotswap.FileListener;
import com.duangframework.mvc.http.enums.ConstEnums;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 动态编译工具类
 * @author laotang
 * @date 2019-5-31
 */
public class CompilerKit {

    private static final Logger logger = LoggerFactory.getLogger(CompilerKit.class);

    private static class CompilerKitHolder {
        private static final CompilerKit INSTANCE = new CompilerKit();
    }
    private CompilerKit() {
//        fileListener();
        rootPath = PathKit.getWebRootPath();
    }
    public static final CompilerKit duang() {
        clear();
        return CompilerKit.CompilerKitHolder.INSTANCE;
    }
    private static void clear(){

    }
//    private void fileListener() {
//        rootPath = PathKit.getWebRootPath();
//        String dirListenerPath = rootPath + rootItemPath() + File.separator + "com.signetz.openapi.controller".replace(".", File.separator);
//        System.out.println(dirListenerPath);
//        try {
//            FileListener.addListener(dirListenerPath);
//        } catch (Exception e) {
//            logger.warn(e.getMessage(),e);
//        }
//    }
    /****************************************************************************************/
    private static String rootPath;
    private String dirPath;
    private String sourceDir;
    private String targetDir;
    /**
     * java文件夹目录，IDEA下到java目录
     * @return
     */
    public CompilerKit dir(String dirPath) {
        this.dirPath = dirPath;
        return this;
    }

    public CompilerKit javaDir(String javaDir) {
        this.sourceDir = javaDir;
        return this;
    }

    public CompilerKit classDir(String classDir) {
        this.targetDir = classDir;
        return this;
    }
    private String rootItemPath() {
        return File.separator + "src" +File.separator + "main" + File.separator + "java";
    }
    private void setDefaultValue() {
        if(ToolsKit.isEmpty(dirPath)) {
            dirPath = rootPath + rootItemPath() + File.separator +
                    PropKit.get(ConstEnums.PROPERTIES.BASE_PACKAGE_PATH.getValue()).replace(".", File.separator);
        }
        if(ToolsKit.isEmpty(sourceDir)) {
            sourceDir = rootPath + rootItemPath();
        }
        if(ToolsKit.isEmpty(targetDir)) {
            targetDir = rootPath + File.separator + "target" + File.separator + "classes";
        }
    }

    public String dir() {
        setDefaultValue();
        return dirPath;
    }

    public String classDir() {
        setDefaultValue();
        return targetDir;
    }

    /**
     * 编译
     */
    public void compiler() {
        boolean isSuccess = false;
        setDefaultValue();
        try {
            if(ToolsKit.isNotEmpty(targetDir)) {
                FileUtils.forceMkdir(new File(targetDir));
            }
            isSuccess = CompilerUtils.getInstance().compiler(dirPath, sourceDir, targetDir);
        } catch (Exception e) {
            logger.warn("动态编译时出错: " + e.getMessage(), e);
        }
        if(isSuccess) {
            ClassLoaderHelper.getInstance().hotSwap();
        } else {
            logger.warn("热部署失败");
        }
    }

}
