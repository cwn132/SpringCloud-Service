package com.springcloud.service;


import com.springcloud.pojo.CommonResult;
import com.springcloud.pojo.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentHystrixFallbackService implements PaymentService{

    @Override
    public int create(Payment payment){
        return 500;
    }

    /**
     * 熔断的回调方法，也就是降级方法
     * @HystrixCommand方法级的熔断处理需要当前方法和回调方法的返回类型和参数要一致
     * @return
     */
    @Override
    public CommonResult queryById(Long id) {
        //访问远程服务失败，这些处理逻辑就可以写在该方法中
        return new CommonResult(500,"服务器异常",id);
    }

    @Override
    public CommonResult updateById(Payment payment) {
        return new CommonResult(500,"服务器异常",null);
    }

    @Override
    public CommonResult deleteById(Integer id) {
        return new CommonResult(500,"服务器异常",null);
    }

    @Override
    public String PaymentFeignTimeOutDefault() {

        return "服务器异常 - Time Out";
    }


}
