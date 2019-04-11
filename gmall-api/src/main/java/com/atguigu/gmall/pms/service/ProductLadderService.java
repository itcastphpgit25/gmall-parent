package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductLadder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 产品阶梯价格表(只针对同商品) 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductLadderService extends IService<ProductLadder> {

    //根据id查询数据
    List<ProductLadder> getLadderById(Long id);
}
