package com.duangframework.db.mongodb.enums;

/**
 * MongoDB数据类型定义
 *
 * @author laotang
 */
public enum MongodbDataTypeEnum {
	DOUBLE(1,"Double"),	 
	STRING(2, "String"), 
	OBJECT(3,"Object"),	 
	ARRAY(4, "Array"),	 
	BINARY_DATA(5,"BinaryData"),	 
	UNDEFINED(6, "Undefined"),
	OBJECTID(7, "ObjectId"),	 
	BOOLEAN(8, "Boolean"),
	DATE(9, "Ymd"),
	NULL(10, "Null"),	 
	REGULAR_EXPRESSION(11, "Regex"),	 
	DBPOINTER(12, "dbPointer"),
	JAVASCRIPT(13, "JavaScript"),	 
	SYMBOL(14, "symbol"),	 
	JAVASCRIPT_WITH_SCOPE(15, "javascriptWithScope"),	 
	INTEGER(16, "Int"),	 
	TIMESTAMP(17,"Timestamp"),	 
	LONG(18, "Long"),
	MIN_KEY(-1, "minKey"),	 
	MAX_KEY(127, "maxKey");

	private final int number;
	private final String alias;

	/**
	 * Constructor.
	 */
	private MongodbDataTypeEnum(int number, String alias) {
		this.number = number;
		this.alias = alias;
	}

	/**
	 * Get the value.
	 *
	 * @return the value
	 */
	public int getNumber() {
		return number;
	}

	public String getAlias() {
		return alias;
	}
}
