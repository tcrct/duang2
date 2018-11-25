package com.duangframework.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 登录成功后的用户对象信息
 * Created by laotang on 2018/11/26.
 */
public class SecurityUser implements java.io.Serializable {


    private String userid;      //用户ID
    private String account;  // 用户帐号
    private String username;  // 用户真实姓名
    private String password;  // 密码
    private String salt;    // 盐值
    private String codeid;  // 标识码
    private Set<String> companys = new HashSet<String>(); // 公司
    private Set<String> departments = new HashSet<String>(); // 部门
    private Set<String> usergroups = new HashSet<String>(); // 用户组
    private Set<String> rolegroups = new HashSet<String>();   //角色组
    private Set<String> roles = new HashSet<>();    //角色
    private Map<String, String> authoritys = new HashMap<>();   //权限

    public SecurityUser() {
    }

    public SecurityUser(String userid, String account, String username, String password, String salt, String codeid, Set<String> companys, Set<String> departments, Set<String> usergroups, Set<String> rolegroups, Set<String> roles, Map<String, String> authoritys) {
        this.userid = userid;
        this.account = account;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.codeid = codeid;
        this.companys = companys;
        this.departments = departments;
        this.usergroups = usergroups;
        this.rolegroups = rolegroups;
        this.roles = roles;
        this.authoritys = authoritys;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCodeid() {
        return codeid;
    }

    public void setCodeid(String codeid) {
        this.codeid = codeid;
    }

    public Set<String> getCompanys() {
        return companys;
    }

    public void setCompanys(Set<String> companys) {
        this.companys = companys;
    }

    public Set<String> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<String> departments) {
        this.departments = departments;
    }

    public Set<String> getUsergroups() {
        return usergroups;
    }

    public void setUsergroups(Set<String> usergroups) {
        this.usergroups = usergroups;
    }

    public Set<String> getRolegroups() {
        return rolegroups;
    }

    public void setRolegroups(Set<String> rolegroups) {
        this.rolegroups = rolegroups;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Map<String, String> getAuthoritys() {
        return authoritys;
    }

    public void setAuthoritys(Map<String, String> authoritys) {
        this.authoritys = authoritys;
    }
}
