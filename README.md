SpringCloud - NingSpace
==============================

#3 基础搭建
- JDK11
- MySQL 8.0.23
- Mybatis

#4 Note:
- vim /ect/hosts
127.0.0.1 eureka7001.com
127.0.0.1 eureka7002.com

#3 目录结构描述
1. cloud-api-commons - SpringCloud API公共接口

2. cloud-provide-payment01 - SpringCloud 服务者01 集群
* 创建消费端订单接口方法： create(Payment). 调用: http://localhost:8001/payment/create
* 根据ID获取支付订单方法： queryById(Id). 调用: http://localhost:8001/payment/get/{id}

3. cloud-provide-payment02 - SpringCloud 服务者02 集群
* 创建消费端订单接口方法： create(Payment). 调用: http://localhost:8002/payment/create
* 根据ID获取支付订单方法： queryById(Id). 调用: http://localhost:8002/payment/get/{id}

4. cloud-consumer-order - SpringCloud 消费者 
* 创建消费端订单接口方法： create(Payment). 调用: http://localhost:8003/consumer/payment/create
* 根据ID获取支付订单方法： getPaymentById(Id). 调用: http://localhost:8003/consumer/payment/get/{id}

5. cloud-eureka-server01 & 02 - SpringCloud Eureka注册中心 集群
* 访问地址eureka01：http://localhost:7001/
* 访问地址eureka02：http://localhost:7002/







