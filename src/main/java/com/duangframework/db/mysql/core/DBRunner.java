package com.duangframework.db.mysql.core;


import com.duangframework.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库执行器
 *
 * @author laotang
 * @since 1.0
 */
public final class DBRunner {

    private final static StringBuilder loggerStr = new StringBuilder();
    private static Logger logger = LoggerFactory.getLogger(DBRunner.class);
    private Connection connection;

    public DBRunner(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * 执行查询SQL语句
     *
     * @param sql        查询SQL语句
     * @param columnList 返回指定的字段
     * @param params     查询参数
     * @throws SQLException
     * @return 结果集List<Map < String, Object>> 每一个Map代表着一行数据，Map里的key<==>字段名，value<==>字段值
     */
    public List<Map<String, Object>> query(String sql, List<String> columnList, Object... params) throws SQLException {
        if (ToolsKit.isEmpty(connection)) {
            throw new SQLException("Null connection");
        }
        if (ToolsKit.isEmpty(sql)) {
            if (ToolsKit.isNotEmpty(connection)) {
                connection.close();
            }
            throw new SQLException("Null SQL Statement");
        }
        loggerSql(sql, params);
        boolean isFilerColumn = ToolsKit.isNotEmpty(columnList);
        if (isFilerColumn) {
            logger.info("column: " + ToolsKit.toJsonString(columnList));
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        PreparedStatement stmt = null;
        try {
            stmt = fillStatement(connection.prepareStatement(sql), params);
            ResultSet rs = stmt.executeQuery();
            if (ToolsKit.isEmpty(rs)) {
                return null;
            }
            /**  结果集的头部信息**/
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();
            while (rs.next()) {
                Map<String, Object> resultMap = new LinkedHashMap<>(cols);
                for (int i = 1; i <= cols; i++) {
                    String columnName = rsmd.getColumnLabel(i);
                    if (ToolsKit.isEmpty(columnName)) {
                        columnName = rsmd.getColumnName(i);
                    }
                    if (isFilerColumn && columnList.contains(columnName)) {
                        resultMap.put(columnName, rs.getObject(columnName));
                    } else {
                        resultMap.put(columnName, rs.getObject(i));
                    }
                }
                // 添加到返回List
                if (ToolsKit.isNotEmpty(resultMap)) {
                    resultList.add(resultMap);
                }
            }
        } catch (SQLException e) {
            logger.warn("query " + sql + " onException: " + e.getMessage(), e);
        } finally {
            DBSession.close(stmt);
            DBSession.close(connection);
        }
        return resultList;
    }

    /**
     * 执行不带参数的SQL语句，如创建表及创建表索引时用
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public int execute(String sql) throws Exception {
        return execute(sql, DBSession.NULL_OBJECT);
    }

    /**
     * 执行SQL语句，用于insert, update, delete等
     *
     * @param sql    sql语句
     * @param params 参数
     * @throws SQLException
     * @return 受影响的行数
     */
    public int execute(String sql, Object... params) throws Exception {
        if (ToolsKit.isEmpty(connection)) {
            throw new SQLException("Null connection");
        }
        if (ToolsKit.isEmpty(sql)) {
            if (ToolsKit.isNotEmpty(connection)) {
                connection.close();
            }
            throw new SQLException("Null SQL statement");
        }
        int rows = 0;
        PreparedStatement stmt = null;
        try {
            loggerSql(sql, params);
            stmt = fillStatement(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS), params);
            rows = stmt.executeUpdate();
            if (sql.toLowerCase().startsWith("insert")) {
                ResultSet rs = stmt.getGeneratedKeys();        // 获取自动递增主键值
                if (rs.next()) {
//                    Serializable ret = (Serializable) rs.getObject(1);
//					rows = Integer.parseInt(ret.toString());
                    rows = rs.getInt(1);
                    System.out.println(rows + "####################rs.getString" + rs.getString("_id"));
                }
            }
        } catch (Exception e) {
            logger.warn("execute " + sql + " onException: " + e.getMessage(), e);
            throw new SQLException(e);
        } finally {
            DBSession.close(stmt);
            DBSession.close(connection);
        }
        return rows;
    }

    private void loggerSql(String sql, Object... params) {
//		loggerStr.delete(0, loggerStr.length()); //时间长了会出现length()为负数的可能性
        loggerStr.setLength(0);
        loggerStr.append(sql);
        if (ToolsKit.isNotEmpty(params)) {
            loggerStr.append("             ").append(ToolsKit.toJsonString(params));
        }
        logger.info(loggerStr.toString());
    }

    /**
     * @param stmt
     * @param params
     * @return
     * @throws SQLException
     */
    private PreparedStatement fillStatement(PreparedStatement stmt, Object... params) throws SQLException {
        if (ToolsKit.isNotEmpty(params)) {

            ParameterMetaData pmd = stmt.getParameterMetaData();
            int stmtCount = pmd.getParameterCount();
            int paramsCount = null == params ? 0 : params.length;

            if (stmtCount != paramsCount) {
                throw new SQLException("Wrong number of parameters: expected "
                        + stmtCount + ", was given " + paramsCount);
            }

            for (int i = 0; i < paramsCount; i++) {
                stmt.setObject(i + 1, params[i]);
            }
        }
        return stmt;
    }

}
