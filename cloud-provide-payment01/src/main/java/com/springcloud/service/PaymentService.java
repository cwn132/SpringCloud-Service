package com.springcloud.service;

import com.springcloud.pojo.Payment;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

import java.util.List;

@LocalTCC
public interface PaymentService {

    int create(Payment payment);

    //根据ID查询,更新,删除payment
    Payment queryById(@Param("paymentId")Long paymentId);
    int updateById(Payment payment);
    int deleteById(@Param("paymentId") Long paymentId);

    //根据订单编号查询,更新,删除payment
    Payment queryByPaymentSerial(@Param("paymentSerial")String paymentSerial);
    int deleteByPaymentSerial(@Param("paymentSerial") String paymentSerial);
    int updateByPaymentSerial(Payment payment);

    List<Object> queryAllPayment();

    /**
     * @TwoPhaseBusinessAction 描述⼆阶段提交
     * name: 为 tcc⽅法的 bean 名称，需要全局唯⼀，⼀般写⽅法名即可
     * commitMethod: Commit⽅法的⽅法名
     * rollbackMethod:Rollback⽅法的⽅法名
     * @BusinessActionContextParamete 该注解⽤来修饰 Try⽅法的⼊参，
     * 被修饰的⼊参可以在 Commit ⽅法和 Rollback ⽅法中通过
    BusinessActionContext 获取。
    **/
    @TwoPhaseBusinessAction(name = "PaymentAddTCC", commitMethod = "addCommit", rollbackMethod = "addRollBack")
    int add(@BusinessActionContextParameter(paramName = "payment") Payment payment);
    public boolean addCommit(BusinessActionContext context);
    public boolean addRollBack(BusinessActionContext context);


}
