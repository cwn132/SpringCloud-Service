package com.springcloud.service;

import com.springcloud.pojo.Payment;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;


public interface PaymentService {

    int create(Payment payment);

    //根据ID查询,更新,删除payment
    Payment queryById(@Param("paymentId")Long paymentId);
    int updateById(Payment payment);
    int deleteById(@Param("paymentId") Long paymentId);

    //根据订单编号查询,更新,删除payment
    Payment queryByPaymentSerial(@Param("paymentSerial")String paymentSerial);
    int deleteByPaymentSerial(@Param("paymentSerial") String paymentSerial);
    int updateByPaymentSerial(Payment payment);
}
