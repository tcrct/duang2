package com.duangframework.cache;

/**
 *
 * @author Created by laotang
 * @date createed in 2018/5/17.
 */
public interface ICacheKeyEnums {

    int DEFAULT_TTL = 60*30*1000;

    /**
     *取出缓存Key的前缀
     */
    String getKeyPrefix();

    /**
     * 取出缓存Key的有效时间,毫秒
     * @return
     */
    int getKeyTTL();

    /**
     * 用途说明
     */
    String getKeyDesc();

}
