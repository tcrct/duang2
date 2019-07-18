package com.duangframework.doclet.modle;

import com.duangframework.mvc.route.RequestMapping;

import java.util.List;

/**
 * 类方法文档模型
 * @author Created by laotang
 * @date createed in 2018/6/27.
 */
public class MethodDocModle implements java.io.Serializable {

    // 方法名
    private String name;
    // 注释说明
    private String commentText;
    //异常信息
    private List<String> exception;
    //返回值类型
    private String returnType;
    // 注释体  /**  */ 里的内容
    private List<TagModle> tagModles;
    //参数体
    private List<ParameterModle> paramModles;
    // 注解
    private RequestMapping mappingModle;
    //参数体
    private List<ParameterModle> returnParamModles;
    public MethodDocModle() {

    }

    public MethodDocModle(String name, RequestMapping mappingModle, String commentText, List<String> exception, String returnType,
                          List<TagModle> tagModles, List<ParameterModle> paramModles, List<ParameterModle> returnParamModles) {
        this.name = name;
        this.mappingModle = mappingModle;
        this.commentText = commentText;
        this.exception = exception;
        this.returnType = returnType;
        this.tagModles = tagModles;
        this.paramModles = paramModles;
        this.returnParamModles= returnParamModles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public List<String> getException() {
        return exception;
    }

    public void setException(List<String> exception) {
        this.exception = exception;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<TagModle> getTagModles() {
        return tagModles;
    }

    public void setTagModles(List<TagModle> tagModles) {
        this.tagModles = tagModles;
    }

    public List<ParameterModle> getParamModles() {
        return paramModles;
    }

    public void setParamModles(List<ParameterModle> paramModles) {
        this.paramModles = paramModles;
    }

    public RequestMapping getMappingModle() {
        return mappingModle;
    }

    public void setMappingModle(RequestMapping mappingModle) {
        this.mappingModle = mappingModle;
    }

    public List<ParameterModle> getReturnParamModles() {
        return returnParamModles;
    }

    public void setReturnParamModles(List<ParameterModle> returnParamModles) {
        this.returnParamModles = returnParamModles;
    }

    @Override
    public String toString() {
        return "MethodDocModle{" +
                "name='" + name + '\'' +
                "mappingModle='" + mappingModle + '\'' +
                ", exception=" + exception +
                ", returnType='" + returnType + '\'' +
                ", tagModles=" + tagModles +
                ", paramModles=" + paramModles +
                ", returnParamModles=" + returnParamModles +
                '}';
    }
}
