package com.duangframework.utils;


import com.duangframework.kit.PathKit;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Created by laotang
 * @date createed in 2018/6/8.
 */
public class UploadFileUtils {

    private static Logger logger = LoggerFactory.getLogger(UploadFileUtils.class);
    /**
     * 创建上传文件存放在服务器的绝对路径，包含文件名
     * @return
     */
    public static String builderServerFileDir(String saveDir) {
        String rootDir = PathKit.getPath("/");
        rootDir = checkDirString(rootDir);
        if(rootDir.endsWith("classes")) {
            rootDir = rootDir.substring(0, rootDir.length() - 7);
        }
        String uploadfilesDir = PropKit.get(ConstEnums.PROPERTIES.UPLOADFILE_DIRECTORY.getValue(), "uploadfiles");
        uploadfilesDir = checkDirString(uploadfilesDir);
        if(ToolsKit.isEmpty(saveDir)) {
            String productCode = PropKit.get(ConstEnums.PROPERTIES.PRODUCT_CODE.getValue(), ConstEnums.FRAMEWORK_OWNER.getValue());
            String currentDate = ToolsKit.formatDate(new Date(), "yyyyMMdd");
            saveDir = productCode+"/"+currentDate;
        }
        saveDir = checkDirString(saveDir);
        String path =  rootDir+"/" +uploadfilesDir+"/" +saveDir;
        path = checkDirString(path);
        logger.debug("upload file on server path : "  +  path);
        return path;
    }

    public static String builderServerFileName(String fileName, boolean isUUIDName) {
        if(isUUIDName) {
            fileName = new DuangId().toString() +"."+getUploadFileExtName(fileName);
        }
        logger.debug("upload file on server name : "  +  fileName);
        return fileName;
    }


    private static String getUploadFileExtName(String fileName) {
        if(!fileName.contains(".")) {
            throw new IllegalArgumentException("文件扩展名不存在");
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
    }

    private static String checkDirString(String dir) {
        if(ToolsKit.isNotEmpty(dir)) {
            dir = dir.startsWith("/") ? dir.substring(1, dir.length()) : dir;
            dir = dir.endsWith("/") ? dir.substring(0, dir.length() - 1) : dir;
        }
        return dir.trim();
    }
}
