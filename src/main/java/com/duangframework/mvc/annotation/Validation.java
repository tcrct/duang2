package com.duangframework.mvc.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validation {
	
	 boolean isEmpty() default true;		// 是否允许值为null或空字串符 默认为允许
	 
	 int length() default 0;	 			// 长度，限制字符串长度
	 
	 double[] range() default 0;			// 取值范围，如[0,100] 则限制该值在0-100之间
	 
	 String desc() default "";				// 设置字段名, 用于发生异常抛出时，中文说明该变量名称
	 
	 String fieldValue() default "";				// 默认值
	 
	 String formatDate() default "yyyy-MM-dd HH:mm:ss";		// 格式化日期(24小时制)
	 
	 boolean oid() default false;					// 是否是mongodb objectId，主要用于验证id


	// 提交参数的名称
	String fieldName() default "";
	// 参数类型
	Class<?> fieldType() default String.class;

	// 对自定义的javabean进行参数说明与验证，该class对象要存在@Vtor注解
	Class<?> bean() default Object.class;
}



/**
ValidatorHandler.assertFalse=assertion failed

ValidatorHandler.assertTrue=assertion failed

ValidatorHandler.future=must be a future date

ValidatorHandler.length=length must be between {min} and {max}

ValidatorHandler.max=must be less than or equal to {value}

ValidatorHandler.min=must be greater than or equal to {value}

ValidatorHandler.notNull=may not be null

ValidatorHandler.past=must be a past date

ValidatorHandler.pattern=must match "{regex}"

ValidatorHandler.range=must be between {min} and {max}

ValidatorHandler.size=size must be between {min} and {max}
*/
