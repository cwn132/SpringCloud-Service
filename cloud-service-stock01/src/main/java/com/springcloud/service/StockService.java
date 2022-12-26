package com.springcloud.service;

import com.springcloud.pojo.Stock;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.apache.ibatis.annotations.Param;

@LocalTCC
public interface StockService {
    //创建Stock
    int create(Stock stock);

    //根据ID查询,更新,删除Stock
    Stock queryByProductId(@Param("productId")Long productId);
    int updateById(Stock stock);
    int deleteById(@Param("productId")Long productId);
    int updateStockByOrder(@Param("productId")Long productId,@Param("orderNum")int orderNum);

    //开启TCC事务
    @TwoPhaseBusinessAction(name = "StockDecreaseTcc", commitMethod = "decreaseCommit", rollbackMethod = "decreaseRollback")
    public boolean decrease(@BusinessActionContextParameter(paramName = "productId")int productId,
                         @BusinessActionContextParameter(paramName ="quantity")int quantity);
    public boolean decreaseCommit(BusinessActionContext context);
    public boolean decreaseRollback(BusinessActionContext context);

}
