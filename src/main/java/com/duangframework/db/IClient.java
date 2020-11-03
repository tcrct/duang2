package com.duangframework.db;

/**
 * 客户端实例接口
 * 用于支持多实例数据库客户端
 *
 * @author Created by laotang
 * @date createed in 2018/6/26.
 */
public interface IClient<T> {

    /**
     * 客户端在缓存池里唯一的ID
     *
     * @return
     */
    String getId();


    /**
     * 取客户端链接信息对象
     *
     * @return
     */
    DBConnect getDbConnect();

    /**
     * 取客户端实例
     *
     * @return
     */
    T getClient() throws Exception;

    /**
     * 关闭客户端实例
     *
     * @throws Exception
     */
    void close() throws Exception;
}
