package com.springcloud.service;

import com.springcloud.dao.PaymentDao;
import com.springcloud.pojo.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Paymentlmpl implements PaymentService{
    @Autowired
    PaymentDao paymentDao;

    //@Override注解告诉你下面这个方法是从父类/接口继承过来的，需要你重写一次
    @Override
    public int create(Payment payment){

        return paymentDao.create(payment);
    }


    @Override
    public Payment queryById(long id){
        return paymentDao.queryById(id);
    }

}
