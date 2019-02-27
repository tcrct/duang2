package com.duangframework.report.dto;

import com.duangframework.mvc.route.Route;

import java.util.List;

public class MappingDto implements java.io.Serializable {

    private String controllerMappingKey;            // Controller的Mapping对象value值
    private Route controllerMapping;                  // Controller的Mapping对象
    private List<Route> actionsMappingList;           // 该Controller下的所有public的Action Mapping注解对象

    public MappingDto() {
    }

    public MappingDto(String controllerMappingKey, Route controllerMapping, List<Route> actionsMappingList) {
        this.controllerMappingKey = controllerMappingKey;
        this.controllerMapping = controllerMapping;
        this.actionsMappingList = actionsMappingList;
    }

    public String getControllerMappingKey() {
        return controllerMappingKey;
    }

    public void setControllerMappingKey(String controllerMappingKey) {
        this.controllerMappingKey = controllerMappingKey;
    }

    public Route getControllerMapping() {
        return controllerMapping;
    }

    public void setControllerMapping(Route controllerMapping) {
        this.controllerMapping = controllerMapping;
    }

    public List<Route> getActionsMappingList() {
        return actionsMappingList;
    }

    public void setActionsMappingList(List<Route> actionsMappingList) {
        this.actionsMappingList = actionsMappingList;
    }
}
