package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.atguigu.gmall.pms.vo.PmsProductQueryParam;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductService extends IService<Product> {

    Map<String,Object> pageProduct(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum);

    void addProductParam(PmsProductParam productParam);

    List<Product> selectProduct(String keyword);


    PmsProductParam getProductParam(Long id);

    //批量修改状态
    boolean updateByProduct(List<Long> ids, Integer verifyStatus, String detail);

    void updateProductById(Long id, PmsProductParam productParam);

    //Integer updateByIdsStatus(List<Long> ids, Integer publishStatus);

    Integer updateRecommendStatus(List<Long> ids, Integer recommendStatus);

    Integer updateNewStatus(List<Long> ids, Integer newStatus);

    Integer updateDeleteStatus(List<Long> ids, Integer deleteStatus);

    List<Product> productCategoryId(Long productCategoryId);

    void publishStatus(List<Long> ids, Integer publishStatus);


    Product getProductByIdFromCache(Long productId);
    List<EsProductAttributeValue> getProductSaleAttr(Long productId);

    List<EsProductAttributeValue> getProductBaseAttr(Long productId);


    SkuStock getSkuInfo(Long skuId);
}
