package com.duangframework.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.utils.DataType;
import redis.clients.util.SafeEncoder;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * 缓存序列化工具
 */
public class SerializableUtils {

	private static final FastJsonConfig fastJsonConfig = new FastJsonConfig();

	private SerializableUtils() {

	}

	public static <T> String serializeString(T obj) {
		return ToolsKit.toJsonString(obj);
	}

	public static byte[][] serializerValueArray(List list) {
		byte[][] result = new byte[list.size()][];
		for (int i=0; i<list.size(); i++) {
			result[i] = serialize(list.get(i));
		}
		return result;
	}

	public static <T> byte[] serialize(T obj) {
		if (obj instanceof String) {
			return String.valueOf(obj).getBytes();
		}
		return SafeEncoder.encode(ToolsKit.toJsonString(obj));
	}

	public static <T> T deserialize(byte[] data, Class<T> clazz) {
		try {
			return ToolsKit.jsonParseObject(data, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T> T deserialize(byte[] data, TypeReference<T> type) {
		try {
			return ToolsKit.jsonParseObject(new String(data, ConstEnums.PROPERTIES.DEFAULT_ENCODING.getValue()), type);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> List<T> deserializeArray(byte[] data, Class<T> clazz) {
		try {
			return ToolsKit.jsonParseArray(new String(data, ConstEnums.PROPERTIES.DEFAULT_ENCODING.getValue()), clazz);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
