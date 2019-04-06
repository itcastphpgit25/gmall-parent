package com.atguigu.gmall.cms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.cms.entity.SubjectProductRelation;
import com.atguigu.gmall.cms.mapper.SubjectProductRelationMapper;
import com.atguigu.gmall.cms.service.SubjectProductRelationService;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 专题商品关系表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class SubjectProductRelationServiceImpl extends ServiceImpl<SubjectProductRelationMapper, SubjectProductRelation> implements SubjectProductRelationService {
    @Override
    public List<SubjectProductRelation> getPrefrenceAreaProductRelationServiceById(Long productId) {
        SubjectProductRelationMapper baseMapper = getBaseMapper();
        List<SubjectProductRelation> subjectProductRelationList = baseMapper.selectList(new QueryWrapper<SubjectProductRelation>().eq("product_id", productId));
        return subjectProductRelationList;
    }

    //根据productid修改更新数据集合
    @Override
    public void updateSubjectProductRelationById(Long id,PmsProductParam productParam) {
        SubjectProductRelationMapper baseMapper = getBaseMapper();
        List<SubjectProductRelation> subjectProductRelationList = baseMapper.selectList(new QueryWrapper<SubjectProductRelation>().eq("product_id", id));
        for (SubjectProductRelation subjectProductRelation : subjectProductRelationList) {
            List<SubjectProductRelation> list = productParam.getSubjectProductRelationList();
            for (SubjectProductRelation productRelation : list) {
                if(productRelation.getId()==subjectProductRelation.getId()){
                    BeanUtils.copyProperties(productRelation,subjectProductRelation);
                    baseMapper.updateById(subjectProductRelation);
                }
            }

        }
    }
}
