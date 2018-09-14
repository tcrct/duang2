package com.duangframework.db.mysql.core;

import java.sql.SQLException;

/**
 * @author laotang
 */
public interface DBAction<T> {

	/**
	 *
	 * @param dbRunner
	 * @return
	 * @throws SQLException
	 */
	T execute(DBRunner dbRunner) throws SQLException;

	/**
	 *
	 * @return
	 */
	String dataSourceKey();
}
