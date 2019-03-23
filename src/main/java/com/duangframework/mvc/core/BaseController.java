package com.duangframework.mvc.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.duangframework.exception.IException;
import com.duangframework.exception.MvcException;
import com.duangframework.exception.ServiceException;
import com.duangframework.kit.ObjectKit;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.ApiDto;
import com.duangframework.mvc.dto.ReturnDto;
import com.duangframework.mvc.dto.upload.DownLoadStream;
import com.duangframework.mvc.dto.upload.FileItem;
import com.duangframework.mvc.dto.upload.UploadFile;
import com.duangframework.mvc.http.handler.UploadFileHandle;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.mvc.http.enums.ContentTypeEnums;
import com.duangframework.mvc.render.*;
import com.duangframework.utils.DataType;
import com.duangframework.utils.GenericsUtils;
import com.duangframework.utils.TypeConverter;
import com.duangframework.vtor.core.VtorFactory;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by laotang on 2018/6/15.
 */
public abstract class BaseController {

    private static Logger logger = LoggerFactory.getLogger(BaseController.class);
    private static final Object[] NULL_ARGS = new Object[0];

    private IRequest request;
    private IResponse response;
    private Render render;

    public IRequest getRequest() {
        return request;
    }

    public IResponse getResponse() {
        return response;
    }

    public void init(IRequest request, IResponse response) {
        this.request = request;
        this.response = response;
        this.render = null;     //防止Controller没写returnXXXX方法时，返回上一次请求结果到到客户端
        if("dev".equalsIgnoreCase(PropKit.get(ConstEnums.PROPERTIES.USE_ENV.getValue()))) {
            printRequestInfo();
        }
    }

    private void printRequestInfo() {
        logger.info("******************************************************************************");
        logger.info("###########RequestDate:   " + ToolsKit.formatDate(getRequestDate(), PropKit.get(ConstEnums.DEFAULT_DATE_FORMAT.getValue(), "yyyy-MM-dd HH:mm:ss")));
        logger.info("###########RequestHeader: " + request.getHeader(HttpHeaderNames.USER_AGENT.toString()));
        logger.info("###########RequestURL:    " + request.getRequestURL());
        logger.info("###########RemoteMethod:  " + request.getMethod());
        logger.info("###########getContentType:  " + request.getContentType());
        logger.info("###########RequestValues: " + ToolsKit.toJsonString(getAllParams()));
        logger.info("******************************************************************************");
    }

    /**
     * 取出请求日期时间
     * @return
     */
    private Date getRequestDate() {
        String d = request.getHeader(HttpHeaderNames.DATE.toString());
        if (ToolsKit.isEmpty(d)) {
            return new Date();
        }
        try {
            return new Date(Long.parseLong(d));
        } catch (Exception e) {
            return ToolsKit.parseDate(d, PropKit.get(ConstEnums.DEFAULT_DATE_FORMAT.getValue(), "yyyy-MM-dd HH:mm:ss"));
        }
    }

    /**
     * 取出所有请求的参数
     * @return
     */
    private Map<String, Object> getAllParams() {
        Map<String, Object> requestParams = new HashMap<>(request.getParameterMap());
        if(ToolsKit.isNotEmpty(requestParams)) {
            requestParams.remove(ConstEnums.INPUTSTREAM_STR_NAME.getValue());
        }
        return requestParams;
    }

    /**
     * 返回XML格式
     * @param xml
     */
    public void returnXml(String xml) {
        render = new XmlRender(xml);
    }

    public BaseController setValue(String key, Object obj) {
        request.setAttribute(key, obj);
        return this;
    }

