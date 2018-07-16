package com.duangframework.mvc.http.enums;


/**
 * Created by laotang on 2017/4/20.
 */
public enum ContentTypeEnums {

    TEXT("text/plain"),
    STREAM("application/octet-stream"),
    JSON("application/json"),
    XML("text/xml"),
    MULTIPART("multipart/form-data"),
    FORM("application/x-www-form-urlencoded");

    private final String value;
    /**
     * Constructor.
     */
    private ContentTypeEnums(String value) {
        this.value = value;
    }

    /**
     * Get the value.
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * 根据contentType字符串，查找是否有对应的ContentType枚举
     * @param contentTypeString
     * @return
     */
    public static ContentTypeEnums parse(String contentTypeString) {
        if(null == contentTypeString || contentTypeString.trim().length() == 0) {
            return null;
        }
        for(ContentTypeEnums contentType : ContentTypeEnums.values()) {
            if(contentType.getValue().contains(contentTypeString)) {
                return contentType;
            }
        }
        // 如果没有匹配到的，则返回null, 最终由提交的内容确定
        return null;
    }

}
