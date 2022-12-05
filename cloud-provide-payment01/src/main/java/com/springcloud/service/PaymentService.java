package com.springcloud.service;

import com.springcloud.pojo.Payment;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;


public interface PaymentService {

    int create(Payment payment);
    Payment queryById(@Param("paymentId")Long paymentId);
    int updateById(Payment payment);
    int deleteById(@Param("paymentId") Long paymentId);
}
