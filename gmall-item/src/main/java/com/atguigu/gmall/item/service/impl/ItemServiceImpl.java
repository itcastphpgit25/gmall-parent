package com.atguigu.gmall.item.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.item.to.ProductAllInfos;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.atguigu.gmall.search.ItemService;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Service(version = "1.0")
public class ItemServiceImpl implements ItemService{

    @Reference
    SkuStockService skuStockService;
    @Reference
    ProductService productService;

    @Override
    public ProductAllInfos getInfo(Long skuId) {

        /**
         * 1.热点数据【经常查】
         * 2.非热点数据【查询频率不高】
         *
         * 缓存
         * 1.经常读数据（一定要进缓存）
         * 2.经常改的数据【还是用缓存】【保持 缓存 和 数据库数据的一致性】
         *
         */

        ProductAllInfos infos = new ProductAllInfos();
        //1.当前sku的详细信息查出来，包括：：：销售属性的组合/库存/价格/
        SkuStock skuStock = skuStockService.getById(skuId);

        Long productId = skuStock.getProductId();
        //引入缓存机制
        //查询流程:
        /**
         *
         * 先去缓存中查，
         * 缓存中没有的去数据库中查，查出来的数据再放入缓存，下一次就不用查数据库了
         */
        //2.获取当前商品的详细信息
        Product product=productService.getProductByIdFromCache(productId);
        //Product product = productService.getById(productId);
        //3.所有sku的组合选法以及库存状态
        List<SkuStock> skuStocks=skuStockService.getAllSkuInfoByProductId(productId);


        //4.查询这个商品所有销售属性可选值 0--规格
        List<EsProductAttributeValue> saleAttr = productService.getProductSaleAttr(productId);


        //5.商品的其他属性值 1--参数
        List<EsProductAttributeValue> baseAttr=productService.getProductBaseAttr(productId);

        //6.当前商品涉及到的服务
        infos.setSaleAttr(saleAttr);
        infos.setBaseAttr(baseAttr);
        infos.setProduct(product);
        infos.setSkuStock(skuStock);  //当前sku信息
        infos.setSkuStocks(skuStocks);  //所有sku信息所

        return infos;
    }
}
