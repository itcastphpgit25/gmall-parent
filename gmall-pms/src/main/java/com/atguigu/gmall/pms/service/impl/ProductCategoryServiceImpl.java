package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.mapper.ProductCategoryMapper;
import com.atguigu.gmall.pms.service.ProductCategoryService;
import com.atguigu.gmall.pms.vo.PmsProductCategoryParam;
import com.atguigu.gmall.pms.vo.PmsProductCategoryWithChildrenItem;
import com.atguigu.gmall.to.CommonResult;
import com.atguigu.gmall.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.xml.transform.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 产品分类 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Slf4j
@Service
@Component
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {
    //引入缓存
    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public List<PmsProductCategoryWithChildrenItem> nestedList() {
        //1.
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        //2.第一次查询缓存是否有数据
        String cache = ops.get(RedisCacheConstant.PRODUCT_CATEGORY_CACHE_KEY);
        //有数据情况下
        if(!StringUtils.isEmpty(cache)){
            log.debug("PRODUCT_CATEGORY_CACHE_KEY 缓存命中...");
            //转换过来返回出去，将数据解析成对应返回值的类型
            List<PmsProductCategoryWithChildrenItem> items = JSON.parseArray(cache,PmsProductCategoryWithChildrenItem.class);
            return items;
        }
        //缓存没数据情况下去数据库下查询
        log.debug("PRODUCT_CATEGORY_CACHE_KEY 缓存没有命中，去数据库查询缓存...");


        //1.最终得到的数据结果
        List<PmsProductCategoryWithChildrenItem> list = new ArrayList<>();

        //查找一级分类结点
        QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("level",0);
        //queryWrapper.orderByAsc("sort","id");
        List<ProductCategory> products = baseMapper.selectList(queryWrapper);

        //查找二级分类结点
        QueryWrapper<ProductCategory> queryWrapper2 = new QueryWrapper<>();
        queryWrapper.eq("level",1); //parent_id不等于0
        //queryWrapper.orderByAsc("sort","id");
        List<ProductCategory> products2 = baseMapper.selectList(queryWrapper2);


        //查找三级分类结点
        QueryWrapper<ProductCategory> queryWrapper3 = new QueryWrapper<>();
        queryWrapper.eq("level",2);  //parent_id不等于0
        //queryWrapper.orderByAsc("sort","id");
        List<ProductCategory> products3 = baseMapper.selectList(queryWrapper3);

       for (ProductCategory product1 : products) {
            //创建vo一级分类对象
            PmsProductCategoryWithChildrenItem pmsProductCategoryWithChildrenItem1 = new PmsProductCategoryWithChildrenItem();
            //将数据库查询出来的数据复制到前端vo中
            BeanUtils.copyProperties(product1,pmsProductCategoryWithChildrenItem1);
            //将一级分类存储到list中
            list.add(pmsProductCategoryWithChildrenItem1);


            //存储一级的子节点二级分类对象
           ArrayList<PmsProductCategoryWithChildrenItem> list1 = new ArrayList<>();
           for (ProductCategory product2 : products2) {
                if(product2.getParentId().equals(product1.getId())){
                    //二级分类VO对象
                    PmsProductCategoryWithChildrenItem pmsProductCategoryWithChildrenItem2 = new PmsProductCategoryWithChildrenItem();
                    BeanUtils.copyProperties(product2,pmsProductCategoryWithChildrenItem2);
                    list1.add(pmsProductCategoryWithChildrenItem2);


                    //存储二级的子节点三级分类对象
                    ArrayList<PmsProductCategoryWithChildrenItem> list2 = new ArrayList<>();
                    for (ProductCategory product3 : products3) {
                        if(product3.getParentId().equals(product2.getId())){
                            //三级vo对象
                            PmsProductCategoryWithChildrenItem pmsProductCategoryWithChildrenItem3 = new PmsProductCategoryWithChildrenItem();
                            BeanUtils.copyProperties(product3,pmsProductCategoryWithChildrenItem3);
                            list2.add(pmsProductCategoryWithChildrenItem3);
                        }
                    }
                    pmsProductCategoryWithChildrenItem2.setChildren(list2);
                }
            }
           pmsProductCategoryWithChildrenItem1.setChildren(list1);

        }


        //1.存到缓存数据库之前转换类型
        String jsonString = JSON.toJSONString(list);
        //查到缓存中，定义一个过期时间，时间3天
        ops.set(RedisCacheConstant.PRODUCT_CATEGORY_CACHE_KEY,jsonString,3, TimeUnit.DAYS);

        //将结果返回
        return list;
    }

    @Override
    public Map<String, Object> pageProductCategory(Integer pageSize, Integer pageNum, Long parentId) {
        ProductCategoryMapper baseMapper = getBaseMapper();
        Page<ProductCategory> productCategoryPage = new Page<>(pageNum, pageSize);

        IPage<ProductCategory> page = baseMapper.selectPage(productCategoryPage, new QueryWrapper<ProductCategory>().eq("parent_id", parentId));

        Map<String, Object> map = PageUtils.getPageMap(page, pageSize);


        return map;
    }

    //添加

    @Override
    public void addProductCategory(PmsProductCategoryParam productCategoryParam) {
        ProductCategoryMapper baseMapper = getBaseMapper();

        ProductCategory productCategory = new ProductCategory();

        BeanUtils.copyProperties(productCategoryParam,productCategory);

        baseMapper.insert(productCategory);
    }

    //根据id获取商品分类

    @Override
    public ProductCategory selectCategoryById(Long id) {
        ProductCategoryMapper baseMapper = getBaseMapper();
        ProductCategory productCategory = baseMapper.selectById(id);
        if(productCategory==null){
            return null;
        }
        return productCategory;
    }
    //根据id删除商品分类

    @Override
    public boolean deleteCategoryById(Long id) {
        ProductCategoryMapper baseMapper = getBaseMapper();
        ProductCategory productCategory = baseMapper.selectById(id);
        if(productCategory==null){
            return false;
        }
        Integer i = baseMapper.deleteById(id);
        return null!=i && i>0;
    }

    //修改显示状态

    @Override
    public Integer updateDeleteStatus(List<Long> ids, Integer showStatus) {
        ProductCategoryMapper baseMapper = getBaseMapper();
        Integer i= 0;
        for (Long id : ids) {
            ProductCategory productCategory = baseMapper.selectById(id);
            if(productCategory!=null){
                productCategory.setShowStatus(showStatus);
                baseMapper.updateById(productCategory);
                i++;
            }

        }
        return i;
    }

    //修改修改导航栏显示状态

    @Override
    public Integer updateNavStatus(List<Long> ids, Integer navStatus) {
        ProductCategoryMapper baseMapper = getBaseMapper();
        Integer i= 0;
        for (Long id : ids) {
            ProductCategory productCategory = baseMapper.selectById(id);
            if(productCategory!=null){
                productCategory.setNavStatus(navStatus);
                baseMapper.updateById(productCategory);
                i++;
            }

        }
        return i;
    }

    //修改商品分类
    @Override
    public boolean updateCategory(Long id, PmsProductCategoryParam productCategoryParam) {
        ProductCategoryMapper baseMapper = getBaseMapper();
        ProductCategory productCategory = baseMapper.selectById(id);
        if(productCategory==null){
            return false;
        }
        BeanUtils.copyProperties(productCategoryParam,productCategory);
        baseMapper.updateById(productCategory);
        return true;
    }
}
