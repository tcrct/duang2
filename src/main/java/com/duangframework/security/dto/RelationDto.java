package com.duangframework.security.dto;

import com.duangframework.mvc.annotation.Bean;
import com.duangframework.vtor.annotation.NotEmpty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 用户关联关系Dto对象，应与tokenId进行关联
 * Created by laotang on 2019/3/3.
 */
@Bean
public class RelationDto implements java.io.Serializable{

    // 以下的map，key为id，value为name
    private Map<String, String> companyMap = new HashMap<>(); // 公司集合
    private Map<String, String> projectMap = new HashMap<>(); // 项目集合
    private Map<String, String> roleMap = new HashMap<>();   //角色集合
    private Map<String, String> deptMap = new HashMap<>(); // 部门集合
    private Map<String, String> postMap = new HashMap<>(); // 岗位集合
    // authorityMap key为id，value为uri
    private Map<String, String> authorityMap = new HashMap<>();   //权限集合

    public RelationDto() {
    }

    public RelationDto(Map<String, String> companyMap, Map<String, String> projectMap, Map<String, String> roleMap, Map<String, String> deptMap, Map<String, String> postMap, Map<String, String> authorityMap) {
        this.companyMap = companyMap;
        this.projectMap = projectMap;
        this.roleMap = roleMap;
        this.deptMap = deptMap;
        this.postMap = postMap;
        this.authorityMap = authorityMap;
    }

    public Map<String, String> getCompanyMap() {
        return companyMap;
    }

    public void setCompanyMap(Map<String, String> companyMap) {
        this.companyMap = companyMap;
    }

    public Map<String, String> getProjectMap() {
        return projectMap;
    }

    public void setProjectMap(Map<String, String> projectMap) {
        this.projectMap = projectMap;
    }

    public Map<String, String> getRoleMap() {
        return roleMap;
    }

    public void setRoleMap(Map<String, String> roleMap) {
        this.roleMap = roleMap;
    }

    public Map<String, String> getDeptMap() {
        return deptMap;
    }

    public void setDeptMap(Map<String, String> deptMap) {
        this.deptMap = deptMap;
    }

    public Map<String, String> getPostMap() {
        return postMap;
    }

    public void setPostMap(Map<String, String> postMap) {
        this.postMap = postMap;
    }

    public Map<String, String> getAuthorityMap() {
        return authorityMap;
    }

    public void setAuthorityMap(Map<String, String> authorityMap) {
        this.authorityMap = authorityMap;
    }
}
