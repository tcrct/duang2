package com.duangframework.db.mysql.core.ds;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.SQLException;

/**
 * Duang For Druid数据源
 *
 * @author laotang
 * @since 1.0
 */
public class DruidDataSourceFactory extends AbstractDataSource<DruidDataSource> {

    /**
     * 初始化连接数量(不知为何，设置了该值之后，启动时会卡死,使用默认值0即可)
     */
    private final int initialSize = 0;
    /**
     * 最大并发连接数
     **/
    private final int maxActive = 20;
    /**
     * 最小空闲连接数
     **/
    private final int maxIdle = 5;
    /**
     * 最大等待时间，毫秒
     */
    private final int maxWaitMillis = 60000;
    /**
     * 超过时间限制是否回收
     **/
    private final boolean removeAbandoned = true;
    /**
     * 超过时间限制多长
     **/
    private final int removeAbandonedTimeout = 180;
    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     **/
    private final long timeBetweenEvictionRunsMillis = 60000L;
    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     **/
    private final long minEvictableIdleTimeMillis = 300000L;
    /**
     * 申请连接时执行validationQuery检测连接是否有效，配置为true会降低性能
     **/
    private final boolean testOnBorrow = false;
    /**
     * 申请连接的时候检测
     **/
    private final boolean testWhileIdle = true;
    /**
     * 归还连接时执行validationQuery检测连接是否有效，配置为true会降低性能
     **/
    private final boolean testOnReturn = false;
    /**
     * 打开PSCache，并且指定每个连接上PSCache的大小
     **/
    private final boolean poolPreparedStatements = true;
    private final int maxPoolPreparedStatementPerConnectionSize = 50;
    /**
     * 属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有：
     * 监控统计用的filter:stat
     * 日志用的filter:log4j
     * 防御SQL注入的filter:wall
     */
    private final String filters = "stat";
    /**
     * 查询执行时间，默认10秒
     */
    private final int queryTimeOut = 10;

    @Override
    public DruidDataSource builderDataSource() {
        try {
            return new DruidDataSource();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setUsername(DruidDataSource ds, String username) {
        ds.setUsername(username);
    }

    @Override
    public void setPassword(DruidDataSource ds, String password) {
        ds.setPassword(password);
    }

    @Override
    public void setUrl(DruidDataSource ds, String jdbcUrl) {
        ds.setUrl(jdbcUrl);
    }

    /**
     * 添加其它参数
     **/
    @Override
    public void setInitParam(DruidDataSource ds) {
        ds.setInitialSize(initialSize);
        ds.setMaxActive(maxActive);
        ds.setMinIdle(maxIdle);
        ds.setMaxWait(maxWaitMillis);
        ds.setRemoveAbandoned(removeAbandoned);
        ds.setRemoveAbandonedTimeout(removeAbandonedTimeout);
        ds.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        ds.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        ds.setTestOnBorrow(testOnBorrow);
        ds.setTestWhileIdle(testWhileIdle);
        ds.setTestOnReturn(testOnReturn);
        ds.setPoolPreparedStatements(poolPreparedStatements);
        ds.setQueryTimeout(queryTimeOut);
        ds.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            ds.setFilters(filters);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
