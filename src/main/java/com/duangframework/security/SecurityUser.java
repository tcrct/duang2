package com.duangframework.security;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Param;
import com.duangframework.security.dto.RelationDto;
import com.duangframework.vtor.annotation.NotEmpty;

/**
 * 登录成功后的用户对象信息
 * Created by laotang on 2018/11/26.
 */
public class SecurityUser implements java.io.Serializable {

    @NotEmpty
    @Param(label = "用户编号")
    private String userId;
    @Param(label = "标识码")
    private String codeId;
    @Param(label = "用户帐号")
    private String account;
    @Param(label = "用户真实姓名")
    private String username;
    @Param(label = "登录成功后的token")
    private String tokenId;
    @Param(label = "终端标识")
    private String terminal;
    @Param(label = "关联关系Dto对象")
    private RelationDto relationDto = new RelationDto();
    @Param(label = "公司ID")
    private String companyId;
    @Param(label = "项目ID")
    private String projectId;
    @Param(label = "部门ID")
    private String departmentId;
    @Param(label = "更新时间")
    private Long updateTime;

    public SecurityUser() {
    }

    public SecurityUser(String userId, String codeId, String account, String username, String tokenId, String terminal,
                        RelationDto relationDto,String companyId, String projectId, String departmentId) {
        this.userId = userId;
        this.codeId = codeId;
        this.account = account;
        this.username = username;
        this.tokenId = tokenId;
        this.terminal = terminal;
        this.relationDto = relationDto;
        this.companyId = companyId;
        this.projectId = projectId;
        this.departmentId = departmentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
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

    public RelationDto getRelationDto() {
        return relationDto;
    }

    public void setRelationDto(RelationDto relationDto) {
        this.relationDto = relationDto;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }


    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }



    @Override
    public String toString() {
        return "SecurityUser{" +
                "userId='" + userId + '\'' +
                ", codeId='" + codeId + '\'' +
                ", account='" + account + '\'' +
                ", username='" + username + '\'' +
                ", tokenId='" + tokenId + '\'' +
                ", terminal='" + terminal + '\'' +
                ", relationDto=" + ToolsKit.toJsonString(relationDto) +
                ", companyId='" + companyId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
