package com.duangframework.mvc.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.duangframework.exception.MvcException;
import com.duangframework.exception.ValidatorException;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.ObjectKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Bean;
import com.duangframework.mvc.dto.ApiDto;
import com.duangframework.mvc.dto.ReturnDto;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.utils.DataType;
import com.duangframework.utils.GenericsUtils;
import com.duangframework.utils.TypeConverter;
import com.duangframework.vtor.annotation.VtorKit;
import com.duangframework.vtor.core.VtorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

/**
 * 参数注入到方法体, 先根据参数名取出请求对象里的值后，再判断是否存在验证注解，如果有验证注解，则验证
 * 验证不通过，则抛出异常中止流程
 *
 * @author Created by laotang
 * @date createed in 2018/6/28.
 */
public class ParameterInvokeMethod {

    private static final Logger logger = LoggerFactory.getLogger(ParameterInvokeMethod.class);

    /**
     * 将请求参数转换为Object[], 注入到Method里
     *
     * @param controller       执行的控制器
     * @param method         执行的方法
     * @param paramNameArray 执行方法里的参数变量名
     * @return
     */
    public static Object[] getParameterValues(BaseController controller, Method method, String[] paramNameArray) {
        Parameter[] methodParams = method.getParameters();
        if (ToolsKit.isEmpty(methodParams)) {
            return  null;
        }
        Object[] requestParamValueObj = null;
        if (methodParams.length != paramNameArray.length) {
            throw new MvcException("参数长度不一致!");
        }
        IRequest request = controller.getRequest();
        requestParamValueObj = new Object[methodParams.length];
        for (int i = 0; i < methodParams.length; i++) {
            System.out.println(methodParams[i].getParameterizedType().getClass());
            Class<?> parameterType = methodParams[i].getType();
            // 取参数里的泛型
            Type type = methodParams[i].getParameterizedType();
            if(ToolsKit.isNotEmpty(type)) {
                Type[] typeParams = ((ParameterizedType) type).getActualTypeArguments();
                if(typeParams.length > 0) {
                    type = typeParams[0];
                }
            }
            Annotation[] annotations = methodParams[i].getAnnotations();
            String paramValue = request.getParameter(paramNameArray[i]);
            boolean isBean = DataType.isBeanType(parameterType);
            if(isBean) {
                requestParamValueObj[i] = invokeBean(request, parameterType, annotations, type, i);
                continue;
            } else if(ToolsKit.isEmpty(paramValue)) { // 方法有参数，但请求没有传递参数值时，要设置该参数类型的默认值，以防抛出空指针异常
                requestParamValueObj[i]  = getDefualtValueOnType(parameterType);
                continue;
            } else if (DataType.isString(parameterType)) {
                requestParamValueObj[i] = paramValue;
            } else if (DataType.isInteger(parameterType) || DataType.isIntegerObject(parameterType)) {
                requestParamValueObj[i] = Integer.parseInt(paramValue);
            } else if (DataType.isLong(parameterType) || DataType.isLongObject(parameterType)) {
                requestParamValueObj[i] =Long.parseLong(paramValue);
            } else if (DataType.isDouble(parameterType) || DataType.isDoubleObject(parameterType)) {
                requestParamValueObj[i] = Double.parseDouble(paramValue);
            } else if (DataType.isDate(parameterType)) {
                requestParamValueObj[i] = ToolsKit.parseDate(paramValue, ConstEnums.DEFAULT_DATE_FORMAT_VALUE.getValue());
            } else if (DataType.isTimestamp(parameterType)) {
                requestParamValueObj[i] = ToolsKit.parseDate(paramValue, ConstEnums.DEFAULT_DATE_FORMAT_VALUE.getValue());
            } else if(DataType.isListType(parameterType) || DataType.isSetType(parameterType) || DataType.isQueueType(parameterType)) {
                requestParamValueObj[i] = invokeCollention(request, parameterType);
            } else if(DataType.isMapType(parameterType) ) {
                requestParamValueObj[i] = invokeMap(request);
            }

            //返回前，根据验证注解，进行参数数据验证
            if (ToolsKit.isNotEmpty(annotations)) {
                try {
                    // 有可能会有多个注解，所以要遍历一下
                    for (Annotation annotation : annotations) {
//                        System.out.println(annotation.annotationType() + "                      " + parameterType.getName() + "                  " + paramNameArray[i] + "              " + paramValue);
                        VtorFactory.validator(annotation, parameterType, paramNameArray[i], paramValue);
                    }
                } catch (Exception e) {
                    throw new ValidatorException(e.getMessage(), e);
                }
            }
        }
        return requestParamValueObj;
    }

