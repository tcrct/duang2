package com.duangframework.mvc.http;

/**
 * 请求行为参数描述接口
 * 定义的字段名称必须与方法的入参名称一致。
 * @author zvae
 * @project sgt-seal-dev-sz
 * @date 2020/12/11 15:23
 */
public interface RequestParamDescription {
    /**
     * 获取请求的uri
     * @return
     */
    String getUri();

    /**
     * 日志类型
     * @return 0：登录；1：查询；2：新增；3：修改；4：删除；5：登出 6：行为动作
     */
    String getLogType();

    /**
     * 是否系统行为长生
     * @return  0: 用户行为，1:系统行为
     */
    default String isSystemOperator() {
        return "0";
    }
}
