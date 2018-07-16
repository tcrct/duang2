package com.duangframework.cache.ds;

import com.duangframework.db.DBConnect;
import com.duangframework.db.IClient;
import com.duangframework.kit.ToolsKit;
import com.duangframework.utils.MD5;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import redis.clients.jedis.exceptions.JedisException;

/**
 * @author Created by laotang
 * @date createed in 2018/7/5.
 */
public class EhCacheAdapter extends AbstractCacheSource<ResourcePoolsBuilder> implements IClient<ResourcePoolsBuilder> {

    private String alias = "dagger_ehcache";       //别名
    private int heap;            // 堆内存大少？？？

    public EhCacheAdapter(String alias, int heap) {
        this.alias = alias;
        this.heap = heap;
    }

    public String getAlias() {
        return alias;
    }

    public int getHeap() {
        return heap;
    }

    @Override
    public String getId() {
        return ToolsKit.isEmpty(alias) ?  MD5.MD5Encode(toString()) : alias;
    }

    @Override
    public DBConnect getDbConnect() {
        return null;
    }

    @Override
    public ResourcePoolsBuilder getClient() throws Exception {
        return getSource();
    }

    @Override
    public void close() throws Exception {

    }

    public static class Builder {
        private String alias;
        private int heap;

        public Builder() {

        }

        public Builder alias(String alias) {
            this.alias = alias;
            return this;
        }
        public Builder heap(int heap) {
            this.heap = heap;
            return this;
        }

        public EhCacheAdapter build() {
            return new EhCacheAdapter(alias, heap);
        }
    }

    @Override
    protected ResourcePoolsBuilder builderDataSource() {
        try {
        return ResourcePoolsBuilder.heap(heap);
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisException(e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "EhCacheAdapter{" +
                "alias='" + alias + '\'' +
                ", heap=" + heap +
                '}';
    }
}
