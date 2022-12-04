package com.springcloud.service;

import com.springcloud.pojo.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;


public interface PaymentService {

    int create(Payment payment);
    Payment queryById(long id);
}
