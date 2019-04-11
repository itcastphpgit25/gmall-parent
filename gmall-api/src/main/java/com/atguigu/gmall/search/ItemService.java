package com.atguigu.gmall.search;

import com.atguigu.gmall.item.to.ProductAllInfos;

public interface ItemService {
    ProductAllInfos getInfo(Long skuId);
}
