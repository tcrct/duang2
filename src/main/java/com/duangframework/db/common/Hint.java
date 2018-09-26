package com.duangframework.db.common;

import java.util.LinkedHashMap;

/**
 *  Hint对象, 强制Mongodb使用指定的索引进行查询
 * @author laotang
 *
 */
public class Hint {

	private String key;
	private int type;

	public Hint() {
	}
	
	/**
	 * 添加查询索引，必须与创建时的值一致
	 * @param key		索引名称
	 * @param type		排序方向
	 * @return
	 */
	public Hint set(String key, int type) {
		this.key = key;
		this.type = type;
		return this;
	}

	public LinkedHashMap getObject() {
		return new LinkedHashMap(1){{
			this.put(key, type);
		}};
	}
}
