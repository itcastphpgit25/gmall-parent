package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductAttributeCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 产品属性分类表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductAttributeCategoryService extends IService<ProductAttributeCategory> {

    void addCategory(String name);

    boolean deleteById(Long id);

    Map<String,Object> pageProduct(Integer pageSize, Integer pageNum);

    Map<String,Object> updateByIdCategory(Long id,String name);
}
