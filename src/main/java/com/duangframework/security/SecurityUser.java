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


    private String userId;      //用户ID
    private String account;  // 用户帐号
    private String username;  // 用户真实姓名
    private String password;  // 密码
    private String salt;    // 盐值
    private String codeId;  // 标识码
    private String tokenId; // 登录成功后的token
    private String terminal;  //终端标识
    private Set<String> companys = new HashSet<String>(); // 公司
    private Set<String> departments = new HashSet<String>(); // 部门
    private Set<String> userGroups = new HashSet<String>(); // 用户组
    private Set<String> roleGroups = new HashSet<String>();   //角色组
    private Set<String> roles = new HashSet<>();    //角色
    private Map<String, String> authoritys = new HashMap<>();   //权限, key为权限ID，value为URI

    public SecurityUser() {
    }

    public SecurityUser(String userId, String account, String username, String password, String salt, String codeId, String tokenId,
                        String terminal, Set<String> companys, Set<String> departments, Set<String> userGroups,
                        Set<String> roleGroups, Set<String> roles, Map<String, String> authoritys) {
        this.userId = userId;
        this.account = account;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.codeId = codeId;
        this.tokenId = tokenId;
        this.terminal = terminal;
        this.companys = companys;
        this.departments = departments;
        this.userGroups = userGroups;
        this.roleGroups = roleGroups;
        this.roles = roles;
        this.authoritys = authoritys;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
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

    public Set<String> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Set<String> userGroups) {
        this.userGroups = userGroups;
    }

    public Set<String> getRoleGroups() {
        return roleGroups;
    }

    public void setRoleGroups(Set<String> roleGroups) {
        this.roleGroups = roleGroups;
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

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    @Override
    public String toString() {
        return "SecurityUser{" +
                "userId='" + userId + '\'' +
                ", account='" + account + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", codeId='" + codeId + '\'' +
                ", tokenId='" + tokenId + '\'' +
                ", terminal='" + terminal + '\'' +
                ", companys=" + companys +
                ", departments=" + departments +
                ", userGroups=" + userGroups +
                ", roleGroups=" + roleGroups +
                ", roles=" + roles +
                ", authoritys=" + authoritys +
                '}';
    }
}
