package com.duangframework.mvc.http;

import com.duangframework.mvc.dto.upload.FileItem;
import com.duangframework.mvc.dto.upload.UploadFile;

/**
 * 上传文件接口，用于扩展云厂商，例如阿里云，七牛云等
 * Created by laotang on 2019/3/10.
 */
public interface IUploadFile {

    /**
     * 上传文件
     */
    UploadFile upload(FileItem fileItem) throws Exception;
}
