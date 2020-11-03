/*
 * 数据链接池抽象类
 */
package com.duangframework.db.mysql.core.ds;

import com.duangframework.db.DBConnect;
import com.duangframework.kit.ToolsKit;

import javax.sql.DataSource;

/**
 * 数据链接池抽象类，子类继承实现
 *
 * @author laotang
 */
public abstract class AbstractDataSource<T extends DataSource> implements IDataSourceFactory {

    @Override
    public T getDataSource(DBConnect connect) throws Exception {
        T ds = builderDataSource();
        if (ToolsKit.isNotEmpty(connect.getUsername())) {
            setUsername(ds, connect.getUsername());
        }
        if (ToolsKit.isNotEmpty(connect.getPassword())) {
            setPassword(ds, connect.getPassword());
        }
        if (ToolsKit.isNotEmpty(connect.getUrl())) {
            setUrl(ds, connect.getUrl());
        }
        setInitParam(ds);
        return ds;
    }


    /**
     * 构建数据源
     *
     * @return
     */
    public abstract T builderDataSource();

    /**
     * 设置用户名
     *
     * @param ds
     * @param userName
     */
    public abstract void setUsername(T ds, String userName);

    /**
     * 设置密码
     *
     * @param ds
     * @param password
     */
    public abstract void setPassword(T ds, String password);

    /**
     * 设置链接字符串
     *
     * @param ds
     * @param jdbcUrl
     */
    public abstract void setUrl(T ds, String jdbcUrl);

    /**
     * 设置初始化参数值
     *
     * @param ds
     * @param map
     */
    public abstract void setInitParam(T ds);

}
