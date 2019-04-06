package com.atguigu.gmall.cms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.cms.entity.PrefrenceAreaProductRelation;
import com.atguigu.gmall.cms.mapper.PrefrenceAreaProductRelationMapper;
import com.atguigu.gmall.cms.service.PrefrenceAreaProductRelationService;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 优选专区和产品关系表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class PrefrenceAreaProductRelationServiceImpl extends ServiceImpl<PrefrenceAreaProductRelationMapper, PrefrenceAreaProductRelation> implements PrefrenceAreaProductRelationService {
    @Override
    public List<PrefrenceAreaProductRelation> getPrefrenceAreaProductRelation(Long productId) {
        PrefrenceAreaProductRelationMapper baseMapper = getBaseMapper();
        List<PrefrenceAreaProductRelation> prefrenceAreaProductRelationList = baseMapper.selectList(new QueryWrapper<PrefrenceAreaProductRelation>().eq("product_id", productId));
        return prefrenceAreaProductRelationList;
    }

    //根据productId修改相关数据

    @Override
    public void updatePrefrenceAreaProductRelationListById(Long id,PmsProductParam productParam) {
        PrefrenceAreaProductRelationMapper baseMapper = getBaseMapper();
        List<PrefrenceAreaProductRelation> prefrenceAreaProductRelationList = baseMapper.selectList(new QueryWrapper<PrefrenceAreaProductRelation>().eq("product_id", id));
        for (PrefrenceAreaProductRelation prefrenceAreaProductRelation : prefrenceAreaProductRelationList) {

            List<PrefrenceAreaProductRelation> list = productParam.getPrefrenceAreaProductRelationList();

            for (PrefrenceAreaProductRelation areaProductRelation : list) {

                if(areaProductRelation.getId()==prefrenceAreaProductRelation.getId()){
                    BeanUtils.copyProperties(areaProductRelation,prefrenceAreaProductRelation);
                    baseMapper.updateById(prefrenceAreaProductRelation);
                }
            }

        }
    }
}
