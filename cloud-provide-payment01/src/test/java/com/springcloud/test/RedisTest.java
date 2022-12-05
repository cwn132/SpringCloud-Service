package com.springcloud.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctc.wstx.util.StringUtil;
import com.springcloud.pojo.CommonResult;
import com.springcloud.pojo.Payment;
import com.springcloud.service.PaymentService;
import com.springcloud.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.math.BigInteger;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisTest {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PaymentService paymentService;

    @Test
    public void testString(){
        String result = "";

//        result = redisUtil.get("2");
//        System.out.println(result);

//        Long id = 2l;
//        System.out.println(id.toString());
//        String order = redisUtil.get(id.toString());
//        System.out.println(order);

        Long paymentId =12l;

        Payment payment = paymentService.queryById(paymentId);
//        double v = payment.getPaymentNum().doubleValue() * payment.getPaymentNum().doubleValue();
//        BigDecimal totalPrice = payment.getPaymentPrice() * new BigDecimal(payment.getPaymentNum());
//        payment.setPaymentTotalPrice(totalPrice);


//        // 将对象转为json字符串方法
//        String jsonString = JSONObject.toJSONString(payment);
//        System.out.println("jsonString = " + jsonString);



    }

}
