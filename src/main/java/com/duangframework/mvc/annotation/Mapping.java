package com.duangframework.mvc.annotation;

/**
 * Created by laotang on 2017/11/5.
 */

import com.duangframework.db.enums.LevelEnums;
import com.duangframework.mvc.http.enums.HttpMethod;

import java.lang.annotation.*;

/**
 * 定义 Mapping 类注解
 *
 * ElementType:
 * 1.CONSTRUCTOR:用于描述构造器
 　2.FIELD:用于描述域，字段
 　3.LOCAL_VARIABLE:用于描述局部变量
 　4.METHOD:用于描述方法
 　5.PACKAGE:用于描述包
 　6.PARAMETER:用于描述参数
 　7.TYPE:用于描述类、接口(包括注解类型) 或enum声明

 * Inherited  继承注解
 * Retention：
 * 1.SOURCE:在源文件中有效（即源文件保留）
    2.CLASS:在class文件中有效（即class保留）
    3.RUNTIME:在运行时有效（即运行时保留）

 关于注解支持的元素数据类型:
 所有基本类型（int,float,boolean,byte,double,char,long,short）
 String
 Class
 enum
 Annotation
 上述类型的一维数组
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Mapping {
    String value() default "";
    String desc() default "";
    LevelEnums level () default LevelEnums.BUTTON;
    int order() default 0;
    long timeout() default 3000L;
    HttpMethod[] method() default {};
    boolean lowerCase() default true; //是否全小写，默认是全小写
    Validation[] vtor() default {};
}
