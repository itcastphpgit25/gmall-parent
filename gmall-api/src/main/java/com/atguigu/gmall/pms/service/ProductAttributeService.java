package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.vo.PmsProductAttributeCategoryItem;
import com.atguigu.gmall.pms.vo.PmsProductAttributeItem;
import com.atguigu.gmall.pms.vo.PmsProductAttributeParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品属性参数表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductAttributeService extends IService<ProductAttribute> {

    Map<String,Object> selectProductAttributeByCategory(Long cid, Integer type, Integer pageNum, Integer pageSize);

    boolean addPmsProductAttributeParam(PmsProductAttributeParam productAttributeParam);

    boolean updateProductAttribute(Long id, PmsProductAttributeParam productAttributeParam);

    ProductAttribute selectById(Long id);


    Integer deleteIds(List<Long> ids);

    //PmsProductAttributeItem getBoolData(Long productCategoryId);
}
