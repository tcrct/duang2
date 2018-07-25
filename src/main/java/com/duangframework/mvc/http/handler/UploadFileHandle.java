package com.duangframework.mvc.http.handler;


import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.upload.FileItem;
import com.duangframework.mvc.dto.upload.UploadFile;
import com.duangframework.utils.UploadFileUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Created by laotang
 * @date createed in 2018/6/8.
 */
public class UploadFileHandle {

    private static Logger logger = LoggerFactory.getLogger(UploadFileHandle.class);

    private FileItem fileItem;      //上传到netty后，封装成FileItem对象
    private String saveDir; // 上传文件存放到服务器的目录 ，绝对路径
    private boolean isUUIDName; // 是否要生成新的文件名

    public UploadFileHandle(FileItem fileItem, String saveDir, boolean isUUIDName) {
        this.fileItem = fileItem;
        this.saveDir =saveDir;
        this.isUUIDName = isUUIDName;
    }

    public UploadFile getUploadFile() {
        if(ToolsKit.isEmpty(fileItem)) {
            throw new NullPointerException("fileitem is null");
        }
        String serverFileDir = UploadFileUtils.builderServerFileDir(saveDir);
        String serverFileName = UploadFileUtils.builderServerFileName(fileItem.getFileName(), isUUIDName);
        File dir = new File(serverFileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dest = new File(serverFileDir, serverFileName);
        UploadFile uploadFile = null;
        try {
            FileUtils.writeByteArrayToFile(dest, fileItem.getData());
            uploadFile = new UploadFile(fileItem.getName(),
                    serverFileDir,
                    serverFileName,
                    fileItem.getFileName(),
                    fileItem.getName(),
                    fileItem.getContentType(),
                    fileItem.getLength());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return uploadFile;
    }


}
