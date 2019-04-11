package com.atguigu.gmall.constant;

public class RedisCacheConstant {

    public static final String PRODUCT_CATEGORY_CACHE_KEY = "gmall:product:category:cache";

    public static final String PRODUCT_INFO_CACHE_KEY="guli:shop:product:info";
    //用户key
    public static final String USER_INFO_CACHE_KEY = "gulishop:user:info:";

    public static final Long USER_INFO_TIMEOUT =3L;
    //没登录的购物车token前缀
    public static final String CART_TEMP = "gmall:cart:temp:";
    //登录的购物车token前缀
    public static final String USER_CART = "gmall:cart:user:";

    //商品秒杀环节
    public static final String SEC_KILL="gmall:sec:";
}
