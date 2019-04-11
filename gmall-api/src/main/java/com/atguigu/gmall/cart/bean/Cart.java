package com.atguigu.gmall.cart.bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * 购物车对象
 * 给前端最终返回的
 */
@Setter
public class Cart implements Serializable{
    @Getter
    private List<CartItem> items; //购物车中的购物项

    private Integer total;  //购物车中商品数量
    private BigDecimal totalPrice; //购物车商品总价格

    public Integer getTotal(){
        //计算总数量
        AtomicReference<Integer> count = new AtomicReference<>(0);
        if(items!=null&&items.size()>0){
            items.forEach((i)->{
                count.set(i.getNum()+count.get());
            });
        }
        return count.get();
    }

    public BigDecimal getTotalPrice() {
       //计算总价格
        AtomicReference<BigDecimal> price = new AtomicReference<>(new BigDecimal("0"));
        if(items!=null&&items.size()>0){
            items.forEach((i)->{
                //每个购物项的价格
                BigDecimal price1 = i.getPrice();
                //每个购物项的价格乘以每个购物项的个数
                BigDecimal multiply = price1.multiply(new BigDecimal("" + i.getNum()));
                //尬歌购物项的总价格加上其他所有的购物项价格
                price.set(multiply.add(price.get()));
            });
        }
        return price.get();
    }
}
