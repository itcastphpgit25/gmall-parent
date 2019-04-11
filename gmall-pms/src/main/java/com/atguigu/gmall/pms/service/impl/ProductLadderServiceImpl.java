package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.ProductLadder;
import com.atguigu.gmall.pms.mapper.ProductLadderMapper;
import com.atguigu.gmall.pms.service.ProductLadderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 产品阶梯价格表(只针对同商品) 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductLadderServiceImpl extends ServiceImpl<ProductLadderMapper, ProductLadder> implements ProductLadderService {
    @Override
    public List<ProductLadder> getLadderById(Long id) {
        ProductLadderMapper baseMapper = getBaseMapper();
        List<ProductLadder> ladderList = baseMapper.selectList(new QueryWrapper<ProductLadder>().eq("product_id",id));
        System.out.println(ladderList);
        return ladderList;
    }
}
