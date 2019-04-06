package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.MemberPrice;
import com.atguigu.gmall.pms.entity.ProductFullReduction;
import com.atguigu.gmall.pms.mapper.MemberPriceMapper;
import com.atguigu.gmall.pms.service.MemberPriceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 商品会员价格表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class MemberPriceServiceImpl extends ServiceImpl<MemberPriceMapper, MemberPrice> implements MemberPriceService {
    @Override
    public List<MemberPrice> getMemberById(Long productId) {
        MemberPriceMapper baseMapper = getBaseMapper();

        List<MemberPrice> memberPriceList = baseMapper.selectList(new QueryWrapper<MemberPrice>().eq("product_id", productId));


        return memberPriceList;
    }


}
