package com.springcloud.service;

import com.springcloud.dao.PaymentDao;
import com.springcloud.pojo.Payment;

import com.springcloud.pojo.Stock;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;

import java.util.List;


@Service //加上该注解会将当前类自动注入到spring容器中，不需要再在applicationContext.xml文件定义bean了。
@Slf4j
public class Paymentlmpl implements PaymentService{

    @Autowired
    PaymentDao paymentDao;


    @Override
    public int create(Payment payment){
        paymentDao.create(payment);
        return payment.getPaymentId().intValue();
    }

    @Override
    public List<Object> queryAllPayment(){
        return paymentDao.queryAllPayment();
    }

    @Override
    public Payment queryById(@Param("paymentId")Long paymentId){
        return paymentDao.queryById(paymentId);
    }

    @Override
    public int updateById(Payment payment) {
        return paymentDao.updateById(payment);
    }

    @Override
    public int deleteById(@Param("paymentId") Long paymentId) {
        return paymentDao.deleteById(paymentId);
    }

    @Override
    public Payment queryByPaymentSerial(@Param("paymentSerial") String paymentSerial){
        return paymentDao.queryByPaymentSerial(paymentSerial);
    }

    @Override
    public int updateByPaymentSerial(Payment payment) {
        return paymentDao.updateByPaymentSerial(payment);
    }

    @Override
    public int deleteByPaymentSerial(@Param("paymentId") String paymentSerial) {
        return paymentDao.deleteByPaymentSerial(paymentSerial);
    }

    //开启TCC事务
    @Override
    @Transactional
    public int add(Payment payment){
        log.info("tcc一阶段{}" + payment.toString());
        log.info("当前XID===>" + RootContext.getXID());

        payment.setPaymentStatus(0); //try阶段-预检查
        int id = paymentDao.create(payment);  //创建订单
        log.info("Payment:" + payment.toString());

        //放开以下注解抛出异常
//        throw new RuntimeException("服务tcc测试回滚");

        return id;
    }

    @Override
    @Transactional
    public boolean addCommit(BusinessActionContext context) {
        Payment payment = JSON.parseObject(context.getActionContext("payment").toString(), Payment.class);
//        payment = this.queryById(payment.getPaymentId());
        log.info("tcc二阶段{}",payment.toString());

        if (payment != null) {
            log.info("commit阶段-提交事务");
            payment.setPaymentStatus(1);//commit阶段-提交事务
            log.info("getPaymentStatus:{}",payment.getPaymentStatus());
            this.updateByPaymentSerial(payment);//修改订单
        }
        log.info("--------->xid=" + context.getXid() + " 提交成功!");
        return true;
    }

    @Override
    @Transactional
    public boolean addRollBack(BusinessActionContext context) {
        Payment payment = JSON.parseObject(context.getActionContext("payment").toString(), Payment.class);
        log.info("cancel阶段{}",payment.toString());
        if (payment != null) {
            this.deleteByPaymentSerial(payment.getPaymentSerial());//删除订单
        }
        log.info("--------->xid=" + context.getXid() + " 回滚成功!");
        return true;
    }



}
