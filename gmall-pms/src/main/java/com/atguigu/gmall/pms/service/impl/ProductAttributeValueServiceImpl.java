package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.ProductAttributeValue;
import com.atguigu.gmall.pms.mapper.ProductAttributeValueMapper;
import com.atguigu.gmall.pms.service.ProductAttributeValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 存储产品参数信息的表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductAttributeValueServiceImpl extends ServiceImpl<ProductAttributeValueMapper, ProductAttributeValue> implements ProductAttributeValueService {
    @Override
    public List<ProductAttributeValue> getProductAttributeValueById(Long productId) {
        ProductAttributeValueMapper baseMapper = getBaseMapper();
        List<ProductAttributeValue> productAttributeValueList = baseMapper.selectList(new QueryWrapper<ProductAttributeValue>().eq("product_id", productId));
        return productAttributeValueList;
    }
}
