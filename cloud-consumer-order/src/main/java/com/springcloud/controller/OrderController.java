package com.springcloud.controller;

import com.springcloud.pojo.CommonResult;
import com.springcloud.pojo.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
//@RestController注解，相当于@Controller+@ResponseBody两个注解的结合，
//返回json数据不需要在方法前面加@ResponseBody注解了，但使用@RestController这个注解，
//就不能返回jsp,html页面，视图解析器无法解析jsp,html页面
@Slf4j  //Lombok 提供了一个注解，叫做 @log4j。这玩意使用了 Log4J 的库
public class OrderController {
    //调用支付订单服务端的ip+端口号
    public static final  String PAYMENT_URL = "http://localhost:8001";

    //自动装配，修饰属性，可以自动装配spring容器中相同属性的pojo对象
    //作用是把该类注入到spring容器中，让spring容器来管理该类，别的属性自动装配容器中相同属性时，
    //spring容器会帮忙new一个该类型对象来装配给autowired修饰的属性。
    //使用new创建的对象，该对象中@Autowired注入的成员对象为null
    //所以如果一个类中有spring容器注入的对象，则不能使用new来创建对象，必须使用必须@Autowired、@Resource来创建该对象。
    @Autowired
    private RestTemplate restTemplate;
//    RestTemplate restTemp = new RestTemplate();


    //创建支付订单的接口
    @GetMapping("/consumer/payment/create")
    public CommonResult<Payment> create(Payment payment){
        return restTemplate.postForObject(PAYMENT_URL+"/payment/create",payment, CommonResult.class);
    }

    //根据id获取支付订单
    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id){
        return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id,CommonResult.class);
    }

}
