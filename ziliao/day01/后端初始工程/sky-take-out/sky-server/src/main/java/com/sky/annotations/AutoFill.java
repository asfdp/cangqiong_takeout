package com.sky.annotations;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 创建自定义注解用于标注特定功能字段的自动填充
 */

@Target(ElementType.METHOD)//表示该注解作用与方法层面
@Retention(RetentionPolicy.RUNTIME)//表示该注解作用与运行时
public @interface AutoFill {
    //数据库操作类型，UPDATE,INSERT
    OperationType value();

}
