package com.springcloud.proxy;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect//标注这个类是切面
@Component
@Slf4j
public class PaymentAspectJProxy {

    //定义切入点规则
    // 使用Pointcut注解，execution(切入点的位置)
    @Pointcut("execution(* com.springcloud.controller.PaymentController.*(..))")
    public void methodPoint() {
    }

    //定义织入规则：Before 方法执行前 ；After 方法执行后 ； Around 方法执行前后；参数为切入点的方法名
    //方法定义规则：
    @Before("methodPoint()")
    public void joinBeforePoint() {
        log.info("========AspectJ执行方法Before=========");
    }

    //定义织入规则：Before 方法执行前 ；After 方法执行后 ； Around 方法执行前后；参数为切入点的方法名
    //方法定义规则：
    @After("methodPoint()")
    public void joinAfterPoint() {
        log.info("========AspectJ执行方法After=========");
    }


}
