package com.atguigu.gmall.cms.service;

import com.atguigu.gmall.cms.entity.PrefrenceAreaProductRelation;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 优选专区和产品关系表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface PrefrenceAreaProductRelationService extends IService<PrefrenceAreaProductRelation> {

    List<PrefrenceAreaProductRelation> getPrefrenceAreaProductRelation(Long productId);

    void updatePrefrenceAreaProductRelationListById(Long id,PmsProductParam productParam);
}
