package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.entity.ProductAttributeCategory;
import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.mapper.ProductAttributeMapper;
import com.atguigu.gmall.pms.service.ProductAttributeCategoryService;
import com.atguigu.gmall.pms.service.ProductAttributeService;
import com.atguigu.gmall.pms.service.ProductCategoryService;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.vo.PmsProductAttributeCategoryItem;
import com.atguigu.gmall.pms.vo.PmsProductAttributeItem;
import com.atguigu.gmall.pms.vo.PmsProductAttributeParam;
import com.atguigu.gmall.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.events.Event;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品属性参数表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductAttributeServiceImpl extends ServiceImpl<ProductAttributeMapper, ProductAttribute> implements ProductAttributeService {
    @Autowired
    ProductCategoryService productCategoryService;
    @Autowired
    ProductService productService;
    @Autowired
    ProductAttributeCategoryService productAttributeCategoryService;
    @Override
    public Map<String, Object> selectProductAttributeByCategory(Long cid, Integer type, Integer pageNum, Integer pageSize) {
        //分页
        ProductAttributeMapper baseMapper = getBaseMapper();

        Page<ProductAttribute> page = new Page<>(pageNum,pageSize);

        QueryWrapper<ProductAttribute> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_attribute_category_id",cid).eq("type",type);

        IPage<ProductAttribute> pages = baseMapper.selectPage(page, queryWrapper);
        System.out.println(pages);
        return PageUtils.getPageMap(pages,pageSize);
    }
    //添加商品属性信息

    @Override
    public boolean addPmsProductAttributeParam(PmsProductAttributeParam productAttributeParam) {
        ProductAttributeMapper baseMapper = getBaseMapper();
        if(productAttributeParam==null){
            return false;
        }
        ProductAttribute productAttribute = new ProductAttribute();
        BeanUtils.copyProperties(productAttributeParam,productAttribute);
        int insert = baseMapper.insert(productAttribute);
        return insert>0?true:false;
    }
    //根据id修改属性

    @Override
    public boolean updateProductAttribute(Long id, PmsProductAttributeParam productAttributeParam) {
        ProductAttributeMapper baseMapper = getBaseMapper();
        ProductAttribute productAttribute = baseMapper.selectById(id);
        if(productAttribute==null) {
            return false;
        }
        BeanUtils.copyProperties(productAttributeParam, productAttribute);
        Integer i = baseMapper.updateById(productAttribute);
        return null!=i && i>0;
    }
    //根据id查询数据

    @Override
    public ProductAttribute selectById(Long id) {
        ProductAttributeMapper baseMapper = getBaseMapper();
        ProductAttribute productAttribute = baseMapper.selectById(id);
        if(productAttribute==null){
            return null;
        }
        return productAttribute;
    }
    //根据ids删除

    @Override
    public Integer deleteIds(List<Long> ids) {
        ProductAttributeMapper baseMapper = getBaseMapper();
        Integer i=0;
        for (Long id : ids) {
            ProductAttribute productAttribute = baseMapper.selectById(id);
            if(productAttribute!=null){
                baseMapper.deleteById(id);
                i++;
            }
        }
        return i;
    }
    //根据商品分类的id获取商品属性及属性分类
//    @Override
//    public PmsProductAttributeItem getBoolData(Long productCategoryId) {
//        //最终返回的数据
//        PmsProductAttributeItem pmsProductAttributeItem = new PmsProductAttributeItem();
//
//        //获取product中的分类id product_attribute_category_id：一个分类的id对应多个商品
//        List<Product> product = productService.productCategoryId(productCategoryId);
//
//        //1.获取商品属性
//        ProductAttributeMapper baseMapper = getBaseMapper();
//
//        for (Product productList : product) {
//            List<ProductAttribute> productAttributeList = baseMapper.selectList(new QueryWrapper<ProductAttribute>().eq("product_attribute_category_id", productList.getProductAttributeCategoryId()));
//        }
//
//        List<ProductAttribute> list = pmsProductAttributeItem.getProductAttributeList();
//        for (ProductAttribute list1 : list) {
//            for (ProductAttribute productAttribute : productAttributeList) {
//                  BeanUtils.copyProperties(productAttribute,list1);
//            }
//        }
//
//
//        //2.获取属性分类
//        ProductAttributeCategory productAttributeCategory = productAttributeCategoryService.getById(product.getProductAttributeCategoryId());
//        pmsProductAttributeItem.setProductAttributeCategory(productAttributeCategory);
//        return pmsProductAttributeItem;
//
//    }
}
