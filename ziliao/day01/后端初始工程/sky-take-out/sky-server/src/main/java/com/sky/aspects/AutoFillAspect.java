package com.sky.aspects;

import com.sky.annotations.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类，用于公共字段自动填充处理逻辑
 * 切面类本质就是 “切入点+通知”
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点注解，其中execution可以先确定要扫描的区域，然后再该区域中进行指定注解的扫描，
     * 这样可以提高效率
     *
     * 表达式中指定的是com.sky.mapper这个包下的任意类的任意方法，
     * 方法参数和返回值都是任意
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotations.AutoFill)")
    public void autoFillPointCut() {}

    @Before("autoFillPointCut()")//使用前置通知，通知的切入点为上面方法指定的切入点
    public void autoFill(JoinPoint joinPoint)  {
        log.info("开始执行公共字段自动填充...");
        //获取到当前被拦截方法上的数据库操作类型UPDATE or INSERT
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//获取到注解对象中的数据库操作类型

        //获取到当前被拦截的方法的参数--实体对象
        Object[] joinPointArgs = joinPoint.getArgs();//该方法拿到的是参数的集合，在设计方法时约定让实体放在参数的第一位
        if (joinPointArgs == null || joinPointArgs.length == 0) {
            return;//如果参数集合为空，则不执行切面
        }
        Object entity = joinPointArgs[0];//集合中的第一个元素就是方法实体对象
        //准备赋值的数据:当前时间以及操作用户id
        LocalDateTime now = LocalDateTime.now();
        Long currentId= BaseContext.getCurrentId();

        //根据上面获取的数据库操作类型确定要自动填充的数据
        if (operationType == OperationType.INSERT) {
            //如果为插入操作，则需要4个公共字段进行赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象赋值
                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (operationType==OperationType.UPDATE){
            //如果为更新操作，则只需要对2个公共字段赋值
            try {
                //Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                //Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象赋值
                //setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                //setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
