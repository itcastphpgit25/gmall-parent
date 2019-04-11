package com.atguigu.gmall.cms.service;

import com.atguigu.gmall.cms.entity.SubjectProductRelation;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 专题商品关系表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface SubjectProductRelationService extends IService<SubjectProductRelation> {

    List<SubjectProductRelation> getPrefrenceAreaProductRelationServiceById(Long productId);


    void updateSubjectProductRelationById(Long id,PmsProductParam productParam);
}
