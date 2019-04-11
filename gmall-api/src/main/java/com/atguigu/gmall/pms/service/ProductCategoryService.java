package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.vo.PmsProductCategoryParam;
import com.atguigu.gmall.pms.vo.PmsProductCategoryWithChildrenItem;
import com.atguigu.gmall.to.CommonResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品分类 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductCategoryService extends IService<ProductCategory> {


    List<PmsProductCategoryWithChildrenItem> nestedList();

    Map<String,Object> pageProductCategory(Integer pageSize, Integer pageNum, Long parentId);

    void addProductCategory(PmsProductCategoryParam productCategoryParam);

    ProductCategory selectCategoryById(Long id);

    boolean deleteCategoryById(Long id);

    Integer updateDeleteStatus(List<Long> ids, Integer showStatus);

    Integer updateNavStatus(List<Long> ids, Integer navStatus);

    boolean updateCategory(Long id, PmsProductCategoryParam productCategoryParam);
}
