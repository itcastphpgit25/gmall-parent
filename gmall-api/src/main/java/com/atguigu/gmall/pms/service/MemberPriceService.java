package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.MemberPrice;
import com.atguigu.gmall.pms.entity.ProductFullReduction;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品会员价格表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface MemberPriceService extends IService<MemberPrice> {

    //根据productId查询MemberPrice数据
    List<MemberPrice> getMemberById(Long productId);


}
