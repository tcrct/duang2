package com.duangframework.kit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.duangframework.db.IdEntity;
import com.duangframework.db.annotation.ConvertField;
import com.duangframework.db.common.Query;
import com.duangframework.encrypt.core.HttpHeaderNames;
import com.duangframework.exception.IException;
import com.duangframework.exception.MvcException;
import com.duangframework.exception.ServiceException;
import com.duangframework.exception.ValidatorException;
import com.duangframework.mvc.annotation.Bean;
import com.duangframework.mvc.dto.*;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.security.SecurityUser;
import com.duangframework.utils.*;
import com.duangframework.vtor.annotation.VtorKit;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by laotang on 2017/10/31.
 */
public final class ToolsKit {

    private static Logger logger = LoggerFactory.getLogger(ToolsKit.class);

    private static final String HASH_ALGORITHM = "SHA-1";
    private static final int HASH_INTERATIONS = 1024;
    private static final int SALT_SIZE = 8;

    private static SerializeConfig jsonConfig = new SerializeConfig();

    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public final static Map<String, String> HTML_CHAR = new HashMap<>();

    public static SerializerFeature[] serializerFeatureArray = {
            SerializerFeature.QuoteFieldNames,
            SerializerFeature.WriteNonStringKeyAsString,
            SerializerFeature.DisableCircularReferenceDetect,
            SerializerFeature.NotWriteRootClassName,
            SerializerFeature.WriteDateUseDateFormat
    };

    private static SerializerFeature[] serializerFeatureArray2 = {
            SerializerFeature.QuoteFieldNames,
            SerializerFeature.UseISO8601DateFormat,
            SerializerFeature.WriteNonStringKeyAsString,
            SerializerFeature.DisableCircularReferenceDetect,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.WriteNullNumberAsZero,
            SerializerFeature.WriteNullBooleanAsFalse,
            SerializerFeature.NotWriteRootClassName
    };

    static {
        HTML_CHAR.put("&", "&#38;");
        HTML_CHAR.put("\"", "&#34;");
        HTML_CHAR.put("<", "&#60;");
        HTML_CHAR.put(">", "&#62;");
        HTML_CHAR.put("'", "&#39;");
        jsonConfig.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    /***
     * 判断传入的对象是否为空
     *
     * @param obj
     *            待检查的对象
     * @return 返回的布尔值,为空或等于0时返回true
     */
    public static boolean isEmpty(Object obj) {
        return checkObjectIsEmpty(obj, true);
    }

    /***
     * 判断传入的对象是否不为空
     *
     * @param obj
     *            待检查的对象
     * @return 返回的布尔值,不为空或不等于0时返回true
     */
    public static boolean isNotEmpty(Object obj) {
        return checkObjectIsEmpty(obj, false);
    }

    @SuppressWarnings("rawtypes")
    private static boolean checkObjectIsEmpty(Object obj, boolean bool) {
        if (null == obj)
            return bool;
        else if (obj == "" || "".equals(obj))
            return bool;
        else if (obj instanceof Integer || obj instanceof Long || obj instanceof Double) {
            try {
                Double.parseDouble(obj + "");
            } catch (Exception e) {
                return bool;
            }
        } else if (obj instanceof String) {
            if (((String) obj).length() <= 0)
                return bool;
            if ("null".equalsIgnoreCase(obj+""))
                return bool;
        } else if (obj instanceof Map) {
            if (((Map) obj).size() == 0)
                return bool;
        } else if (obj instanceof Collection) {
            if (((Collection) obj).size() == 0)
                return bool;
        } else if (obj instanceof Object[]) {
            if (((Object[]) obj).length == 0)
                return bool;
        }
        return !bool;
    }

    /**
     * 判断是否JSON字符串
     * @param jsonString
     * @return
     */
    public static boolean isMapJsonString(String jsonString) {
        return jsonString.startsWith("{") && jsonString.endsWith("}");
    }

    /**
     * 判断是否数据JSON字符串
     * @param jsonString
     * @return
     */
    public static boolean isArrayJsonString(String jsonString) {
        return jsonString.startsWith("[") && jsonString.endsWith("]");
    }

    /**
     * 判断是否是数组
     * @param obj
     * @return
     */
    public static boolean isArray(Object obj) {
        return obj instanceof List || obj instanceof Array || obj.getClass().isArray();
    }


    public static String toJsonString(Object obj) {
        return JSON.toJSONString(obj, jsonConfig, serializerFeatureArray);
    }

    public static String toJsonString(Object obj, SerializeFilter filter) {
        return JSON.toJSONString(obj, jsonConfig, filter, serializerFeatureArray);
    }

    public static String toJsonString2(Object obj) {
        return JSON.toJSONString(obj, jsonConfig, serializerFeatureArray2);
    }

    public static byte[] toJsonBytes(Object obj) {
        return JSON.toJSONBytes(obj, jsonConfig, serializerFeatureArray);
    }

    public static <T> T jsonParseObject(String jsonText, Class<T> clazz) {
        return JSON.parseObject(jsonText, clazz);
    }

    public static <T> T jsonParseObject(String jsonText, Type typeClazz) {
        return JSON.parseObject(jsonText, typeClazz);
    }

    public static <T> T jsonParseObject(String jsonText, TypeReference<T> typeReference) {
        return JSON.parseObject(jsonText, typeReference);
    }

    public static <T> List<T> jsonParseArray(String jsonText, Class<T> clazz) {
        return JSON.parseArray(jsonText, clazz);
    }

    public static <T> T jsonParseObject(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz);
    }

