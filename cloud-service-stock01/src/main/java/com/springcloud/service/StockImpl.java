package com.springcloud.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.springcloud.dao.StockDao;
import com.springcloud.pojo.Payment;
import com.springcloud.pojo.Stock;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import io.seata.rm.tcc.api.BusinessActionContext;

@Service
@Slf4j
public class StockImpl implements StockService{

    @Autowired
    private StockDao stockDao;


    @Override
    public int create(Stock stock) {
        return stockDao.create(stock);
    }

    @Override
    public Stock queryByProductId(Long productId) {
        return stockDao.queryByProductId(productId);
    }

    @Override
    public int updateById(Stock stock) {
        return stockDao.updateById(stock);
    }

    @Override
    public int deleteById(Long productId) {
        return stockDao.deleteById(productId);
    }

    @Override
    public int updateStockByOrder(Long productId,int orderNum) {
        return stockDao.updateStockByOrder(productId,orderNum);
    }

    //开启TCC事务
    @Override
    @Transactional
    public boolean decrease(int productId, int quantity) {
        Stock stock = stockDao.queryByProductId((long) productId);
        log.info("tcc一阶段{}",stock.toString());
        log.info("当前XID===>" + RootContext.getXID());
        if (stock.getStockNum() >= quantity) {
            stock.setStockNum(stock.getStockNum() - quantity);
            //设置冻结库存
            stock.setFrozenStorage(quantity);

        } else {
            throw new RuntimeException(stock.getProductId() + "库存不⾜,⽬前剩余库存:"
                    + stock.getStockNum());
        }

        int success = this.updateById(stock);
        log.info("当前库存数:{},减少库数为:{}",stock.getStockNum(),quantity);
        if(success >0){
            log.info("减少库存：tcc一阶段try成功");
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public boolean decreaseCommit(BusinessActionContext context) {
        long productId = Long.parseLong(context.getActionContext("productId").toString());
        Stock stock = stockDao.queryByProductId(productId);
        log.info("tcc二阶段{}",stock.toString());

        if (stock != null) {
            //冻结库存清零
            stock.setFrozenStorage(0);
            this.updateById(stock);
        }
        log.info("--------->xid=" + context.getXid() + " 提交成功!");
        return true;
    }

    @Override
    @Transactional
    public boolean decreaseRollback(BusinessActionContext context) {
        long productId = Long.parseLong(context.getActionContext("productId").toString());
        long quantity = Long.parseLong(context.getActionContext("quantity").toString());
        Stock stock = stockDao.queryByProductId(productId);
        log.info("cancel阶段{}",stock.toString());

        if (stock != null) {
            //加回库存
            stock.setStockNum((int) (stock.getStockNum() + quantity));
            //冻结库存清零
            stock.setFrozenStorage(0);
            this.updateById(stock);
        }
        log.info("--------->xid=" + context.getXid() + " 回滚成功!");
        return true;
    }


}
