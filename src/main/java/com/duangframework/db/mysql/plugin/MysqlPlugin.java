package com.duangframework.db.mysql.plugin;

import com.duangframework.db.DbClientFactory;
import com.duangframework.db.mysql.MysqlDao;
import com.duangframework.db.mysql.client.MysqlClientAdapter;
import com.duangframework.db.mysql.utils.MysqlUtils;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Import;
import com.duangframework.mvc.core.helper.BeanHelper;
import com.duangframework.mvc.plugin.IPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Mysql 插件
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class MysqlPlugin implements IPlugin {

    private final static Logger logger = LoggerFactory.getLogger(MysqlPlugin.class);


    public MysqlPlugin(MysqlClientAdapter clientAdapter) throws Exception {
        DbClientFactory.setMysqlClient(clientAdapter);
        DbClientFactory.setMysqlDefaultClientId(clientAdapter.getId());
    }



    /**
     * 多数据库时使用<br/>
     * 如果没有设置默认db client的话，则用第一个client作为默认的client
     * @param clientAdapterList
     */
    public MysqlPlugin(List<MysqlClientAdapter> clientAdapterList) throws Exception {
        String defaultClientId = "";
        for(MysqlClientAdapter clientAdapter : clientAdapterList) {
            if(clientAdapter.isDefaultClient()) {
                defaultClientId = clientAdapter.getId();
            }
            DbClientFactory.setMysqlClient(clientAdapter);
        }
        if(ToolsKit.isEmpty(defaultClientId)) {
            defaultClientId = clientAdapterList.get(0).getId();
        }
        DbClientFactory.setMysqlDefaultClientId(defaultClientId);
    }

    /**
     *  启动插件，先进行链接，再设置Dao Bean 到Ioc集合
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        // 链接数据
        for (Iterator<Map.Entry<String, MysqlClientAdapter>> iterator = DbClientFactory.getMysqlDbClients().entrySet().iterator(); iterator.hasNext();) {
            iterator.next().getValue().getClient();
        }
        addBeanToIocMap();
    }

    @Override
    public void stop() throws Exception {
        for (Iterator<Map.Entry<String, MysqlClientAdapter>> iterator = DbClientFactory.getMysqlDbClients().entrySet().iterator(); iterator.hasNext();) {
            iterator.next().getValue().close();
        }
    }

    /**
     * 设置Dao Bean 到Ioc集合
     * @throws Exception
     */
    private void addBeanToIocMap() throws Exception {
    // 取出所有类对象
    Map<String, Object> iocBeanMap = BeanHelper.getIocBeanMap();
    if(ToolsKit.isEmpty(iocBeanMap)) {
        return;
    }
    for(Iterator<Map.Entry<String, Object>> it = iocBeanMap.entrySet().iterator(); it.hasNext();) {
        Map.Entry<String, Object> entry = it.next();
        Object beanObj = entry.getValue();
        Class<?> serviceClass = beanObj.getClass();
        Field[] fields = serviceClass.getDeclaredFields();
            for(Field field : fields) {
                Import importAnnot = field.getAnnotation(Import.class);
                if (ToolsKit.isNotEmpty(importAnnot) && MysqlDao.class.equals(field.getType())) {
                    ParameterizedType paramType = (ParameterizedType) field.getGenericType();
                    Type[] types = paramType.getActualTypeArguments();
                    if(ToolsKit.isNotEmpty(types)) {
                        // <>里的泛型类
                        Class<?> paramTypeClass = ClassKit.loadClass(types[0].getTypeName());
                        String key = ToolsKit.isEmpty(importAnnot.client()) ? DbClientFactory.getMysqlDefaultClientId() : importAnnot.client();
                        Object daoObj = MysqlUtils.getMysqlDao(key, paramTypeClass);
                        if(null != daoObj) {
                            field.setAccessible(true);
                            field.set(beanObj, daoObj);
                        }
                    }
                }
            }
        }
    }
}
