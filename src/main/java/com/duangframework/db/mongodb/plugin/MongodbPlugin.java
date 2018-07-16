package com.duangframework.db.mongodb.plugin;

import com.duangframework.db.DbClientFactory;
import com.duangframework.db.mongodb.client.MongoClientAdapter;
import com.duangframework.db.mongodb.common.MongoDao;
import com.duangframework.db.mongodb.utils.MongoUtils;
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
 * MongoDB插件
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class MongodbPlugin implements IPlugin {

    private final static Logger logger = LoggerFactory.getLogger(MongodbPlugin.class);


    public MongodbPlugin(MongoClientAdapter clientAdapter) throws Exception {
        DbClientFactory.setMongoClient(clientAdapter);
        DbClientFactory.setMongoDefaultClientId(clientAdapter.getId());
    }



    /**
     * 多数据库时使用<br/>
     * 如果没有设置默认db client的话，则用第一个client作为默认的client
     * @param clientAdapterList
     */
    public MongodbPlugin (List<MongoClientAdapter> clientAdapterList) throws Exception {
        String defaultClientId = "";
        for(MongoClientAdapter clientAdapter : clientAdapterList) {
            if(clientAdapter.isDefaultClient()) {
                defaultClientId = clientAdapter.getId();
            }
            DbClientFactory.setMongoClient(clientAdapter);
        }
        if(ToolsKit.isEmpty(defaultClientId)) {
            defaultClientId = clientAdapterList.get(0).getId();
        }
        DbClientFactory.setMongoDefaultClientId(defaultClientId);
    }

    /**
     *  启动插件，先进行链接，再设置Dao Bean 到Ioc集合
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        // 链接数据
        for (Iterator<Map.Entry<String, MongoClientAdapter>> iterator = DbClientFactory.getMongoDbClients().entrySet().iterator(); iterator.hasNext();) {
            iterator.next().getValue().getClient();
        }
        addBeanToIocMap();
    }

    @Override
    public void stop() throws Exception {
        for (Iterator<Map.Entry<String, MongoClientAdapter>> iterator = DbClientFactory.getMongoDbClients().entrySet().iterator(); iterator.hasNext();) {
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
                if (ToolsKit.isNotEmpty(importAnnot) && MongoDao.class.equals(field.getType())) {
                    ParameterizedType paramType = (ParameterizedType) field.getGenericType();
                    Type[] types = paramType.getActualTypeArguments();
                    if(ToolsKit.isNotEmpty(types)) {
                        // <>里的泛型类
                        Class<?> paramTypeClass = ClassKit.loadClass(types[0].getTypeName());
                        String key = ToolsKit.isEmpty(importAnnot.client()) ? DbClientFactory.getMongoDefaultClientId() : importAnnot.client();
                        Object daoObj = MongoUtils.getMongoDao(key, paramTypeClass);
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
