package com.duangframework.db.enums;

/**
 * Created by laotang on 2018/12/3.
 */
public enum LevelEnums {
    DIR(0, "目录"),
    MENU(1, "菜单"),
    BUTTON(2, "按钮");

    private Integer value;
    private String desc;

    private LevelEnums(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
