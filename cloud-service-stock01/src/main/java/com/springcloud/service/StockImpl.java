package com.springcloud.service;

import com.springcloud.dao.StockDao;
import com.springcloud.pojo.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
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

}
