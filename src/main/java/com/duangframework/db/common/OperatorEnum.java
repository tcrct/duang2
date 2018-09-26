package com.duangframework.db.common;

/**
 * Created by laotang on 2018/9/26.
 */
public enum  OperatorEnum {

    ID("_id", "id","主键ID字段名称"),
    GT("$gt",">", "message"),
    ;


    private final String mkey;
    private final String skey;
    private final String desc;

    /**
     * Constructor.
     */
    private OperatorEnum(String mkey, String skey, String desc) {
        this.mkey = mkey;
        this.skey = skey;
        this.desc = desc;
    }

    /**
     * Get the value.
     *
     * @return the value
     */
    public String getMkey() {
        return mkey;
    }

    public String getSkey() {
        return skey;
    }

    public String getDesc() {
        return desc;
    }
}
