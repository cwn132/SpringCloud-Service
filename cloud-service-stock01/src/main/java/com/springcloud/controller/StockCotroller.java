package com.springcloud.controller;

import com.springcloud.generator.StockCodeGenerator;
import com.springcloud.pojo.CommonResult;
import com.springcloud.pojo.Stock;
import com.springcloud.service.StockService;
import com.springcloud.util.RedisUtil;
import com.springcloud.zookeeper.lock.ZkLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class StockCotroller {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StockService stockService;

//    @Autowired
//    private ZkLock zkLock;

    @PostMapping("/stock/create")
    public CommonResult create(@RequestBody Stock stock){

        //Zookeeper锁 - 创建临时节点
//        try {
//            try {
//                zkLock.lock();
//
//                //新建仓库编号
//                String stockSerial =stockCodeGenerator.generatorStockCode();
//                log.info("stockSerial:"+stockSerial);
//                stock.setStockSerial(stockSerial);
//            }catch (Exception e){
//                log.error(e.getMessage());
//            }finally {
//                zkLock.unlock();
//            }

        if (stock.getProductId() == null){
            return new CommonResult(444,"商品ID为空",null,0);
        }


        String stockSerial =StockCodeGenerator.generatorStockCode();
        log.info("stockSerial:"+stockSerial);

        stock.setStockSerial(stockSerial);


        int i = stockService.create(stock);

        if(i>0){
            redisUtil.set(stock.getProductId().toString(),String.valueOf(stock.getStockNum()));
            log.info("********插入库存和缓存成功*********"+stock.getProductId());
            return new CommonResult(200,"插入库存成功",stock.getProductId(),0);
        }else{
            log.info("********插入库存失败*********"+stock.getProductId());
            return new CommonResult(444,"插入库存失败",stock.getProductId(),0);
        }
    }

//    @GetMapping("/stock/getStockNum/{productId}")
//    public CommonResult queryByProductId(@PathVariable("productId")  Long productId) {
//
//        if (productId == null) {
//            return new CommonResult(444, "sproductId为空", productId);
//        }
//
//        String stockNum = redisUtil.get(productId.toString());
//        if (stockNum != null) {
//            log.info("********查询缓存库存成功*********"+stockNum);
//            return new CommonResult(200, "查询成功", stockNum);
//        }
//
//        Stock stock = stockService.queryByProductId(productId);
//        if(stock != null){
//            redisUtil.set(stock.getStockSerial(),stock.getStockNum().toString());
//            log.info("********查询库存成功*********"+stock.getStockNum());
//            return new CommonResult(200, "查询成功", stock.getStockNum());
//        }
//
//        log.info("********此库存不存在*********"+productId);
//        return new  CommonResult(444, "此库存不存在", productId);
//
//    }

    @GetMapping("/stock/getStockNum/{productId}")
    public int queryByProductId(@PathVariable("productId")  Long productId) {

        if (productId == null) {
           return 0;
        }
        log.info("********开始查询库存{}*********",productId);

        String stockNum = redisUtil.get(productId.toString());
        if (stockNum != "0" && !"0".equals(stockNum) && stockNum != null) {
            log.info("********查询缓存库存成功*********"+stockNum);
            return Integer.parseInt(stockNum);
        }

        Stock stock = stockService.queryByProductId(productId);
        if(stock != null){
            redisUtil.set(stock.getProductId().toString(),String.valueOf(stock.getStockNum()));
            log.info("********查询库存成功*********"+stock.getStockNum());
            return stock.getStockNum();
        }

        log.info("********此库存不存在*********"+productId);
        return 0;
    }

    @GetMapping("/stock/updateStockNum/{productId}/{stockNum}")
    public int updateStockNumByProductId(@PathVariable("productId")  Long productId,@PathVariable("stockNum")  int stockNum) {

//        try{
//            if(Integer.valueOf(stockNum) instanceof Integer && Integer.valueOf(productId) instanceof Integer){
//                log.info("******输入有效******");
//            }
//        }catch (Exception e){
//            e.getMessage();
//        }


        Stock stock = null;

        stock.setStockNum(stockNum);
        stock = stockService.queryByProductId(productId);

        if(stock != null){
            redisUtil.set(stock.getProductId().toString(),String.valueOf(stock.getStockNum()));
            log.info("********更新库存成功*********"+stock.getStockNum());
            return stock.getStockNum();
        }

        log.info("********此库存不存在*********"+productId);
        return 0;
    }

    @GetMapping("/stock/updateStockByOrder/{productId}/{orderNum}")
    public int updateStockByOrder(@PathVariable("productId")  Long productId,@PathVariable("orderNum")  int orderNum) {

        int success = stockService.updateStockByOrder(productId,orderNum);

        if(success>0){
            Stock stock = stockService.queryByProductId(productId);
            redisUtil.set(stock.getProductId().toString(),String.valueOf(stock.getOrderNum()));
            log.info("********更新库存成功*********"+stock.getStockNum());
            return stock.getStockNum();
        }

        log.info("********此库存不存在*********"+productId);
        return 0;
    }

    @GetMapping("/stock/decrease/{productId}/{orderNum}")
    public boolean decrease(@PathVariable("productId")  int productId,@PathVariable("orderNum")  int orderNum){

        boolean success = stockService.decrease(productId,orderNum);

        if(success){
            log.info("********减少库存成功*********");
            return true;
        }else{
            log.info("********减少库存失败*********");
            return false;
        }
    }


}
