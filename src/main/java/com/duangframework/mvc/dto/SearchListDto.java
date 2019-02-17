package com.duangframework.mvc.dto;

import com.duangframework.mvc.annotation.Bean;

import java.util.List;

@Bean
public class SearchListDto extends ApiDto {

    private int pageNo = 0;		//页数,由0开始
    private int pageSize = 10;		//页行数
    // 搜索字段集合
    @Bean
    private List<SearchDto> searchDtos;
    // 多条件查询时，and 或 or 连接查询VtorFactory
    private String operator = "and";  // and 或 or 链接 SearchDto对象值, 如果值为空，默认为and查询

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
}