    public static <T> T xmlParseObject(String xmlText, Class<T> clazz) {
        String json = toJsonString(XmlHelper.of(xmlText).toMap());
        return jsonParseObject(json, clazz);
    }

    public static String getCurrentDateString() {
        try {
            return SDF.format(new Date());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 关键字是否存在于map中, 如果存在返回true, 不存在返回false
     *
     * @param key
     * @param map
     * @return
     */
    public static boolean isExist(String key, Map map) {
        if (map.containsKey(key)) {
            return true;
        }
        return false;
    }

    public static DuangId message2DuangrId(String id) {
        boolean isObjectId = ToolsKit.isValidDuangId(id);
        if (isObjectId) {
            return new DuangId(id);
        } else {
            throw new IllegalArgumentException(id + " is not Vaild DuangId");
        }
    }

    /**
     * 验证是否为MongoDB 的ObjectId
     *
     * @param str
     *            待验证字符串
     * @return  如果是则返回true
     */
    public static boolean isValidDuangId(String str) {
        if (ToolsKit.isEmpty(str)) {
            return false;
        }
        int len = str.length();
        if (len != 24) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if ((c < '0') || (c > '9')) {
                if ((c < 'a') || (c > 'f')) {
                    if ((c < 'A') || (c > 'F')) {
                        logger.warn(str + " is not DuangId!!");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 根据format字段，格式化日期
     * @param date          日期
     * @param format        格式化字段
     * @return
     */
    public static String formatDate(Date date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     *  将字符串日期根据format格式化字段转换成日期类型
     * @param stringDate    字符串日期
     * @param format           格式化日期
     * @return
     */
    public static Date parseDate(String stringDate, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getFieldName(Field field) {
        ConvertField convertField = field.getAnnotation(ConvertField.class);
        if(null != convertField) {
            return convertField.name();
        }
        return field.getName();
//        JSONField jsonField = field.getAnnotation(.class);
//        return (ToolsKit.isEmpty(jsonField)) ? field.getName() :
//                (ToolsKit.isEmpty(jsonField.format()) ? jsonField.name() : jsonField.format());
    }

    public static FileFilter fileFilter(final File dir, final String extName){
        return new FileFilter() {
            public boolean accept(File file) {
                if(".class".equalsIgnoreCase(extName)) {
                    return ( file.isFile() && file.getName().endsWith(extName) ) || file.isDirectory();
                } else if(".jar".equalsIgnoreCase(extName)) {
                    return ( file.isFile() && file.getName().endsWith(extName) ) || file.isFile();
                } else {
                    throw new IllegalArgumentException();
                }
            }
        };
    }


    /**
     * HTML字符转换表
     */
    public static final StringBuilder toHTMLChar(String str) {
        if (str == null) {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder(str);
        char tempChar;
        String tempStr;
        for (int i = 0; i < sb.length(); i++) {
            tempChar = sb.charAt(i);
            if (HTML_CHAR.containsKey(Character.toString(tempChar))) {
                tempStr = HTML_CHAR.get(Character.toString(tempChar));
                sb.replace(i, i + 1, tempStr);
                i += tempStr.length() - 1;
            }
        }
        return sb;
    }

    public static final String htmlChar2String(String htmlChar) {
        if (isEmpty(htmlChar)) return "";
        for (Iterator<Map.Entry<String, String>> it = HTML_CHAR.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> entry = it.next();
            htmlChar = htmlChar.replace(entry.getValue(), entry.getKey());
        }
        return htmlChar;
    }

    /**
     *
     * @param exception
     * @param obj
     * @return
     */
    public static ReturnDto<Object> buildReturnDto(IException exception, Object obj) {
        ReturnDto<Object> dto = new ReturnDto<Object>();
        HeadDto head = ToolsKit.getThreadLocalDto();
        if(isEmpty(head)) {
            head = new HeadDto();
        }
        if (ToolsKit.isEmpty(exception)) {
            head.setRet(IException.SUCCESS_CODE);
            head.setMsg(IException.SUCCESS_MESSAGE);
        } else {
            head.setRet(exception.getCode());
            head.setMsg(exception.getMessage());
        }
        dto.setHead(head);
        dto.setData(obj);
        return dto;
    }

    public static boolean isDuangBean(Class<?> parameterType) {
        if(DataType.isBaseType(parameterType)) {
            return false;
        }
        return parameterType.isAnnotationPresent(Bean.class)
                || DataType.isIdEntityType(parameterType)
                || ObjectKit.newInstance(parameterType) instanceof Serializable;
    }

    public static InputStream string2InputStream(String str, String encoding) {
        try {
            return new ByteArrayInputStream(str.getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            logger.warn("ToolsKit str2InputStream fail: " + e.getMessage());
            return null;
        }
    }

    /**
     * Hex编码.
     */
    public static String encodeHex(byte[] input) {
        return new String(Hex.encodeHex(input)); // .encodeHexString(input);
    }

    /**
     * Hex解码.
     */
    public static byte[] decodeHex(String input) {
        try {
            return Hex.decodeHex(input.toCharArray());
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据codeid设定安全密码的Salt
     */
    public static byte[] buildEntryptSalt(int codeid) {
        return Digests.generateSalt(SALT_SIZE);
    }

    /**
     * 随机设定安全密码的Salt
     */
    public static byte[] buildEntryptSalt() {
        return Digests.generateSalt(SALT_SIZE);
    }

    /**
     * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
     */
    public static String buildEntryptPassword(String password, byte[] salt) {
        byte[] hashPassword = Digests.sha1(password.getBytes(), salt, HASH_INTERATIONS);
        return Encodes.encodeHex(hashPassword);
    }

    /**
     * 去掉不符合指定字符串里包含的字符
     * @param password
     * @return
     */
    private static String vaildPassword(String password){
        char[] charArray = password.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<charArray.length; i++){
            for(int j=0; j<Encodes.BASE62.length; j++){
                if(charArray[i] == Encodes.BASE62[j]){
                    sb.append(charArray[i]);
                }
            }
        }
        if(sb.length() > 0){
            password = sb.toString();
        }
        return password;
    }

    /**
     * 首字母变小写
     */
    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    /**
     * 首字母变大写
     */
    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] -= ('a' - 'A');
            return new String(arr);
        }
        return str;
    }


    /**
     * 设定安全的KEY
     * @param key
     * @param key2
     */
    public static String buildEntryptKey(String key, String key2) {
        byte[] hashKey = Digests.sha1(key.getBytes(), key2.getBytes());
        return Encodes.encodeHex(hashKey);
    }

    /**
     * 验证对象属性值是否正确
     * @param validateObj   待验证的对象
     * @return
     */
    public static void validatorObj(Object validateObj) {
        if (ToolsKit.isEmpty(validateObj)) {
            throw new ValidatorException("validator object is fail: object is null");
        }
        try {
            VtorKit.validate(validateObj);
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }

    /**
     * 将搜索对象转换为查询对象
     * @param searchListDto
     * @param searchListDto
     * @return
     */
    public static Query searchDto2Query(SearchListDto searchListDto, Class<?> tClass) throws Exception {
        if(ToolsKit.isEmpty(searchListDto)) {
            throw new ServiceException("searchListDto is null");
        }
        if(ToolsKit.isEmpty(tClass)) {
            throw new ServiceException("tClass is null");
        }
        Map<String,Field> fieldMap = ClassKit.getFieldMap(tClass);
        Query query = new Query();
        int pageNo = searchListDto.getPageNo();
        if(pageNo == 1) {pageNo = 0;}
        query.page(new PageDto(pageNo, searchListDto.getPageSize()));
        List<SearchDto> searchDtoList = searchListDto.getSearchDtos();
        Class<?> typeClass = null;
        if(ToolsKit.isNotEmpty(searchDtoList)) {
            for(SearchDto searchDto : searchDtoList) {
                String fieldName = searchDto.getField();
                Field field = fieldMap.get(fieldName);
                typeClass = (null == field) ? String.class : field.getType();
                String operator = ToolsKit.isEmpty(searchDto.getOperator()) ? "==" : searchDto.getOperator();
                Object value =  TypeConverter.convert(typeClass, searchDto.getValue());
                if (ToolsKit.isNotEmpty(fieldName) && ToolsKit.isNotEmpty(value)) {
                    switch (operator) {
                        case "!=":
                            query.ne(fieldName, value);
                            break;
                        case ">":
                            query.gt(fieldName, value);
                            break;
                        case ">=":
                            query.gte(fieldName, value);
                            break;
                        case "<":
                            query.lt(fieldName, value);
                            break;
                        case "<=":
                            query.lte(fieldName, value);
                            break;
                        case "like":
                            query.like(fieldName, value);
                            break;
                        default:
                            query.eq(fieldName, value);
                            break;
                    }
                }
            }
        }
        return query;
    }


    /***
     * 新增记录时，填充基本数据到对象中
     *
     * @param obj 需要反射的对象
     */
    public static void addEntityData(Object obj) throws Exception {
        Map<String,String> map = getRequestUserIdTerminal();
        addIdEntityData(obj,
                map.get(ConstEnums.REQUEST_ID_FIELD.getValue()),
                map.get(ConstEnums.TERMINAL_FIELD.getValue()));
    }

    /***
     * 新增记录时，添加基本数据到对象中
     *
     * @param obj 需要反射的对象
     * @param userId 创建人ID
     * @param source 数据来源
     */
    private static void addIdEntityData(Object obj, String userId, String source) throws Exception {
        if (isEmpty(obj) || isEmpty(userId)) {
            throw new ServiceException("自动填充IdEntity数据时出错,需要填充对象为空或创建/更新用户ID为空");
        }
        Date currentDate = new Date();
        Field createTimeField = IdEntity.class.getDeclaredField(IdEntity.CREATETIME_FIELD);
        Field updateTimeField = IdEntity.class.getDeclaredField(IdEntity.UPDATETIME_FIELD);
        Field statusField = IdEntity.class.getDeclaredField(IdEntity.STATUS_FIELD);
        Field createUserIdField = IdEntity.class.getDeclaredField(IdEntity.CREATEUSERID_FIELD);
        Field updateUserIdField = IdEntity.class.getDeclaredField(IdEntity.UPDATEUSERID_FIELD);
        Field sourceField = IdEntity.class.getDeclaredField(IdEntity.SOURCE_FIELD);
        Object value;
        value = ObjectKit.getFieldValue(obj, createTimeField);
        if (isEmpty(value)) {
            ObjectKit.setField(obj, createTimeField, currentDate);
        }
        ObjectKit.setField(obj, updateTimeField, currentDate);
        value = ObjectKit.getFieldValue(obj, statusField);
        if (isEmpty(value)) {
            ObjectKit.setField(obj, statusField, IdEntity.STATUS_FIELD_SUCCESS);
        }
        value = ObjectKit.getFieldValue(obj, createUserIdField);
        if (isEmpty(value)) {
            ObjectKit.setField(obj, createUserIdField, userId);
        }
        ObjectKit.setField(obj, updateUserIdField, userId);
        value = ObjectKit.getFieldValue(obj, sourceField);
        if (isEmpty(value)) {
            ObjectKit.setField(obj, sourceField, source);
        }
    }

    /**
     * 修改记录时，修改更新时间，更新人ID到对象中
     *@param obj 要修改的对象
     */
    public static void updateEntityData(Object obj) throws Exception {
        Map<String,String> map = getRequestUserIdTerminal();
        updateIdEntityData(obj,
                map.get(ConstEnums.REQUEST_ID_FIELD.getValue()),
                map.get(ConstEnums.TERMINAL_FIELD.getValue()));
    }

    /**
     * 修改记录时，修改更新时间，更新人ID到对象中
     *
     * @param obj 要修改的对象
     */
    private static void updateIdEntityData(Object obj, String userId, String source) throws Exception {
        Field updateTimeField = IdEntity.class.getDeclaredField(IdEntity.UPDATETIME_FIELD);
        Field updateUserIdField = IdEntity.class.getDeclaredField(IdEntity.UPDATEUSERID_FIELD);
        Field sourceField = IdEntity.class.getDeclaredField(IdEntity.SOURCE_FIELD);
        ObjectKit.setField(obj, updateTimeField, new Date());
        ObjectKit.setField(obj, updateUserIdField, userId);
        ObjectKit.setField(obj, sourceField, source);
    }

    /**
     * 取出请求里的userid及terminal
     * @return Map
     */
    private static Map<String,String> getRequestUserIdTerminal() {
        String userId = "";
        String terminal = "";
        try {
            HeadDto headDto = ToolsKit.getThreadLocalDto();
            if(ToolsKit.isNotEmpty(headDto)) {
                terminal = headDto.getHeaderMap().get(ConstEnums.TERMINAL_FIELD);
                String tokenId = headDto.getTokenId();
                if(ToolsKit.isNotEmpty(tokenId)) {
                    userId = getSecurityUser(tokenId).getUserId();
                }
            }
        } catch (Exception e) {
            throw new MvcException(e.getMessage(), e);
        }
        userId = ToolsKit.isEmpty(userId) ? "admin" : userId;
        terminal = ToolsKit.isEmpty(terminal) ? "console" : terminal;
        Map<String, String> map =  new HashMap<>();
        map.put(ConstEnums.REQUEST_ID_FIELD.getValue(), userId);
        map.put(ConstEnums.TERMINAL_FIELD.getValue(), terminal);
        return map;
    }

    /**
     * 根据key参数，取已经登录的SecurityUser对象
     * @param key   参数值为userId或tokenId
     * @return      SecurityUser对象
     * @Exception SecurityUser对象不存在，则抛出空指针异常
     */
    public static SecurityUser getSecurityUser(String key) {
        try {
            SecurityUser securityUser = SecurityKit.duang().id(key).get();
            if (ToolsKit.isEmpty(securityUser)) {
                throw new NullPointerException("根据[" + key + "]取SecurityUser时失败,SecurityUser对象为null,请确定是否已登录!");
            }
            return securityUser;
        } catch (Exception e) {
            throw new MvcException(e.getMessage(), e);
        }
    }

    // 定义一个请求对象安全线程类
    private static DuangThreadLocal<HeadDto> requestHeaderThreadLocal = new DuangThreadLocal<HeadDto>() {
        @Override
        public HeadDto initialValue() {
            return new HeadDto();
        }
    };

    /**
     * 设置请求头DTO到ThreadLocal变量
     * @param headDto       请求头DTO
     */
    public static void setThreadLocalDto(HeadDto headDto) {
        requestHeaderThreadLocal.set(headDto);
    }

    /**
     *  取ThreadLocal里的HeadDto对象
     * @return
     */
    public static HeadDto getThreadLocalDto() {
        return  requestHeaderThreadLocal.get();
    }

    /**
     * 简单判断是否duang请求
     *  为了防止恶意请求，对于此类不带tokenId的请求
     *  客户端必须增加HTTP基本认证。
     *  默认用户名是 duang  密码是 duangduangduang
     *  如需要自定义，则在duang.properties里，设置base.auth.username及base.auth.password
     *  同时亦须对security.filter.uri设置允许访问的uri ，如有多个uri，以小写的,号分隔
     *  即需要在客户端设置http基本认证， 在服务器的duang.properties文件里对security.filter.uri设置允许访问的uri
     *
     * @param headMap   head头信息
     * @return    正确的请求返回true
     */
    public static boolean isDuangRequest(Map<String,String> headMap) {
        String authorization = headMap.get(HttpHeaderNames.AUTHORIZATION);
        if(ToolsKit.isEmpty(authorization)) {
            throw new SecurityException("current request duangframework is not allowed access!");
        }
        String duang = ConstEnums.FRAMEWORK_OWNER.getValue();
        String account = PropKit.get(ConstEnums.PROPERTIES.BASE_AUTH_USERNAME.getValue(), duang);
        String password = PropKit.get(ConstEnums.PROPERTIES.BASE_AUTH_PASSWORD.getValue(), duang+duang+duang);
        if(!authorization.equals(createBaseAuthHeaderString(account, password))) {
            throw new SecurityException("current request duangframework is not allowed access!");
        }
        return true;
    }

    /**
     * 创建HTTP基本认证头信息
     * @param account       用户名
     * @param password     密码
     * @return
     */
    public static String createBaseAuthHeaderString(String account, String password) {
        String auth = account+":"+password;
        return "Basic " + Encodes.encodeBase64(auth.getBytes()); //Charset.forName("US-ASCII")
    }
}
