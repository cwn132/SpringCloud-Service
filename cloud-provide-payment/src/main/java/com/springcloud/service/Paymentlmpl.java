package com.springcloud.service;

import com.springcloud.dao.PaymentDao;
import com.springcloud.pojo.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
public class Paymentlmpl implements PaymentService{

    @Autowired
    PaymentDao paymentDao;

    @Override
    public int create(Payment payment){

        return paymentDao.create(payment);
    }


    @Override
    public Payment queryById(long id){
        return paymentDao.queryById(id);
    }
}
