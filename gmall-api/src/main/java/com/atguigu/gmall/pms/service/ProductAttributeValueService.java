package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductAttributeValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 存储产品参数信息的表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductAttributeValueService extends IService<ProductAttributeValue> {

    List<ProductAttributeValue> getProductAttributeValueById(Long productId);
}
