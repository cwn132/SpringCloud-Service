package com.springcloud.proxy;


import com.springcloud.service.PaymentService;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *  JDK动态代理 - 基于接口paymentService实现
 */
public class PaymentJDKProxy implements InvocationHandler {


    private Object proxyInterface;

    public PaymentJDKProxy(Object proxyInterface) {
        super();
        this.proxyInterface = proxyInterface;
    }
    //获取动态代理
    public Object getProxy() {
        //生成动态代理 - 反射原理
        Object proxyFactory =Proxy.newProxyInstance(this.getClass().getClassLoader(),
                proxyInterface.getClass().getInterfaces(),
                //实现代理的类对象
                this);
        return proxyFactory;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO Auto-generated method stub
        System.out.println("JDK动态代理 - 执行开始");
        Object invoke = method.invoke(proxyInterface, args);
        System.out.println("JDK动态代理 - 执行结束");
        return invoke;
    }


}
