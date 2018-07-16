package com.duangframework.vtor.core;


import com.duangframework.kit.ClassKit;
import com.duangframework.kit.ObjectKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.scan.ScanClassFactory;
import com.duangframework.mvc.annotation.Bean;
import com.duangframework.vtor.core.template.AbstractValidatorTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public final class VtorFactory {

	private static final Logger logger = LoggerFactory.getLogger(VtorFactory.class);

	private static final Map<String,Field[]> map = new HashMap<String, Field[]>();

	private static Map<Class<?>, AbstractValidatorTemplate> VALIDATOR_HANDLE_MAP = new HashMap<>();

	private static void init() {
		if(ToolsKit.isEmpty(VALIDATOR_HANDLE_MAP)) {
			String packagePath = AbstractValidatorTemplate.class.getPackage().getName();
			List<Class<?>> validatorHandleList = ScanClassFactory.getAllClass(packagePath, null);
			for (Class<?> clazz : validatorHandleList) {
				if (!ClassKit.supportInstance(clazz)) {
					continue;
				}
				AbstractValidatorTemplate validatorTemplate = ObjectKit.newInstance(clazz);
				VALIDATOR_HANDLE_MAP.put(validatorTemplate.annotationClass(), validatorTemplate);
			}
		}
	}

	/**
	 * 按单个注解进行验证
	 * @param annotationType		注解类型
	 * @param parameterType			参数类型
	 * @param paramName				参数名称
	 * @param paramValue				参数值
	 * @throws Exception
	 */
	public static void validator(Annotation annotationType,  Class<?> parameterType,  String paramName, Object paramValue) throws Exception {
		init();
		Class<? extends Annotation> annotationClass = annotationType.annotationType();
		if(ToolsKit.isNotEmpty(VALIDATOR_HANDLE_MAP) &&VALIDATOR_HANDLE_MAP.containsKey(annotationClass)) {
			VALIDATOR_HANDLE_MAP.get(annotationClass).vaildator(annotationType, parameterType, paramName, paramValue);
		}
	}

	/**
	 * List, Set集合验证
	 * 集合里的元素必须实现了java.io.Serializable接口 且  设置了@VtorBean注解
	 * @param beanCollections
	 * @throws Exception
	 */
	public static void validator(Collection<Object> beanCollections) throws Exception {
		if(ToolsKit.isEmpty(beanCollections)) {
			throw new NullPointerException("collection is null");
		}
		init();
		boolean isValidator = false;
		for(Object item : beanCollections) {
			if(item instanceof  Serializable || item.getClass().isAnnotationPresent(Bean.class)) {
				isValidator = true;
				validator(item);
			}
		}
		if(!isValidator) loggerInfo();
	}

	/**
	 * Map集合验证
	 * 集合里的元素value值必须实现了java.io.Serializable接口 且  设置了@VtorBean注解
	 * @param beanMap
	 * @throws Exception
	 */
	public static void validator(Map<String, Object> beanMap) throws Exception {
		if(ToolsKit.isEmpty(beanMap)) {
			throw new NullPointerException("map is null");
		}
		init();
		boolean isValidator = false;
		for(Iterator<Map.Entry<String, Object>> iterator = beanMap.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, Object> entry = iterator.next();
			Object item = entry.getValue();
			if(item instanceof Serializable || item.getClass().isAnnotationPresent(Bean.class)) {
				isValidator =true;
				validator(item);
			}
		}
		if(!isValidator) loggerInfo();
	}

	private static void loggerInfo() {
		logger.warn("框架并没进行注解验证，请注意对象或集合元素是否实现[ java.io.Serializable ]接口及设置了[ @Bean ]注解");
	}

    /**
     * 验证bean
     * @param bean
     * @throws Exception
     */
    public static void validator(Object bean) throws Exception {
		init();
		String beanName = bean.getClass().getName();
        Field[] fields = map.get(beanName);
        if( null == fields){
            fields = bean.getClass().getDeclaredFields();
        }

        boolean isValidatorBean = false;
        for(int i=0; i<fields.length; i++){
            Field field = fields[i];
            Annotation[] annotationArray = field.getAnnotations();
            if(ToolsKit.isEmpty(annotationArray)) {
            	return;
			}
            for(Annotation annotation : annotationArray) {
            	// 如果在验证处理器集合包含了该验证注解则进行验证，并将该bena添加到map缓存，以便再次使用时直接取出字段属性进行验证
                if(VALIDATOR_HANDLE_MAP.containsKey(annotation.annotationType())) {
                    Object fieldValue = ObjectKit.getFieldValue(bean, field);
                    Class<?> fieldType = field.getType();
                    String fieldName = field.getName();
                    validator(annotation, fieldType, fieldName, fieldValue);
                    isValidatorBean = true;
                }
            }
        }
        // 添加到集合
        if(isValidatorBean){
            map.put(beanName, fields);
        } else {
        	loggerInfo();
		}
    }
}
