package com.duangframework.report.dto;



/**
 * @author Created by laotang
 * @date createed in 2018/5/29.
 */
public class ActionInfoDto {

    private String controllerKey;   // controller的映射URI
    private String actionKey;// api的映射URI
    private String desc;// 该api功能简要说明
    private int level;// api在树型结构下的等级
    private int order;// api在同等级下的排序
    private String controllerName;// api的controller的类名称
    private String methodName;// api的请求 method的名称(get, post, put, delete)
    private String restfulKey;  //restful风格映射URI
    private long timeout;  //请求过期时间

    public ActionInfoDto() {
    }

    public ActionInfoDto(String controllerKey, String actionKey, String desc, int level, int order, String controllerName, String methodName,  String restfulKey, long timeout) {
        this.controllerKey = controllerKey;
        this.actionKey = actionKey;
        this.desc = desc;
        this.level = level;
        this.order = order;
        this.controllerName = controllerName;
        this.methodName = methodName;
        this.restfulKey = restfulKey;
        this.timeout = timeout;
    }

    public String getControllerKey() {
        return controllerKey;
    }

    public void setControllerKey(String controllerKey) {
        this.controllerKey = controllerKey;
    }

    public String getActionKey() {
        return actionKey;
    }

    public void setActionKey(String actionKey) {
        this.actionKey = actionKey;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getRestfulKey() {
        return restfulKey;
    }

    public void setRestfulKey(String restfulKey) {
        this.restfulKey = restfulKey;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "ActionInfoDto{" +
                "controllerKey='" + controllerKey + '\'' +
                ", actionKey='" + actionKey + '\'' +
                ", desc='" + desc + '\'' +
                ", level=" + level +
                ", order=" + order +
                ", controllerName='" + controllerName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", restfulKey='" + restfulKey + '\'' +
                ", timeout=" + timeout +
                '}';
    }
}
