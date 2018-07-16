package com.duangframework.doclet.modle;

import com.duangframework.mvc.route.RequestMapping;

import java.util.List;

/**
 * 通过Doclet反射后，取得类对象的相关信息文档模型
 * @author Created by laotang
 * @date createed in 2018/6/27.
 */
public class ClassDocModle implements java.io.Serializable {

    // 类方法名
    private String name;
    // 注解类说明
    private RequestMapping mappingModle;
    // 注释说明
    private String commentText;
    // 注释体
    private List<TagModle> tagModles;
    // 方法体
    List<MethodDocModle> methods;

    public ClassDocModle(String name, RequestMapping mappingModle, String commentText, List<TagModle> tagModles, List<MethodDocModle> methods) {
        this.name = name;
        this.mappingModle = mappingModle;
        this.commentText = commentText;
        this.tagModles = tagModles;
        this.methods = methods;
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

    public List<TagModle> getTagModles() {
        return tagModles;
    }

    public void setTagModles(List<TagModle> tagModles) {
        this.tagModles = tagModles;
    }

    public List<MethodDocModle> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodDocModle> methods) {
        this.methods = methods;
    }

    public RequestMapping getMappingModle() {
        return mappingModle;
    }

    public void setMappingModle(RequestMapping mappingModle) {
        this.mappingModle = mappingModle;
    }

    @Override
    public String toString() {
        return "ClassDocModle{" +
                "name='" + name + '\'' +
                "mappingModle='" + mappingModle + '\'' +
                ", tagModles=" + tagModles +
                ", methods=" + methods +
                '}';
    }
}
