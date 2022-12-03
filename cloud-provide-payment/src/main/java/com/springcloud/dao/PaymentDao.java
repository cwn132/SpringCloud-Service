package com.springcloud.dao;

import com.springcloud.pojo.Payment;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.Mapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
@Repository
public interface PaymentDao {
   int create(Payment payment);
   Payment queryById(@Param("id")long id);

}
