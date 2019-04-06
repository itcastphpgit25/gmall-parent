package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.Brand;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.mapper.BrandMapper;
import com.atguigu.gmall.pms.service.BrandService;
import com.atguigu.gmall.pms.vo.PmsBrandParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import sun.plugin2.util.NativeLibLoader;

import java.nio.file.FileStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Component
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {
    //BrandMapper baseMapper = getBaseMapper();

    @Override
    public Map<String, Object> pageBrand(String keyword, Integer pageNum, Integer pageSize) {
        BrandMapper baseMapper = getBaseMapper();
        QueryWrapper<Brand> queryWrapper = null;

        //keyword按照关键字查询
        if(!StringUtils.isEmpty(keyword)){
            queryWrapper.like("name",keyword)
                    .eq("first_letter",keyword);
        }
        Page<Brand> brandPage = new Page<Brand>(pageNum,pageSize);
        IPage<Brand> selectPage = baseMapper.selectPage(brandPage, queryWrapper);

        //封装数据
        Map<String, Object> map = new HashMap<>();
        map.put("pagetSize",pageSize);
        map.put("totalPage",selectPage.getPages());
        map.put("total",selectPage.getTotal());
        map.put("pageNum",selectPage.getCurrent());
        map.put("list",selectPage.getRecords());
        return map;
    }

    @Override
    public Map<String, Object> listAll() {
        BrandMapper baseMapper = getBaseMapper();
        List<Brand> list = baseMapper.selectList(null);
        Map<String, Object> map = new HashMap<>();
        map.put("brandList",list);
        return map;
    }

    @Override
    public boolean deleteBrand(Integer id) {
        BrandMapper baseMapper = getBaseMapper();
        Integer result = baseMapper.deleteById(id);
        return null!=result && result>0;
    }
    //添加品牌
    @Override
    public void addBrand(PmsBrandParam pmsBrand) {
        BrandMapper baseMapper = getBaseMapper();
        Brand brand = new Brand();
        System.out.println("1:::::::"+pmsBrand);
        BeanUtils.copyProperties(pmsBrand,brand);
        System.out.println("2:::::::"+brand);
        baseMapper.insert(brand);
    }

    //更新品牌

    @Override
    public void updateBrandVoById(Brand brand) {
        BrandMapper baseMapper = getBaseMapper();
        baseMapper.updateById(brand);
    }
    //根据id查询商品信息
    @Override
    public Brand selectById(Long id) {
        BrandMapper baseMapper = getBaseMapper();
        Brand brand = baseMapper.selectById(id);

        return brand;
    }

    //批量删除
    @Override
    public boolean deleteBatch(List<Long> ids) {
        BrandMapper baseMapper = getBaseMapper();
//        if(ids.size()>0){
//            for (Long id : ids) {
//                baseMapper.deleteById(id);
//            }
//            return true;
//        }
        ArrayList<Brand> list = new ArrayList<>();
        if(ids.size()>0){
            for (Long id : ids) {
                Brand brand = baseMapper.selectById(id);
                list.add(brand);
            }
        }else{
            return false;
        }
        for (Brand brand : list) {
            if(brand==null){
                return false;
            }
        }
        for (Long id : ids) {
            baseMapper.deleteById(id);
        }
        return true;
    }
    //批量更新显示状态
    @Override
    public Integer updateByIdsshowStatus(List<Long> ids, Integer showStatus) {
        BrandMapper baseMapper = getBaseMapper();
        Integer i= 0;
        for (Long id : ids) {
            Brand brand = baseMapper.selectById(id);
            if(brand!=null){
                brand.setShowStatus(showStatus);
                this.baseMapper.updateById(brand);
                i++;
            }

        }
        return i;
    }
    //批量修改制造商

    @Override
    public Integer updateByIdsFactoryStatus(List<Long> ids, Integer factoryStatus) {
        BrandMapper baseMapper = getBaseMapper();
        Integer i= 0;
        for (Long id : ids) {
            Brand brand = baseMapper.selectById(id);
            if(brand!=null){
                brand.setFactoryStatus(factoryStatus);
                this.baseMapper.updateById(brand);
                i++;
            }

        }
        return i;
    }
}
