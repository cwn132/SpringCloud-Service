package com.springcloud.service;

import com.alibaba.fastjson.JSONObject;
import com.springcloud.pojo.CommonResult;
import com.springcloud.pojo.Payment;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @Test
    public void test(){

        List<Object> list = paymentService.queryAllPayment();
        List<Payment> paymentList = (List<Payment>) list.get(0); //数据集合
        Integer total = ((List<Integer>) list.get(1)).get(0);//总量

//        //将对象转成Json
        String paymentJson = JSONObject.toJSONString(paymentList);
        CommonResult commonResult = new CommonResult(200,"查询成功",paymentJson,total);
        log.info("commonResult:{}",commonResult);

    }

}
