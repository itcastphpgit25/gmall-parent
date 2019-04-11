package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.ProductAttributeCategory;
import com.atguigu.gmall.pms.mapper.ProductAttributeCategoryMapper;
import com.atguigu.gmall.pms.mapper.ProductMapper;
import com.atguigu.gmall.pms.service.ProductAttributeCategoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 产品属性分类表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductAttributeCategoryServiceImpl extends ServiceImpl<ProductAttributeCategoryMapper, ProductAttributeCategory> implements ProductAttributeCategoryService {
    @Override
    public void addCategory(String name) {
        ProductAttributeCategoryMapper baseMapper = getBaseMapper();
        ProductAttributeCategory productAttributeCategory = new ProductAttributeCategory();
        productAttributeCategory.setName(name);
        baseMapper.insert(productAttributeCategory);
    }

    @Override
    public boolean deleteById(Long id) {
        ProductAttributeCategoryMapper baseMapper = getBaseMapper();
        Integer i = baseMapper.deleteById(id);

        return null!=i && i>0;
    }

    @Override
    public Map<String, Object> pageProduct(Integer pageSize, Integer pageNum) {

        ProductAttributeCategoryMapper baseMapper = getBaseMapper();

        Page<ProductAttributeCategory> page = new Page<ProductAttributeCategory>(pageNum,pageSize);
        IPage<ProductAttributeCategory> productAttributeCategoryIPage = baseMapper.selectPage(page, null);
        //IPage<Product> selectPage = this.baseMapper.selectPage(page,null);

        //封装数据
        Map<String, Object> map = new HashMap<>();
        map.put("pagetSize",pageSize);
        map.put("totalPage",productAttributeCategoryIPage.getPages());
        map.put("total",productAttributeCategoryIPage.getTotal());
        map.put("pageNum",productAttributeCategoryIPage.getCurrent());
        map.put("list",productAttributeCategoryIPage.getRecords());
        return map;
    }

    @Override
    public Map<String,Object> updateByIdCategory(Long id,String name){
        HashMap<String, Object> map = new HashMap<>();

        ProductAttributeCategoryMapper baseMapper = getBaseMapper();
        ProductAttributeCategory productAttributeCategory = baseMapper.selectById(id);
        if(productAttributeCategory==null){
            map.put("error","此数据不存在...");
            return map;
        }

        productAttributeCategory.setName(name);
        baseMapper.updateById(productAttributeCategory);
        map.put("success","修改成功！");
        return  map;
    }
}
