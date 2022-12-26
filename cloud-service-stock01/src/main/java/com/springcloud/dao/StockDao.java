package com.springcloud.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.springcloud.pojo.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

//mybatis支持的映射方式有基于xml的mapper.xml文件、基于java的使用Mapper接口class。
//从mybatis3.4.0开始加入了@Mapper注解，目的就是为了不再写mapper映射文件。
@Mapper
//@Repository注解便属于最先引入的一批，它用于将数据访问层 (DAO 层 ) 的类标识为 Spring Bean,表明这个类具有对对象CRUD（增删改查）的功能
@Repository
public interface StockDao {

    //创建Stock
    int create(Stock stock);

    //根据ID查询,更新,删除Stock
    Stock queryByProductId(@Param("productId")Long productId);
    int updateById(Stock stock);
    int deleteById(@Param("productId")Long productId);
    int updateStockByOrder(@Param("productId")Long productId,@Param("orderNum")int orderNum);

    Stock queryById(@Param("productId") LambdaQueryWrapper<Stock> productId);
}