    /**
     * 取出请求值
     *
     * @param key
     *            请求参数的key
     * @return 如果存在则返回字符串内容,不存在则返回""
     */
    public String getValue(String key) {
        String values = "";
        try {
            values = request.getParameter(key) + "";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return values;
    }

    private Object getValueObj(String key) {
        Object values = null;
        try {
            values = request.getParameter(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValueObj(String key, Class<?> cls) {
        Object values = getValueObj(key);
        if(ToolsKit.isEmpty(values)) {
            return null;
        }
        if(DataType.isBaseType(cls)) {
            logger.warn("基础类型请使用getValue或getXXXValue方法");
            return null;
        }
        String jsonText = ToolsKit.toJsonString(values);
        if(ToolsKit.isEmpty(jsonText)) {
            try {
                return ObjectKit.newInstance(cls);
            } catch (Exception e) {
                return null;
            }
        }
        return (T) ToolsKit.jsonParseObject(jsonText, cls);
    }

    /**
     * 取出请求值
     *
     * @param key
     *            请求参数的key
     * @return 如果存在则返回字符串内容,不存在则返回null
     */
    public String[] getValues(String key) {
        String[] values = null;
        String errorMsg = "";
        try {
            values = request.getParameterValues(key);
            if (ToolsKit.isEmpty(values)) {
                values = ToolsKit.isEmpty(request.getAttribute(key)) ? null : (String[]) request.getAttribute(key);
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            try{
                Object valObj = request.getAttribute(key);
                if(valObj instanceof JSONArray) {
                    JSONArray array = (JSONArray)valObj;
                    values = array.toArray(new String[]{});
                }
            }catch(Exception e1) {
                errorMsg = e1.getMessage();
            }
        }
        if(ToolsKit.isEmpty(values)) {
            logger.warn(errorMsg);
        }
        return values;
    }

    /**
     * 根据key取请求值，并将数据转换为int返回
     *
     * @param key
     *            请求参数的key
     * @return 如果值为空或转换异常时，返回-1
     */
    protected int getIntValue(String key) {
        String value = getValue(key);
        if (ToolsKit.isNotEmpty(value)) {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                try {
                    logger.warn(e.getMessage(), e);
                    return getNumberValue(value).intValue();
                } catch (Exception e1) {
                    throw new MvcException(e1.getMessage(), e1);
                }
            }
        }
        return -1;
    }

    /**
     * 根据key取请求值，并将数据转换为long返回
     *
     * @param key
     *            请求参数的key
     * @return 如果值为空或转换异常时，返回-1
     */
    protected long getLongValue(String key) {
        String value = getValue(key);
        if (ToolsKit.isNotEmpty(value)) {
            try {
                return Long.parseLong(value);
            } catch (Exception e) {
                try {
                    logger.warn(e.getMessage(), e);
                    return getNumberValue(value).longValue();
                } catch (Exception e1) {
                    throw new MvcException(e1.getMessage(), e1);
                }
            }
        }
        return -1L;
    }

    /**
     * 根据key取请求值，并将数据转换为float返回
     *
     * @param key
     *            请求参数的key
     * @return 如果值为空或转换异常时，返回-1
     */
    protected float getFloatValue(String key) {
        String value = getValue(key);
        if (ToolsKit.isNotEmpty(value)) {
            try {
                return Float.parseFloat(value);
            } catch (Exception e) {
                try {
                    logger.warn(e.getMessage(), e);
                    return getNumberValue(value).floatValue();
                } catch (Exception e1) {
                    throw new MvcException(e1.getMessage(), e1);
                }
            }
        }
        return -1f;
    }

    /**
     * 根据key取请求值，并将数据转换为double返回
     *
     * @param key
     *            请求参数的key
     * @return 如果值为空或转换异常时，返回-1
     */
    protected double getDoubleValue(String key) {
        String value = getValue(key);
        if (ToolsKit.isNotEmpty(value)) {
            try {
                return Double.parseDouble(value);
            } catch (Exception e) {
                throw new MvcException(e.getMessage(), e);
            }
        }
        return -1d;
    }

    private Double getNumberValue(String key) {
        try {
            return  getDoubleValue(key);
        } catch (Exception e) {
            throw new MvcException(e.getMessage(), e);
        }
    }

    /**
     * 根据key取请求值，并将数据转换为Boolean返回
     *
     * @param key
     *            请求参数的key
     * @return 如果值为空或转换异常时，返回false
     */
    protected Boolean getBooleanValue(String key) {
        String value = getValue(key);
        if (ToolsKit.isNotEmpty(value)) {
            try {
                return Boolean.parseBoolean(value);
            } catch (Exception e) {
                throw new MvcException(e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * 根据key取请求值，并将数据转换为Date返回
     *
     * @param key
     *            请求参数的key
     * @return 如果值为空或转换异常时，返回null
     */
    protected Date getDateValue(String key) {
        String value = getValue(key);
        if (ToolsKit.isNotEmpty(value)) {
            try {
                long millisecond = Long.parseLong(value);
                return new Date(millisecond);
            } catch (Exception ex) {
                try {
                    return ToolsKit.parseDate(value, "yyyy-MM-dd HH:mm:ss");
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 取请求body
     * @return
     */
    private Object getBodyString() {
        return getValue(ConstEnums.INPUTSTREAM_STR_NAME.getValue());
    }

    /**
     * 取出请求对象的json字符串
     * @return
     */
    protected String getJson() {
        Object inputStreamObj = getBodyString();
        String jsonString = "";
        if (ToolsKit.isNotEmpty(inputStreamObj)) {
            if(inputStreamObj instanceof String[]) {
                jsonString = ((String[])inputStreamObj)[0];
            } else {
                jsonString = (String) inputStreamObj;
            }
        }
        return jsonString;
    }

    /**
     * 取出请求对象的xml字符串
     * @return
     */
    protected String getXml() {
        return getJson();
    }

    /**
     * 取出请求body对象的InputStream对象
     * @return
     */
    public InputStream getInputStream() {
        InputStream is = null;
        Object inputStreamObj = getBodyString();
        try{
            if(ToolsKit.isNotEmpty(inputStreamObj)) {
                is = IOUtils.toInputStream((String)inputStreamObj, ConstEnums.PROPERTIES.DEFAULT_ENCODING.getValue());
            }
        }catch(Exception e) {
            logger.warn("Controller.getInputStream() fail: " + e.getMessage() + " return null...", e);
        }
        return is;
    }

    /**
     * 根据类，取出请求参数并将其转换为Bean对象返回
     * 默认验证
     * @param tClass            要转换的类
     * @return
     */
    protected <T> T getBean(Class<T> tClass) {
        return getBean(tClass, ReturnDto.DATA_FIELD);
    }

    protected <T> T getBean(TypeReference tClass) {
        return (T)ToolsKit.jsonParseObject(getJson(), tClass);
    }

    /**
     * 根据类，取出请求参数并将其转换为Bean对象返回
     * 默认验证
     * @param tClass            要转换的类
     * @return
     */
    protected <T> T getBean(Class<T> tClass, String dataKey) {
        return getBean(tClass, dataKey, true);
    }

    /**
     * 根据类，取出请求参数并将其转换为Bean对象返回
     * @param tClass            要转换的类
     * @param isValidator     是否验证
     * @param <T>
     * @return
     */
    protected <T> T getBean(Class<T> tClass, String dataKey, boolean isValidator) {
        List<T> resultBeanList = new ArrayList<>();
        T resultBean = null;
        boolean isApiBean = false;
        String contentType = request.getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
        try {
            if (ToolsKit.isEmpty(contentType) || contentType.contains(ContentTypeEnums.FORM.getValue())) {
                String paramsJson = ToolsKit.toJsonString(getAllParams());
                resultBean = ToolsKit.jsonParseObject(paramsJson, tClass);
            }else if(contentType.contains(ContentTypeEnums.JSON.getValue())) {
                String jsonString = getJson();
                Type genSuperClass = tClass.getGenericSuperclass();
//                System.out.println(tClass.getGenericSuperclass().getTypeName() + "      tClass.getSuperclass(): " + tClass.getSuperclass());
                String tokenid = null;
                String dataJson = null;
                if(ApiDto.class.equals(tClass.getSuperclass())) {
                    JSONObject jsonObject = JSONObject.parseObject(jsonString);
                    tokenid = jsonObject.getString(ReturnDto.TOKENID_FIELD);
                    Object dataJsonObject = jsonObject.get(dataKey);
                    if(dataJsonObject instanceof  JSONObject){
                        dataJson = ((JSONObject) dataJsonObject).toJSONString();
                    }
                    isApiBean = null != tokenid && null != dataJson;
                }
                if (isApiBean && genSuperClass instanceof ParameterizedType) {
                    ParameterizedType genType = (ParameterizedType)genSuperClass;
//                    System.out.println(genType.getRawType());
//                    System.out.println(genType.getTypeName());
                    Type[] paramTypes = genType.getActualTypeArguments();
                    if(ToolsKit.isNotEmpty(paramTypes)) {
                        Type genTypeClass = paramTypes[0];                // ApiDto <> 里的泛型
                        Type rawTypeClass = genType.getRawType();      // ApiDto
                        if(ApiDto.class.getName().equals(rawTypeClass.getTypeName())) {
                            ApiDto apiDto = new ApiDto();
                            if (null != tokenid) {
                                request.setAttribute(ReturnDto.TOKENID_FIELD, tokenid);
                                apiDto.setTokenId(tokenid);
                            }
                            if(null != dataJson) {
                                apiDto.setData(ToolsKit.jsonParseObject(dataJson, genTypeClass));
                                resultBean = (T)apiDto;
                            }
                        }
                    }
                } else {
                    resultBean = ToolsKit.jsonParseObject(jsonString, tClass);
                }
            } else if(contentType.contains(ContentTypeEnums.XML.getValue())) {
                resultBean = ToolsKit.xmlParseObject(getXml(), tClass);
            }
            // 开启验证
            if(isValidator) {
                if(ToolsKit.isNotEmpty(resultBean)) {
                    if(isApiBean) {
                        VtorFactory.validator(((ApiDto)resultBean).getData());
                    } else {
                        VtorFactory.validator(resultBean);
                    }
                } else if(ToolsKit.isNotEmpty(resultBeanList)) {
                    for(int i=0; i<resultBeanList.size(); i++) {
                        VtorFactory.validator(resultBeanList.get(i));
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("getBean is fail : " + e.getMessage(), e);
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return ToolsKit.isNotEmpty(resultBeanList) ? (T)resultBeanList : resultBean;
    }


    public Render getRender(Object resultObj) {
        if(null == render) {
            if(null != resultObj) {
                render = new JsonRender(ToolsKit.buildReturnDto(null, resultObj));
            } else {
                render = new TextRender("controller is not set render value");
            }
        }
        return render;
    }

    /**
     * 返回文本格式
     * @param text
     */
    public void returnText(String text) {
        render = new TextRender(text);
    }


    /**
     * 返回请求成功json
     * @param obj
     */
    protected void returnSuccessJson(Object obj) {
        returnFailJson(null, obj );
    }


    /**
     * 返回请求失败json
     * @param exception
     */
    protected void returnFailJson(Exception exception) {
        returnFailJson(exception, exception.getMessage());
    }

    /**
     * 返回请求失败json
     * @param exception
     * @param obj
     */
    protected void returnFailJson(Exception exception, Object obj) {
        IException iException = null;
        if(null != exception) {
            if (exception instanceof IException) {
                iException = (IException) exception;
            } else {
                logger.warn(exception.getMessage(), exception);
                iException = new ServiceException(exception.getMessage()+"", exception);
            }
        }
        returnJson(ToolsKit.buildReturnDto(iException, obj), null);
    }

    /**
     * 返回JSON格式字符串
     *
     * @param obj           对象
     * @param fieldSet	不返回的字段集合
     */
    private void returnJson(Object obj, Set<String> fieldSet) {
        render = new JsonRender(obj, fieldSet);
    }

    /**
     * 下载文件
     */
    public void download(File file) throws Exception {
        render = new FileRender(file);
    }

    public void download(UploadFile file) throws Exception {
        render = new FileRender(file);
    }

    public void download(File file, boolean isDelete) throws Exception {
        render = new FileRender(file, isDelete);
    }

    public void download(UploadFile file, boolean isDelete) throws Exception {
        render = new FileRender(file, isDelete);
    }

    public void download(DownLoadStream downLoadStream) throws Exception {
        render = new FileRender(downLoadStream);
    }

    public void download(DownLoadStream downLoadStream, boolean isDelete) throws Exception {
        render = new FileRender(downLoadStream, isDelete);
    }

    /**
     * 上传文件部份
     */

    public List<UploadFile> getUploadFiles() {
        return getUploadFiles("", true);
    }

    public List<UploadFile> getUploadFiles(String saveDirectory) {
        return getUploadFiles(saveDirectory, true);
    }

    public List<UploadFile> getUploadFiles(String saveDirectory,  boolean isUUIDName) {
        Enumeration<String> enumeration = request.getAttributeNames();
        List<UploadFile> uploadFileList = new ArrayList<>();
        saveDirectory = saveDirectory.equals("/") ? "" : saveDirectory;
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            Object requestAttribute = request.getAttribute(key);
            if(requestAttribute instanceof FileItem) {
                FileItem fileItem = (FileItem) requestAttribute;
                UploadFileHandle uploadFileRequest = new UploadFileHandle(fileItem, saveDirectory, isUUIDName);
                UploadFile uploadFile = uploadFileRequest.getUploadFile();
                if(ToolsKit.isNotEmpty(uploadFile)) {
                    uploadFileList.add(uploadFile);
                }
            }
        }
        return uploadFileList;
    }
}

