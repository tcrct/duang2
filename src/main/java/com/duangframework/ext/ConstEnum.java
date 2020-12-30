package com.duangframework.ext;

public enum ConstEnum {
    ;
    private ConstEnum() {
    }

    public static enum GETUI {
        URl("http://sdk.open.api.igexin.com/apiex.htm", "指定域名"),
        ACCESS_KEY("o9EawK5nDE6P4FMJhvKH01", "AppKey"),
        ACCESS_KEY_SECRET("80yPwSNOAm5VnjyyA6pwN", "安全码"),
        APP_ID("xK3lW5yGoZ7GpEuIwnri07", "AppID"),
        MASTER_SECRET("8lSuaZGVgC9XwxRRCxzMW1", "个推服务端API鉴权码");

        private final String value;
        private final String desc;

        private GETUI(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public String getValue() {
            return this.value;
        }

        public String getDesc() {
            return this.desc;
        }
    }

    public static enum XINGE {
        ACCESS_KEY_ID("", ""),
        ACCESS_KEY_SECRET("", "");

        private final String value;
        private final String desc;

        private XINGE(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public String getValue() {
            return this.value;
        }

        public String getDesc() {
            return this.desc;
        }
    }

    public static enum QINIU {
        ACCESS_KEY_ID("", ""),
        ACCESS_KEY_SECRET("", "");

        private final String value;
        private final String desc;

        private QINIU(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public String getValue() {
            return this.value;
        }

        public String getDesc() {
            return this.desc;
        }
    }

    public static enum ALIYUN {
        ACCESS_KEY_ID("LTAID4F8gZO7HA4P", "APP KEY"),
        ACCESS_KEY_SECRET("7fe6lJZwfC7bsgS6Y6eCBobWAGY50n", "APP SECRET"),
        DNS_REGION_ID("cn-hangzhou", "dns必填固定值，必须为cn-hanghou"),
        OSS_ENDPOINT("oss-cn-hangzhou.aliyuncs.com", "OSS的链接地址"),
        SMS_PHONE_NUMBER_FIELD("PhoneNumbers", "手机号码字段名"),
        SMS_SIGN_NAME_FIELD("SignName", "签名名称字段名"),
        SMS_CODE_FIELD("TemplateCode", "短信模板字段名"),
        SMS_PARAM_FIELD("TemplateParam", "短信参数字段名"),
        SMS_PARAM_JSON_FIELD("TemplateParamJson", "短信参数数据字段名，批量发送时用"),
        SMS_PHONE_NUMBER_JSON_FIELD("PhoneNumberJson", "手机号码数组字组名，批量发送时用"),
        SMS_SIGN_NAME_JSON_FIELD("SignNameJson", "签名名称数组字段名，批量发送时用"),
        SMS_SENDSMS_FIELD("SendSms", "发送一条短信"),
        SMS_SEND_BATCH_SMS_FIELD("SendBatchSms", "批量发送短信"),
        SMS_SIGN_NAME("思格特智能印章系统", "签名名称"),
        SMS_DOMAIN("dysmsapi.aliyuncs.com", "短信域名"),
        SMS_VERSION("2017-05-25", "短信版本号"),
        PUSH_KEY("27760428", "推送key"),
        PUSH_SECRET("2f8ffe5a909352d6a1f4c42d492d119f", "推送安全码");

        private final String value;
        private final String desc;

        private ALIYUN(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public String getValue() {
            return this.value;
        }

        public String getDesc() {
            return this.desc;
        }
    }
}
