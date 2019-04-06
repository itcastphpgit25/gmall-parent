package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.bean.Cart;
import com.atguigu.gmall.cart.bean.SkuResponse;

public interface CartService {
    SkuResponse addToCart(Long skuId, Integer num, String cartKey);

    boolean updateCount(Long skuId, Integer num, String cartKey);

    boolean deleteCount(Long skuId, String cartKey);

    boolean checkCount(Long skuId, Integer flag, String cartKey);

    Cart cartItemsList(String cartKey);


    //List<CartItem> cartItemsForJieSuan(String token);
}
