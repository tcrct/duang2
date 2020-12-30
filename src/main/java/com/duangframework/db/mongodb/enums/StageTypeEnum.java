package com.duangframework.db.mongodb.enums;

/**
 * MongoDB数据类型定义
 *
 * @author laotang
 */
public enum StageTypeEnum {

	COLLSCAN("全表扫描"),
	IXSCAN("索引扫描"),
	FETCH("根据索引去检索指定document"),
	SHARD_MERGE("将各个分片返回数据进行merge"),
	SORT("在内存中进行了排序"),
	LIMIT("使用limit限制返回数"),
	SKIP("使用skip进行跳过"),
	IDHACK("针对ObjectId进行查询"),
	SHARDING_FILTER("通过mongos对分片数据进行查询"),
	COUNT("利用db.coll.explain().count()之类进行count运算"),
	COUNTSCAN("没有使用Index进行的count查询"),
	COUNT_SCAN("使用了Index进行count的查询"),
	SUBPLA("没有使用索引的$or查询"),
	TEXT("使用全文索引进行查询"),
	PROJECTION("限定返回字段");


	private final String readme;

	/**
	 * Constructor.
	 */
	private StageTypeEnum(String readme) {
		this.readme = readme;
	}

	/**
	 * Get the value.
	 *
	 * @return the value
	 */

	public String getReadme() {
		return readme;
	}
}
