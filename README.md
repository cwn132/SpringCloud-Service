SpringCloud - NingSpace
==============================

# 一. 基础搭建
- JDK11
- MySQL 8.0.23
- Mybatis
- Maven

# Note:
- vim /ect/hosts
127.0.0.1 eureka7001.com
127.0.0.1 eureka7002.com

# 二. 目录结构描述
1. cloud-api-commons - SpringCloud API公共接口

2. cloud-provide-payment01 & 02- SpringCloud 服务者01 & 02 集群
* 创建消费端订单接口方法-服务01： create(Payment). 调用: http://localhost:8001/payment/create
* 根据ID获取支付订单方法-服务01： queryById(Id). 调用: http://localhost:8001/payment/get/{id}
* 创建消费端订单接口方法-服务02： create(Payment). 调用: http://localhost:8002/payment/create
* 根据ID获取支付订单方法-服务02： queryById(Id). 调用: http://localhost:8002/payment/get/{id}
* 加载服务发现组件discovery - 调用：http://localhost:8001/payment/discovery

3. cloud-consumer-order - SpringCloud 消费者 
* 创建消费端订单接口方法： create(Payment). 调用: http://localhost:8003/consumer/payment/create
* 根据ID获取支付订单方法： getPaymentById(Id). 调用: http://localhost:8003/consumer/payment/get/{id}
* 定义负载均衡Ribbon - RandomRule为随机访问服务

4. cloud-eureka-server01 & 02 - SpringCloud Eureka注册中心 集群
* 访问地址eureka01：http://localhost:7001/
* 访问地址eureka02：http://localhost:7002/
![Image text](https://github.com/cwn132/SpringCloud-Service/blob/master/eureka.jpg)

5. cloud-consumer-feign-order - SpringCloud 消费者服务调用Feign
* 创建消费端订单接口方法： create(Payment). 调用: http://localhost:8004/consumer/payment/create
* 根据ID获取支付订单方法： getPaymentById(Id). 调用: http://localhost:8004/consumer/payment/get/{id}
* 集成超时时间和日志打印 - PaymentFeignTimeOut & FeignConfig
* 集成Hystrix熔断器 - PaymentHystrixFallbackService

6. cloud-consumer-hystrix-dashboard - SpringCloud Hystrix监控器
* 控制台 - http://localhost:9001/hystrix
* 加入cloud-provide-payment01监控 - http://localhost:8001/hystrix.stream
* 加入cloud-consumer-feign-order监控 - http://localhost:8004/hystrix.stream
![Image text](https://github.com/cwn132/SpringCloud-Service/blob/master/hystrix.jpg)

7. cloud-gateway9522 - SpringCloud Gateway网关
* 路由服务者MCROSERVICE-PAYMENT - http://localhost:9522/payment/get/1?token=123
* 查服务者端口 - http://localhost:9522/payment/lb?token=123
* 添加Token过滤器加token访问 - http://localhost:9522/payment/lb?token=123
![Image text](https://github.com/cwn132/SpringCloud-Service/blob/master/gateway.jpg)








