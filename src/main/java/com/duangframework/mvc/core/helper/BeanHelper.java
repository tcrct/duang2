package com.duangframework.mvc.core.helper;

import com.duangframework.exception.MvcException;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.ObjectKit;
import com.duangframework.kit.ThreadPoolKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Proxy;
import com.duangframework.mvc.core.BaseController;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.mvc.proxy.IProxy;
import com.duangframework.mvc.proxy.ProxyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

/**
 * Bean辅助类
 * 用于将框架所需的类实例化成对象， 以供使用
 *
 * @author Created by laotang
 * @date createed in 2018/6/22.
 */
public class BeanHelper {

    private static final Logger logger = LoggerFactory.getLogger(BeanHelper.class);
    private static Set<String> excludedMethodName = ObjectKit.buildExcludedMethodName();

    /**
     * Bean Map [已实例]
     */
    private static Map<String, List<Object>> beanMap = new ConcurrentHashMap<>();
    /**
     * ioc所需要的bean
     */
    private static Map<String, Object> iocBeanMap = new HashMap<>();

    /**
     * 实例化bean用的临时任务集合
     */
    private static Map<String, FutureTask<List<Object>>> futureTaskMap = new HashMap<>();

    static {
        for (ConstEnums.ANNOTATION_CLASS annotationClass : ConstEnums.ANNOTATION_CLASS.values()) {
            // 如果需要实例化
            if (annotationClass.getInstance()) {
                String key = annotationClass.getClazz().getName();
                List<Object> beanList = beanMap.get(key);
                if (ToolsKit.isEmpty(beanList)) {
                    beanList = new ArrayList<>();
                }
                List<Class<?>> classList = ClassHelper.getClassList(annotationClass.getClazz());
                if (ToolsKit.isNotEmpty(classList)) {
                    FutureTask<List<Object>> futureTask = ThreadPoolKit.execute(new InstanceBeanTask(classList, beanList));
                    futureTaskMap.put(key, futureTask);
                }
            }
        }
        try {
            for( Iterator<Map.Entry<String, FutureTask<List<Object>>>> iterator = futureTaskMap.entrySet().iterator(); iterator.hasNext();){
                Map.Entry<String, FutureTask<List<Object>>> entry = iterator.next();
                beanMap.put(entry.getKey(), entry.getValue().get());
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * 返回所有Bean，其中：</br>
     *          key为<p>ConstEnums.ANNOTATION_CLASS</p>里指定的Name,
     *          value为Bean的List集合
     * @return  Map<String, List<Object>>
     */
    public static Map<String, List<Object>> getBeanMap() {
        return beanMap;
    }

    public static List<Object> getControllerBeanList() {
        return beanMap.get(ConstEnums.ANNOTATION_CLASS.CONTROLLER_ANNOTATION.getName());
    }
    public static List<Object> getServiceBeanList() {
        return beanMap.get(ConstEnums.ANNOTATION_CLASS.SERVICE_ANNOTATION.getName());
    }
    public static List<Object> getEntityBeanList() {
        return beanMap.get(ConstEnums.ANNOTATION_CLASS.ENTITY_ANNOTATION.getName());
    }

    public static List<Object> getWebSocketBeanList() {
        return beanMap.get(ConstEnums.ANNOTATION_CLASS.WEBSOCKET_ANNOTATION.getName());
    }

    /**
     *  根据Class取出对应的Ioc Bean
     */
    public static <T> T getBean(Class<?> clazz, Object targetObj) {
        String key = getBeanClassName(clazz);
        if (!iocBeanMap.containsKey(key) && !targetObj.getClass().equals(Class.class)) {
            throw new MvcException(targetObj.getClass().getName() + " 无法根据类名获取实例: " + clazz + " , 请检查是否后缀名是否正确！");
        }
        return (T)iocBeanMap.get(key);
    }

    public static void setBean(Object targetObj) {
        String key = getBeanClassName(targetObj.getClass());
        iocBeanMap.put(key, targetObj);
    }

    /**
     *  根据Class取出对应的Ioc Bean
     */
    public static <T> T getBean(Class<?> clazz) {
        String key = getBeanClassName(clazz);
        if (!iocBeanMap.containsKey(key)) {
            throw new MvcException("无法根据类名["+clazz.getName()+"]获取实例 , 请检查！");
        }
        return (T)iocBeanMap.get(key);
    }

    /**
     * 所需要的Ioc Bean集合
     * @return
     */
    public static Map<String, Object> getIocBeanMap() {
        if(ToolsKit.isEmpty(iocBeanMap)) {

            List<Object> iocBeanList = new ArrayList<Object>() {
                {
                    this.addAll(getControllerBeanList());
                    this.addAll(getServiceBeanList());
                    this.addAll(getWebSocketBeanList());
                }
            };

            for (Object bean : iocBeanList) {
                iocBeanMap.put(getBeanClassName(bean.getClass()), bean);
            }
        }
        return iocBeanMap;
    }

    /**
     * 实例化对对象
     */
    static class InstanceBeanTask implements Callable<List<Object>> {

        private List<Class<?>> classList;
        private List<Object> beanList;
        private Set<String> excludedMethodName;

        public InstanceBeanTask(List<Class<?>> sourceClassList, List<Object> sourceBeanList) {
            classList = sourceClassList;
            beanList = sourceBeanList;
            excludedMethodName = ObjectKit.buildExcludedMethodName(BaseController.class);
        }

        @Override
        public List<Object> call() {
            try {
                for (Iterator<Class<?>> iterator = classList.iterator(); iterator.hasNext(); ) {
                    Class<?> clazz = iterator.next();
                    // 是接口或抽象类则退出本次循环
                    if (!ClassKit.supportInstance(clazz)) {
                        continue;
                    }
                    Object bean = buildIocBean(clazz);
                    if (ToolsKit.isNotEmpty(bean)) {
                        beanList.add(bean);
                    }
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
            return beanList;
        }

        private Object buildIocBean(Class<?> clazz) throws Exception {
            Method[] methods = clazz.getMethods();
            Object bean = null;
            List<Proxy> proxyAnnotaionList = new ArrayList<>();
            // 遍历所有方法，判断是否存在Proxy注解，如果有则添加到List集合
            for(Method method : methods) {
                if(ObjectKit.isExcludeMethod(method, excludedMethodName)) {
                    continue;
                }
                Proxy proxy = method.getAnnotation(Proxy.class);
                if(ToolsKit.isNotEmpty(proxy)) {
                    proxyAnnotaionList.add(proxy);
                }
            }
            /** 如果Proxy注解集合不为空，则将类转换为代理类
            *    注意： 只要有一个方法有设置Proxy注解，则这个类的所有方法都会用代理的方式，所以要在代理处理类里，即实现了IPorxy的类里，
             *    根据targetMethod来判断是否要对这个方法进行处理。
             *    如果一个类里指定了添加了多个@Proxy注解，按注解里的index顺序执行
             *    如果一个方法里指定了多个代理类，则按书写顺序，从左至右执行
             */
            if(ToolsKit.isNotEmpty(proxyAnnotaionList)) {
                // 排序，Proxy.index()数字小的靠前
                int size = proxyAnnotaionList.size();
                for(int i=0; i<size-1; i++){
                    for(int j=0; j<size-1-i; j++){
                        if(proxyAnnotaionList.get(j).index() > proxyAnnotaionList.get(j+1).index()){
                            Proxy tempProxy = proxyAnnotaionList.get(j);
                            proxyAnnotaionList.set(j, proxyAnnotaionList.get(j+1));
                            proxyAnnotaionList.set(j+1, tempProxy);
                        }
                    }
                }
//                System.out.println(proxyAnnotaionList.get(0).value()[0].getId());
                bean= buildProxyBean(clazz, proxyAnnotaionList);
            } else {
                bean = ObjectKit.newInstance(clazz);
            }
            if(null == bean) {
                throw new NullPointerException(clazz.getName() +" buildIocBean is null, place check in...");
            }
            return bean;
        }

        /**
         * 创建代理对象
         * @param clazz         被代理的类
         * @param proxyAnnotaionList        代理注解集合, 集合每个元素要注意顺序，按编写时的顺序执行代理
         *   <p>使用方法：</p>
         *  <p>Proxy({xxxProxy, zzzProxy, ...})</p>
         *
         * @return
         */
        private Object buildProxyBean(Class<?> clazz, List<Proxy> proxyAnnotaionList) {
            List<IProxy> proxyList = new ArrayList<>();
            for(Proxy proxy : proxyAnnotaionList) {
                Class<? extends IProxy>[] proxyClassArray = proxy.value();
                if (ToolsKit.isEmpty(proxyClassArray)) {
                    logger.error("proxy class array is null");
                    return null;
                }
                for (Class<? extends IProxy> proxyClass : proxyClassArray) {
                    IProxy proxyObj = ObjectKit.newInstance(proxyClass);
                    proxyList.add(proxyObj);
                }
            }
            Object instanceObj = null;
            // 实例化类，如果有代理注解则创建代理类
            if (ToolsKit.isNotEmpty(proxyList)) {
                // 代理类
                try {
                    instanceObj = ProxyManager.createProxy(clazz, proxyList);
                } catch (Exception e) {
                    logger.error("buildProxyBean is fail: " + e.getMessage()+" return null...", e);
                }
            }
            return instanceObj;
        }
    }

    /**
     * 取实例化后的Bean名称，因为用CGLIB实现代理后，该类的名称会在原名的基础上添加  $$EnhancerByCGLIB$$xxxx  这样的标识字符串
     * 在BeanHelper.getBean()里，参数是类的名称，会导致不匹配而导致返回null
     * 所以要调用该方法以确保名称一致
     *      其实就是如果$$标识存在， 就将$$后的字符串标识去掉
     * @param clazz
     * @return
     */
    private static String getBeanClassName(Class<?> clazz) {
        String className = clazz.getName();
        int index = className.indexOf("$$");
        if(index>-1 && className.contains("CGLIB$$")) {
            className = className.substring(0, index);
        }
        return className;
    }
}
