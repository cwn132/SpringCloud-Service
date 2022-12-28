package com.springcloud.dao;

import com.springcloud.pojo.Payment;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.Mapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


//mybatis支持的映射方式有基于xml的mapper.xml文件、基于java的使用Mapper接口class。
//从mybatis3.4.0开始加入了@Mapper注解，目的就是为了不再写mapper映射文件。
@Mapper
//@Repository注解便属于最先引入的一批，它用于将数据访问层 (DAO 层 ) 的类标识为 Spring Bean,表明这个类具有对对象CRUD（增删改查）的功能
@Repository
public interface PaymentDao {

   //创建payment
   int create(Payment payment);

   //根据ID查询,更新,删除payment
   Payment queryById(@Param("paymentId")Long paymentId);
   int updateById(Payment payment);
   int deleteById(@Param("paymentId")Long paymentId);

   //根据订单编号查询,更新,删除payment
   Payment queryByPaymentSerial(@Param("paymentSerial")String paymentSerial);
   int deleteByPaymentSerial(@Param("paymentSerial") String paymentSerial);
   int updateByPaymentSerial(Payment payment);

   //查询所有订单
   List<Object> queryAllPayment();

}
