package com.duangframework.doclet.api.service;

import com.duangframework.doclet.ApiDocument;
import com.duangframework.doclet.api.dto.MethodDto;
import com.duangframework.doclet.api.dto.MethodListDto;
import com.duangframework.doclet.modle.ClassDocModle;
import com.duangframework.doclet.modle.MethodDocModle;
import com.duangframework.exception.MvcException;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Mock;
import com.duangframework.mvc.annotation.Service;
import com.duangframework.mvc.route.RequestMapping;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Created by laotang
 * @date createed in 2018/7/9.
 */
@Service
public class ApiService {

    // Controller Api 列表 key为Controller name
    private static final Map<String, String> controllerListMap = new TreeMap<>();
    // Controller Method 列表 key为Controller name
    private static final Map<String, MethodListDto> methodListMap = new TreeMap<>();
    // Method 详细 key为Controller name+"."+Method name
    private static final Map<String, MethodDocModle> methodDetailMap = new TreeMap<>();

    public Map<String, String> list() {
        if(ToolsKit.isNotEmpty(controllerListMap)) {
            return controllerListMap;
        }
        List<ClassDocModle> classDocModleList = ApiDocument.getClassDocModleList();
        if(ToolsKit.isEmpty(classDocModleList)) {
            throw new MvcException("请先生成api文档");
        }

        for(Iterator<ClassDocModle> it = classDocModleList.iterator(); it.hasNext();) {
            ClassDocModle docModle = it.next();
            RequestMapping mapping = docModle.getMappingModle();
            String key = docModle.getName();
            String desc = ToolsKit.isEmpty(mapping.getDesc()) ? docModle.getName() : mapping.getDesc();
            controllerListMap.put(key, desc);
            MethodListDto methodListDto = new MethodListDto();
            methodListDto.setControllerDesc(desc);
            methodListDto.setControllerReadme(docModle.getCommentText());
            List<MethodDocModle> methodDocModleList = docModle.getMethods();
            if(ToolsKit.isNotEmpty(methodDocModleList)) {
                List<MethodDto> methodDtoList = new ArrayList<>(methodDocModleList.size());
                for(MethodDocModle methodDocModle : methodDocModleList) {
                    MethodDto methodDto = new MethodDto();
                    String name = methodDocModle.getName();
                    desc = methodDocModle.getCommentText();
                    String uri = mapping.getValue() + "/"+name;
                    RequestMapping requestMapping = methodDocModle.getMappingModle();
                    if(ToolsKit.isNotEmpty(requestMapping)) {
//                        name = ToolsKit.isEmpty(requestMapping.getValue()) ? requestMapping.getValue() : name;
                        desc = ToolsKit.isNotEmpty(requestMapping.getDesc()) ? requestMapping.getDesc() : desc;
                        uri = mapping.getValue() + (ToolsKit.isEmpty(requestMapping.getValue()) ? "/"+name : requestMapping.getValue());
                    }
                    String method = "";
                    if(ToolsKit.isNotEmpty(requestMapping) && ToolsKit.isNotEmpty(requestMapping.getMethod())) {
                        method = requestMapping.getMethod();
                    }
                    methodDto.setName(name);
                    methodDto.setDesc(desc);
                    methodDto.setMethod(method);
                    methodDto.setUri(uri.toLowerCase());
                    requestMapping.setValue(uri.toLowerCase());
                    methodDtoList.add(methodDto);
                    methodDetailMap.put(key+"."+name, methodDocModle);
                }
                methodListDto.setMethodDtoList(methodDtoList);
            }
            methodListMap.put(key, methodListDto);
        }
        return controllerListMap;
    }

    /**
     * 返回方法列表DTO
     * @param key       Controller Name
     * @return
     */
    public MethodListDto methodList(String key) {
        if(ToolsKit.isEmpty(methodListMap) || !methodListMap.containsKey(key)) {
            throw new MvcException("methodListMap is null");
        }
        return methodListMap.get(key);
    }

    /**
     * 返回方法详细DTO
     * @param key       Controller Name+"."+Method Name
     * @return
     */
    public MethodDocModle methodDetail(String key) {
        if(ToolsKit.isEmpty(methodDetailMap) || !methodDetailMap.containsKey(key)) {
            throw new MvcException("methodListMap is null or key["+key+"] not exist");
        }
        return methodDetailMap.get(key);
    }


    /**
     * 返回方法详细DTO
     * @param key       Controller Name+"."+Method Name
     * @return
     */
    public Object mock(String key) {
        if(ToolsKit.isEmpty(key) || "void".equalsIgnoreCase(key)) {
            return new HashMap<String ,String>();
        }

        int startIndex = key.indexOf("<");
        int endIndex = key.indexOf(">");
        boolean isArray = key.toLowerCase().startsWith("list") || key.toLowerCase().startsWith("set");
        if(startIndex > -1 && endIndex > -1) {
            key = key.substring(startIndex+1, endIndex);
        }

        Class<?> clazz = null;
        try {
            clazz = ClassKit.loadClass(key);
        } catch (Exception e) {
            throw new MvcException(key +" is not exist");
        }

        Field[] fields = clazz.getDeclaredFields();
        if(ToolsKit.isEmpty(fields)) {
            return null;
        }
        Map<String ,String> map = new HashMap<>();
        for(Field field : fields) {
            Mock mock = field.getAnnotation(Mock.class);
            if(ToolsKit.isNotEmpty(mock)) {
                map.put(field.getName(), mock.value());
            }
        }

        if(isArray) {
            List<Map<String, String>> list = new ArrayList<Map<String, String>>() {
                {
                    this.add(map);
                }
            };
            return list;
        }
        return map;
    }


}
