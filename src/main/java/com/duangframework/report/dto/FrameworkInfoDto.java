package com.duangframework.report.dto;

import com.duangframework.report.ComputerInfo;

import java.util.List;

/**
 * 框架信息DTO对象
 *
 * @author  laotang
 */
public class FrameworkInfoDto implements java.io.Serializable {

    private ComputerInfo computerInfo;
    private String host;
    private Integer prot;
    private boolean ssl;
    private Integer controllerCount;
    private Integer actionCount;
    private String author;
    private List<MappingDto> mappingDtoList;

    public FrameworkInfoDto() {
    }

    public FrameworkInfoDto(ComputerInfo computerInfo, String host, Integer prot, boolean ssl, Integer controllerCount, Integer actionCount, String author, List<MappingDto> mappingDtoList) {
        this.computerInfo = computerInfo;
        this.host = host;
        this.prot = prot;
        this.ssl = ssl;
        this.controllerCount = controllerCount;
        this.actionCount = actionCount;
        this.author = author;
        this.mappingDtoList = mappingDtoList;
    }

    public ComputerInfo getComputerInfo() {
        return computerInfo;
    }

    public void setComputerInfo(ComputerInfo computerInfo) {
        this.computerInfo = computerInfo;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getProt() {
        return prot;
    }

    public void setProt(Integer prot) {
        this.prot = prot;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public Integer getControllerCount() {
        return controllerCount;
    }

    public void setControllerCount(Integer controllerCount) {
        this.controllerCount = controllerCount;
    }

    public Integer getActionCount() {
        return actionCount;
    }

    public void setActionCount(Integer actionCount) {
        this.actionCount = actionCount;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<MappingDto> getMappingDtoList() {
        return mappingDtoList;
    }

    public void setMappingDtoList(List<MappingDto> mappingDtoList) {
        this.mappingDtoList = mappingDtoList;
    }
}
