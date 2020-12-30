package com.duangframework.mvc.dto;

import com.duangframework.db.annotation.VoColl;
import com.duangframework.mvc.annotation.Bean;
import com.duangframework.mvc.annotation.Param;
//import com.duangframework.vtor.annotation.FieldName;

import java.util.List;

/**
 * 搜索条件集合Dto
 * @author laotang
 */
@Bean
public class SearchListDto extends ApiDto {

    /**
     * 页数,由0开始
     */
    @Param(label = "页数", desc = "页数,由0开始", defaultValue = "0")
    private int pageNo = 0;
    /**
     * 页行数，默认每页10行
     */
    @Param(label = "页行数", desc = "页行数，默认每页10行", defaultValue = "10")
    private int pageSize = 10;
    /**
     * 搜索字段集合
      */
    @Param(label = "搜索对象集合", desc = "将搜索条件封装成SearchDto", defaultValue = "SearchDto")
    @Bean
    @VoColl
    private List<SearchDto> searchDtos;
    /**
     * or 搜索字段集合
     * 不放在searchDtos中的目的：
     *      与原本的分开，但又不影响原有的功能，构造成的查询: searchDto and orSearchDto
      */
    @Param(label = "搜索对象集合", desc = "将搜索条件封装成SearchDto", defaultValue = "SearchDto")
    @Bean
    @VoColl
    private List<SearchDto> orSearchDtos;
    /**
     * 多条件查询时，and 或 or 链接 SearchDto对象值, 如果值为空，默认为and查询
      */
    @Param(label = "查询模式", desc = "多条件查询时，and 或 or 链接 SearchDto对象值, 如果值为空，默认为and查询", defaultValue = "and")
    private String operator = "and";

    public SearchListDto() {
    }

    public SearchListDto(String tokenId, int pageNo, int pageSize, List<SearchDto> searchDtos, String operator) {
        super(tokenId, "");
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.searchDtos = searchDtos;
        this.operator = operator;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<SearchDto> getSearchDtos() {
        return searchDtos;
    }

    public void setSearchDtos(List<SearchDto> searchDtos) {
        this.searchDtos = searchDtos;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<SearchDto> getOrSearchDtos() {
        return orSearchDtos;
    }

    public void setOrSearchDtos(List<SearchDto> orSearchDtos) {
        this.orSearchDtos = orSearchDtos;
    }
}
