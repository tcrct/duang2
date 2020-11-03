package com.duangframework.mvc.dto.upload;


import com.alibaba.fastjson.annotation.JSONField;

/**
 * HTTP multipart/form-data Request
 */
public class FileItem {

    private String name;
    private String contentTransferEncoding;
    private String fileName;
    private String contentType;
    private Long length;
    @JSONField(serialize = false, deserialize = false)
    private byte[] data;

    public FileItem(String name, String contentTransferEncoding, String fileName, String contentType, Long length, byte[] data) {
        this.name = name;
        this.contentTransferEncoding = contentTransferEncoding;
        this.fileName = fileName;
        this.contentType = contentType;
        this.length = length;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getContentTransferEncoding() {
        return contentTransferEncoding;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        long kb = length / 1024;
        return "FileItem(" +
                "name='" + name + '\'' +
                ", contentTransferEncoding='" + contentTransferEncoding + '\'' +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + (kb < 1 ? 1 : kb) + "KB)";
    }
}