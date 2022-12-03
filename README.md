SpringCloud - Demo
==============================



目录结构描述
* cloud-api-commons - SpringCloud API公共接口

* cloud-provide-payment - SpringCloud 服务者
1. 创建消费端订单接口方法： create(Payment). 调用: http://localhost:8001/payment/create
2. 根据ID获取支付订单方法： queryById(Id). 调用: http://localhost:8001/payment/get/{id}

* cloud-consumer-order - SpringCloud 消费者 
1. 创建消费端订单接口方法： create(Payment). 调用: http://localhost:8002/consumer/payment/create
2. 根据ID获取支付订单方法： getPaymentById(Id). 调用: http://localhost:8002/consumer/payment/get/{id}

* cloud-eureka-server - SpringCloud Eureka注册中心
1. 访问地址：http://localhost:7001/
