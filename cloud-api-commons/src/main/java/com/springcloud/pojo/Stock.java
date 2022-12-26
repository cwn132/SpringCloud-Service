package com.springcloud.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Stock {

    private Long stockId;
    private Long productId;
    private int stockNum;
    private int orderNum;
    private String stockSerial;
    private Timestamp updateTime;
    private Timestamp beginTime;
    private Timestamp endTime;

}
