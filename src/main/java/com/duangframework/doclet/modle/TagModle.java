package com.duangframework.doclet.modle;

/**
 * 注释部份文档模型
 * @author Created by laotang
 * @date createed in 2018/6/27.
 */
public class TagModle {

    private String name;
    private String text;

    public TagModle(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TagModle{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
