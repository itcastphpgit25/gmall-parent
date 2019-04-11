package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.mapper.SkuStockMapper;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * sku的库存 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class SkuStockServiceImpl extends ServiceImpl<SkuStockMapper, SkuStock> implements SkuStockService {
    @Override
    public List<SkuStock> getSkuStockServiceById(Long productId) {
        SkuStockMapper baseMapper = getBaseMapper();
        List<SkuStock> skuStockList = baseMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id", productId));
        return skuStockList;
    }

    @Override
    public List<SkuStock> getAllSkuInfoByProductId(Long productId) {
        SkuStockMapper baseMapper = getBaseMapper();

        return baseMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id",productId));
    }
}
