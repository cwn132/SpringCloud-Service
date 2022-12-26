package com.springcloud.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Payment implements Serializable {

    private Long paymentId;
    private String paymentSerial;
    private BigDecimal paymentPrice;
    private BigDecimal paymentTotalPrice;
    private int paymentNum;
    private Timestamp updateTime;
    private Long productId;


}
