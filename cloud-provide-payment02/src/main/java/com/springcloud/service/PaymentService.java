package com.springcloud.service;

import com.springcloud.pojo.Payment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;


public interface PaymentService {

    int create(Payment payment);
    Payment queryById(long id);
    int updateById(Payment payment);
    int deleteById(@Param("id") Integer id);
}
