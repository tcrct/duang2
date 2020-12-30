package com.duangframework.db.mysql.core.ds;

import com.duangframework.db.DBConnect;

import javax.sql.DataSource;

/**
 * 数据源接口
 * @author laotang
 * @since 1.0
 */
public interface IDataSourceFactory {

	/**
	 *  获取数据源
	 * @return		DataSource
	 */
	DataSource getDataSource(DBConnect connect) throws Exception;

}
