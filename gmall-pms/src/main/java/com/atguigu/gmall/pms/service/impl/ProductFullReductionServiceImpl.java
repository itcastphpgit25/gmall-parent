package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.ProductFullReduction;
import com.atguigu.gmall.pms.mapper.MemberPriceMapper;
import com.atguigu.gmall.pms.mapper.ProductFullReductionMapper;
import com.atguigu.gmall.pms.service.ProductFullReductionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 产品满减表(只针对同商品) 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductFullReductionServiceImpl extends ServiceImpl<ProductFullReductionMapper, ProductFullReduction> implements ProductFullReductionService {
    @Override
    public List<ProductFullReduction> getProductFullReductionById(Long productId) {
        ProductFullReductionMapper baseMapper = getBaseMapper();
        List<ProductFullReduction> productFullReductionList = baseMapper.selectList(new QueryWrapper<ProductFullReduction>().eq("product_id", productId));
        return productFullReductionList;
    }
}
