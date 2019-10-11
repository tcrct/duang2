package com.duangframework.mvc.dto;

import com.duangframework.db.annotation.VoColl;
import com.duangframework.mvc.annotation.Bean;
import com.duangframework.mvc.annotation.Param;
import com.duangframework.utils.GenericsUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author laotang
 */
@Bean
public class PageDto<T> implements java.io.Serializable {

	public static final String RESULT_FIELD = "result";
	
	@Param(label = "页数", defaultValue = "1", desc = "当前页数")
	protected int pageNo = 1;
	@Param(label = "页行数", defaultValue = "1", desc = "当前页行数")
	protected int pageSize = 1;
	@Param(label = "跳过行数", defaultValue = "-1", desc = "跳过行数")
	protected int skipNum = -1;
	@Param(label = "统计总行数", defaultValue = "true", desc = "否是开启自动统计总行数,true为开启")
	protected boolean autoCount = true;
	@Param(label = "分页结果集", desc = "查询后返回的分页结果集")
	protected List<T> result = Collections.emptyList();
	@Param(label = "总行数", desc = "查询后返回结果集的总行数")
	protected long totalCount = -1;
	@Param(label = "搜索关键字说明", desc = "可搜索的属性字段说明")
	private List<String> searchField;

//	private Class<T> genricTypeClass;
	
	public PageDto() {
//		genricTypeClass = GenericsUtils.getSuperClassGenricType(getClass());
	}



	
	public PageDto(int pageNo, int pageSize) {
		setPageNo(pageNo);
		setPageSize(pageSize);
	}

	public PageDto(int skipNum) {
		setSkipNum(skipNum);
	}
	
	//-- 访问查询参数函数 --//
	/**
	 * 获得当前页的页号,序号从1开始,默认为1.
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
	 */
	public void setPageNo(final int pageNo) {
		this.pageNo = pageNo;

		if (pageNo < 1) {
			this.pageNo = 1;
		}
	}
	public PageDto<T> pageNo(final int thePageNo) {
		setPageNo(thePageNo);
		return this;
	}

	/**
	 * 获得每页的记录数量,默认为1.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页的记录数量, 低于1且不等于-100时自动设置为1， -100代表查询全部
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;

		if (pageSize < 1 && pageSize != -100) {
			this.pageSize = 1;
		}
	}

	public PageDto<T> pageSize(final int thePageSize) {
		setPageSize(thePageSize);
		return this;
	}
	
	/**
	 * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从1开始.
	 */
	public int getFirst() {
		return ((pageNo - 1) * pageSize) + 1;
	}
	
	/**
	 * 查询对象时是否自动另外执行count查询获取总记录数, 默认为false.
	 */
	public boolean isAutoCount() {
		return autoCount;
	}

	/**
	 * 查询对象时是否自动另外执行count查询获取总记录数.
	 */
	public void setAutoCount(final boolean autoCount) {
		this.autoCount = autoCount;
	}
	
	public PageDto<T> autoCount(final boolean theAutoCount) {
		setAutoCount(theAutoCount);
		return this;
	}
	
	//-- 访问查询结果函数 --//

	/**
	 * 取得页内的记录列表.
	 */
	public List<T> getResult() {
		return result;
	}

	/**
	 * 设置页内的记录列表.
	 */
	public void setResult(final List<T> result) {
		this.result = result;
	}

	/**
	 * 取得总记录数, 默认值为-1.
	 */
	public long getTotalCount() {
		return totalCount;
	}

	/**
	 * 设置总记录数.
	 */
	public void setTotalCount(final long totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 根据pageSize与totalCount计算总页数, 默认值为-1.
	 */
	public long getTotalPages() {
		if (totalCount < 0) {
			return -1;
		}

		long count = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			count++;
		}
		if(pageSize == -1) count = 1;
		return count;
	}

	/**
	 * 是否还有下一页.
	 */
	public boolean isHasNext() {
		return (pageNo + 1 <= getTotalPages());
	}

	/**
	 * 取得下页的页号, 序号从1开始.
	 * 当前页为尾页时仍返回尾页序号.
	 */
	public int getNextPage() {
		if (isHasNext()) {
			return pageNo + 1;
		} else {
			return pageNo;
		}
	}

	/**
	 * 是否还有上一页.
	 */
	public boolean isHasPre() {
		return (pageNo - 1 >= 1);
	}

	/**
	 * 取得上页的页号, 序号从1开始.
	 * 当前页为首页时返回首页序号.
	 */
	public int getPrePage() {
		if (isHasPre()) {
			return pageNo - 1;
		} else {
			return pageNo;
		}
	}


	public int getSkipNum() {
		return skipNum;
	}

	/**
	 * 跳过行数，如果设置该值，则pageNo与pageSize将失效
	 * @return
	 */
	public void setSkipNum(int skipNum) {
		this.skipNum = skipNum;
	}

	/**
	 * 可支持搜索字段的中文说明
	 * @return
	 */
	public List<String> getSearchField() {
		return searchField;
	}

	public void setSearchField(List<String> searchField) {
		this.searchField = searchField;
	}
}
