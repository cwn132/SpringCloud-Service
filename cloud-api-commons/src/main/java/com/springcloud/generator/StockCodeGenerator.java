package com.springcloud.generator;

import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 仓库编号生成器
 * @date:
 **/
@Component
public class StockCodeGenerator {

    /** 自增序列  **/
    private static int i = 0;

    /**
     * 根据当前时间生成仓库编号："yyyyMMddHHmmss-自增序号"
     * @return
     */
    private static final String DATE_FORMAT = "yyyyMMddHHmmss";
    private static AtomicInteger ai  = new AtomicInteger(0);


    private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT);
        }
    };

    public static DateFormat getDateFormat() {
        return (DateFormat) threadLocal.get();
    }

    public static String generatorStockCode() {
        try {
            return "ST"+getDateFormat().format(new Date(System.currentTimeMillis()))
                    + i++;
        } finally {
            threadLocal.remove();
        }
    }

}
