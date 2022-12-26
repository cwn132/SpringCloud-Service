package com.springcloud.service;

import com.springcloud.dao.PaymentDao;
import com.springcloud.pojo.Payment;

import io.seata.spring.annotation.GlobalTransactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service //加上该注解会将当前类自动注入到spring容器中，不需要再在applicationContext.xml文件定义bean了。
public class Paymentlmpl implements PaymentService{

    @Autowired
    PaymentDao paymentDao;


    @Override
    public int create(Payment payment){
        paymentDao.create(payment);
        return payment.getPaymentId().intValue();
    }

    @Override
    public Payment queryById(@Param("paymentId")Long paymentId){
        return paymentDao.queryById(paymentId);
    }

    @Override
    public int updateById(Payment payment) {
        return paymentDao.updateById(payment);
    }

    @Override
    public int deleteById(@Param("paymentId") Long paymentId) {
        return paymentDao.deleteById(paymentId);
    }

    @Override
    public Payment queryByPaymentSerial(@Param("paymentSerial") String paymentSerial){
        return paymentDao.queryByPaymentSerial(paymentSerial);
    }

    @Override
    public int updateByPaymentSerial(Payment payment) {
        return paymentDao.updateByPaymentSerial(payment);
    }

    @Override
    public int deleteByPaymentSerial(@Param("paymentId") String paymentSerial) {
        return paymentDao.deleteByPaymentSerial(paymentSerial);
    }



}
