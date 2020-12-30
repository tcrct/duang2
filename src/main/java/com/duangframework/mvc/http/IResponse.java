package com.duangframework.mvc.http;


import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 *dagger4j框架自实现的Request, 定义response对象接口
 * @author laotang
 * @date 2018/06/09
 */
public interface IResponse {

    String getRequestId();
    /**
     * 添加返回头信息
     * @param key           名称
     * @param value         值
     */
    void setHeader(String key, String value);

    /**
     * 根据name返回头信息
     * @param name          名称
     */
    String getHeader(String name);

    /**
     * 返回Header头所有名称
     * @return
     */
    Collection<String> getHeaderNames();

    /**
     * 返回header对象
     * @return
     */
    Map<String,String> getHeaders();

    /**
     * 取返回状态标识
     * @return
     */
    int getStatus();

    /**
     * 设置返回状态标识
     * @return
     */
    void setStatus(int status);

    /**
     * 设置编码格式
     * @return
     */
    void setCharacterEncoding(String charset);

    /**
     * 返回编码格式名称
     * @return
     */
    String getCharacterEncoding();

    /**
     * 设置返回ContentType信息
     * @param contentType
     */
    void setContentType(String contentType);

      /**
     * 设置返回主体内容
     * @param returnObj     返回主体对象
     */
    void write(Object returnObj);

    /**
     * 取返回主体内容
     * 在返回到客户前，可以对返回值进行编辑
     * @return
     */
    Object getBody();

    @Override
    String toString();

    /**
     *
     * @param contentLength
     */
    void setContentLength(int contentLength);

    /**
     *  取需要下载的文件
     * @return  File
     */
    File getFile();

    /**
     * 是否下载文件返回
     * @return 如果是File则返回true
     */
    boolean isFile();

    /**
     * 下载完成后，是否物理删除服务器上的文件
     * @return  是删除返回true
     */
    boolean isDeleteFile();

    /**
     * 是否删除下载文件
     * @param isDelete 是否删除
     */
    void setDeleteFile(boolean isDelete);

    /**
     * Redirect to newUri
     *
     * @param newUri new url
     */
    void redirect(String newUri);

    /**
     *  encode url
     */
    String encodeURL(String url);
}
