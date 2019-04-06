package com.atguigu.gmall.portal.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.item.to.ProductAllInfos;
import com.atguigu.gmall.search.ItemService;
import org.springframework.web.bind.annotation.*;
import sun.misc.Version;
@CrossOrigin
@RestController
@RequestMapping("/item")
public class ItemController {

    @Reference(version = "1.0")
    ItemService itemService;

    @GetMapping(value = "/{skuId}.html",produces = "application/json")
    public ProductAllInfos productInfo(@PathVariable("skuId")Long skuId){
        ProductAllInfos allInfos=itemService.getInfo(skuId);
        return allInfos;
    }
}
