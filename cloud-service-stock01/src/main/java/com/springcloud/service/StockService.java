package com.springcloud.service;

import com.springcloud.pojo.Stock;
import org.apache.ibatis.annotations.Param;

public interface StockService {
    //创建Stock
    int create(Stock stock);

    //根据ID查询,更新,删除Stock
    Stock queryByProductId(@Param("productId")Long productId);
    int updateById(Stock stock);
    int deleteById(@Param("productId")Long productId);
    int updateStockByOrder(@Param("productId")Long productId,@Param("orderNum")int orderNum);

}