    /**
     * List, Set, Queue类型验证
     * @param request
     * @param parameterType
     * @return
     */
    private static Object invokeCollention(IRequest request, Class<?> parameterType) {
        String json = request.getParameter(ConstEnums.INPUTSTREAM_STR_NAME.getValue());
        List entityList = ToolsKit.jsonParseArray(json, parameterType);
        try {
            VtorKit.validate(entityList);
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
        return entityList;
    }

    /**
     * Map类型验证
     * @param request
     * @return
     */
    private static Object invokeMap(IRequest request) {
        String json = request.getParameter(ConstEnums.INPUTSTREAM_STR_NAME.getValue());
        Map entityMap = ToolsKit.jsonParseObject(json, Map.class);
        try {
            VtorKit.validate(entityMap);
        }  catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
        return entityMap;
    }

    private static Object invokeBean2(IRequest request, Class parameterType) {
        Field[] fields = parameterType.getFields();
        Object obj = null;
        try {
            obj = ObjectKit.newInstance(parameterType);
            for(Field field : fields) {
                System.out.println(field.getName()+"                         "+field.getType()+"                          "+field.getGenericType());
                String fieldName = field.getName();
                Object fieldValue = request.getParameter(fieldName);
                if(ToolsKit.isNotEmpty(fieldValue)) {
                    ObjectKit.setField(obj, field, fieldValue, field.getType());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private static Object invokeBean3(IRequest request, Class parameterType) {
        Method[] methods = parameterType.getMethods();
        Object obj = null;
        try {
            obj = ObjectKit.newInstance(parameterType);
            for(Method method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith("set") == false) {// only setter method
                    continue;
                }
                Class<?>[] types = method.getParameterTypes();
                if (types.length != 1) {                    // only one parameter
                    continue;
                }
                System.out.println("types[0]: " + types[0].getTypeName());
                System.out.println("types[0]: " + types[0].getName());
                System.out.println("types[0]: " + types[0].getGenericSuperclass());
                String attrName = methodName.substring(3);
                Object value = request.getParameter(ToolsKit.firstCharToLowerCase(attrName));
                if(ToolsKit.isNotEmpty(value)) {
                    method.invoke(obj, TypeConverter.convert(types[0], value));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * Bean类型验证
     * @param request   请求对象
     * @param parameterType     参数类型
     * @param annotation            注解对象
     * @param index                     索引位置
     * @return
     */
    private static Object invokeBean(IRequest request, Class parameterType, Annotation[] annotation, Type type, int index) {
        Object entity = null;
        // 如果是继承了IdEntity或对象有设置Bean注解或在参数前设置了Bean注解， 则认为是要转换为Bean对象并验证
        String json = request.getParameter(ConstEnums.INPUTSTREAM_STR_NAME.getValue());
        System.out.println("genricTypeClass: " + parameterType);
        if(parameterType.equals(ApiDto.class)){
            Map<String,Object> jsonMap = ToolsKit.jsonParseObject(json, Map.class);
            Object dataJson = jsonMap.get(ApiDto.DATA_FIELD);
            if(ToolsKit.isNotEmpty(dataJson)) {
                Object genricEntity = ((JSONObject)dataJson).toJavaObject(type);
                entity = new ApiDto(jsonMap.get(ApiDto.TOKENID_FIELD).toString(), genricEntity);
            }
        } else {
            entity = ToolsKit.jsonParseObject(json, parameterType);
        }
        if(ToolsKit.isEmpty(entity)) {
            logger.warn("invokeBean: json字符串转换为Object时出错，json字符串可能是空，所以返回null退出...");
            return null;
        }
        boolean isBean = DataType.isBeanType(parameterType)
                || entity instanceof Serializable
                || (ToolsKit.isNotEmpty(annotation) && Bean.class.equals(annotation[index].annotationType()));
        if(!isBean) {
            logger.warn("请注意对象或集合元素是否实现[ java.io.Serializable ]接口及设置了[ @Bean ]注解");
            return null;
        }

        // 如果Bean的话，无需在参数添加注解，遍历bean里的field进行判断是否需要验证
        try {
            if(isBean&& ToolsKit.isNotEmpty(entity)) {VtorKit.validate(entity);}
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
        return entity;
    }

    private static Object getDefualtValueOnType(Class<?> paramType) {
        if(DataType.isString(paramType)) {
            return "";
        } else if(DataType.isInteger(paramType) || DataType.isIntegerObject(paramType)) {
            return 0;
        } else {
            return null;
        }
    }
}
