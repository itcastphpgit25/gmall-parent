package com.atguigu.gmall.rabbit.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable{

    private String orderId;  //订单id
    private String status;  //订单状态
    private BigDecimal price; //价格
}
