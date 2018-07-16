package com.duangframework.doclet.api.dto;

import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/7/9.
 */
public class MethodListDto {

    private String controllerDesc;
    private String controllerReadme;

    private List<MethodDto> methodDtoList;

    public MethodListDto() {
    }

    public MethodListDto(String controllerDesc, String controllerReadme, List<MethodDto> methodDtoList) {
        this.controllerDesc = controllerDesc;
        this.controllerReadme = controllerReadme;
        this.methodDtoList = methodDtoList;
    }

    public String getControllerDesc() {
        return controllerDesc;
    }

    public void setControllerDesc(String controllerDesc) {
        this.controllerDesc = controllerDesc;
    }

    public String getControllerReadme() {
        return controllerReadme;
    }

    public void setControllerReadme(String controllerReadme) {
        this.controllerReadme = controllerReadme;
    }

    public List<MethodDto> getMethodDtoList() {
        return methodDtoList;
    }

    public void setMethodDtoList(List<MethodDto> methodDtoList) {
        this.methodDtoList = methodDtoList;
    }
}
