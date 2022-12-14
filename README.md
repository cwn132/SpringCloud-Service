SpringCloud - NingSpace
==============================
* 说明：此项目为临时搭建实现基本增删改查功能，只作参考
* Github地址：https://github.com/cwn132/SpringCloud-Service

# 一. 基础搭建
- JDK11
- MySQL 8.0.23
- Mybatis
- Maven
- Redis(master: 192.168.0.102:6379; slave: 192.168.0.102:6378)
- Zookeeper(127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183) 

# Note:
- vim /ect/hosts
127.0.0.1 eureka7001.com
127.0.0.1 eureka7002.com

==============================
* 技术点：
1. 集成Mybatis操作Mysql
2. AOP动态代理（AspectJ & JDK代理）
3. 实现Redis缓存查询 
4. 实现Zookeeper分布式锁（停用）
5. 实现Seata分布式事务
* Seata AT模式-创建订单和库存服务调用: http://localhost:8001/payment/create/{productId}
* Seata TCC模式-创建订单和库存服务调用: http://localhost:8001/payment/add/{productId}
6. 联动Vue前后端分离 - https://github.com/cwn132/demo-vue


# 二. 目录结构描述
1. cloud-api-commons - SpringCloud API公共接口

2. cloud-provide-payment01 & 02- SpringCloud 订单服务者01(port:8001)& 02(port:8002) 集群
* 说明：服务端引用Redis缓存与数据库增删改查同步
* 根据ID获取支付订单方法： queryById(Id). 调用: http://localhost:8001/payment/get/{id}
![Image text](https://gitee.com/cwn132/SpringCloud-Service/raw/master/query.jpg)
* 创建订单接口方法： create(Payment). 调用: http://localhost:8001/payment/create
![Image text](https://gitee.com/cwn132/SpringCloud-Service/raw/master/create.jpg)
* 根据ID更新订单方法: updateById(Id). 调用：http://localhost:8001/payment/update
![Image text](https://gitee.com/cwn132/SpringCloud-Service/raw/master/update.jpg)
* 删除订单接口方法： deleteById(Id). 调用：http://localhost:8001/payment/delete/{id}
* 加载服务发现组件discovery - 调用：http://localhost:8001/payment/discovery


3. cloud-consumer-order - SpringCloud 订单消费者 
* 创建消费端订单接口方法： create(Payment). 调用: http://localhost:8003/consumer/payment/create
* 根据ID获取支付订单方法： getPaymentById(Id). 调用: http://localhost:8003/consumer/payment/get/{id}
* 定义负载均衡Ribbon - RandomRule为随机访问服务

4. cloud-eureka-server01 & 02 - SpringCloud Eureka注册中心 集群
* 访问地址eureka01：http://localhost:7001/
* 访问地址eureka02：http://localhost:7002/
![Image text](https://gitee.com/cwn132/SpringCloud-Service/raw/master/eureka.jpg)

5. cloud-consumer-feign-order - SpringCloud 订单消费者服务调用Feign
* 创建消费端订单接口方法： create(Payment). 调用: http://localhost:8004/consumer/payment/create
* 根据ID获取支付订单方法： getPaymentById(Id). 调用: http://localhost:8004/consumer/payment/get/{id}
* 删除订单接口方法： deleteById(Id). 调用：http://localhost:8004/payment/delete/{id}
* 根据ID更新订单方法: updateById(Id). 调用：http://localhost:8004/payment/update
* 集成超时时间和日志打印 - PaymentFeignTimeOut & FeignConfig
* 集成Hystrix熔断器 - PaymentHystrixFallbackService

6. cloud-consumer-hystrix-dashboard - SpringCloud Hystrix监控器
* 控制台 - http://localhost:9001/hystrix
* 加入cloud-provide-payment01监控 - http://localhost:8001/hystrix.stream
* 加入cloud-consumer-feign-order监控 - http://localhost:8004/hystrix.stream
![Image text](https://gitee.com/cwn132/SpringCloud-Service/raw/master/hystrix.jpg)

7. cloud-gateway9522 - SpringCloud Gateway网关
* 路由服务者MCROSERVICE-PAYMENT - http://localhost:9522/payment/get/1?token=123
* 查服务者端口 - http://localhost:9522/payment/lb?token=123
* 添加Token过滤器加token访问 - http://localhost:9522/payment/lb?token=123
![Image text](https://gitee.com/cwn132/SpringCloud-Service/raw/master/gateway.jpg)

8. cloud-service-stock01 - SpringCloud 库存服务
* 根据id查询库存：http://localhost:8005/stock/getStockNum/{productId}
* 根据订单减库存(创建订单时GlobalTransactional-seata分布式事务)：http://localhost:8005/stock/updateStockByOrder/{productId}/{orderNum}
![Image text](https://gitee.com/cwn132/SpringCloud-Service/raw/master/seata(AT).jpg)

9. Vue前端管理系统
![Image text](https://gitee.com/cwn132/SpringCloud-Service/raw/master/vue-payment.jpg)







