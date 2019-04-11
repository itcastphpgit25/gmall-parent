package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cms.entity.PrefrenceAreaProductRelation;
import com.atguigu.gmall.cms.entity.SubjectProductRelation;
import com.atguigu.gmall.cms.service.PrefrenceAreaProductRelationService;
import com.atguigu.gmall.cms.service.SubjectProductRelationService;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.mapper.*;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.atguigu.gmall.pms.vo.PmsProductQueryParam;
import com.atguigu.gmall.search.GmallSearchService;
import com.atguigu.gmall.to.es.EsProduct;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.atguigu.gmall.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Slf4j
@Service
@Component
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
       @Autowired
       ProductLadderService productLadderService;
       @Autowired
       MemberPriceService memberPriceService;
       @Autowired
       ProductFullReductionService productFullReductionService;
       @Autowired
       SkuStockService  skuStockService;
       @Autowired
       ProductAttributeValueService productAttributeValueService;
       //以下远程调用
       @Reference
       SubjectProductRelationService subjectProductRelationService;
       @Reference
       PrefrenceAreaProductRelationService prefrenceAreaProductRelationService;

       @Autowired
       ProductVertifyRecordService productVertifyRecordService;
       @Autowired
       ProductLadderMapper productLadderMapper;
       @Autowired
       ProductFullReductionMapper productFullReductionMapper;
       @Autowired
       MemberPriceMapper  memberPriceMapper;
       @Autowired
       ProductAttributeValueMapper productAttributeValueMapper;
       @Autowired
       ProductCategoryMapper  productCategoryMapper;
       @Autowired
       SkuStockMapper skuStockMapper;

       @Autowired
       ProductMapper productMapper;

       //远程调用seacher模块方法保存es
       @Reference(version = "1.0")
       GmallSearchService searchService;

       @Autowired
       JedisPool jedisPool;
    //使用ThreadLocal共享数据，线程安全由java底层来控制
       ThreadLocal<Product> productThreadLocal=new ThreadLocal<Product>();
    @Override
    public   Map<String,Object> pageProduct(PmsProductQueryParam productQueryParam,Integer pageSize, Integer pageNum) {
        ProductMapper baseMapper = getBaseMapper();

        Product product = new Product();
        BeanUtils.copyProperties(productQueryParam,product);

        Page<Product> page = new Page<Product>(pageNum,pageSize);

        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        if(null!=product.getPublishStatus()) {
            queryWrapper.eq("publish_status", product.getPublishStatus());
        }
        if(null!=product.getVerifyStatus()){
            queryWrapper.eq("verify_status",product.getVerifyStatus());
        }
        if(null!=product.getKeywords()){
            queryWrapper.like("keywords",product.getKeywords());
        }
        if(null!=product.getProductSn()){
            queryWrapper.eq("product_sn",product.getProductSn());
        }
        if(null!=product.getProductCategoryId()){
            queryWrapper.eq("product_category_id",product.getProductCategoryId());
        }
        if(null!=product.getProductCategoryId()){
            queryWrapper.eq("product_category_id",product.getProductCategoryId());
        }
        if(null!=product.getBrandId()){
            queryWrapper.eq("brand_id",product.getBrandId());
        }


        IPage<Product> selectPage = baseMapper.selectPage(page,queryWrapper);

        Map<String, Object> map= PageUtils.getPageMap(selectPage, pageSize);
        System.out.println(map.get("list"));
        return map;
    }

    /**
     *
     * 事物的传播行为
     * REQUIRED
     * SUPPORTS
     * MANDATORY
     * REQUIRES_NEW
     * NOT_SUPPORTED
     * NEVER
     * NESTED
     * @param productParam
     */
    @Transactional(propagation=Propagation.REQUIRED)
    @Override
    public void addProductParam(PmsProductParam productParam) {
        //创建代理对象实现自己调自己
        ProductServiceImpl psProxy = (ProductServiceImpl) AopContext.currentProxy();
        //1和5方法要同生共死，放一个方法
        psProxy.saveBaseProductCategoryCount(productParam);

        //2.保存商品阶梯价格
        psProxy.saveProductLadder(productParam.getProductLadderList());   //【REQUIRED_NEW】
        //3.保存商品满减价格到 pms_product_full_reduction
        psProxy.saveProductFullReduction(productParam.getProductFullReductionList());
        //4.保存商品的会员价格pms_member_price
        psProxy.saveMemberPrice(productParam.getMemberPriceList());
        //6.保存参数及自定义规格到pms_product_attribute_value
        psProxy.saveProductAttributeValue(productParam.getProductAttributeValueList());
        //7.更新商品分类数目
        psProxy.updateProductCategoryCount();
    }
    //1和5方法,其他炸了跟我没关系
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBaseProductCategoryCount(PmsProductParam productParam){
        ProductServiceImpl psProxy = (ProductServiceImpl) AopContext.currentProxy();
        //1.保存商品的基本信息pms_product(将刚才保存的这个商品的自增id获取出来)
        psProxy.saveProduct(productParam); //【REQUIRED】
        //5.保存商品的sku库存到 pms_sku_stock
        psProxy.saveSkuInfo(productParam.getSkuStockList());
    }

    //1.大保存：保存商品的基本信息pms_product(将刚才保存的这个商品的自增id获取出来)
    @Transactional(propagation=Propagation.REQUIRED) //开新车
    public Long saveProduct(PmsProductParam pmsProductParam){
        ProductMapper baseMapper = getBaseMapper();
        Product product = new Product();

        BeanUtils.copyProperties(pmsProductParam,product);
        int insert = baseMapper.insert(product);
        log.debug("获取productId{}",product.getId());

        //共享product，其实是用id
        productThreadLocal.set(product);

        return product.getId();

    }
   //2.保存商品阶梯价格
    @Transactional(propagation = Propagation.REQUIRES_NEW) //开新车
    public void saveProductLadder(List<ProductLadder> productLadderList){
        Product product = productThreadLocal.get();
        //以下五个用REQUIRED_NEW
        //2.保存商品的阶梯价格到 pms_product_ladder
       // List<ProductLadder> productLadderList = pmsProductParam.getProductLadderList();
        for (ProductLadder ladder : productLadderList) {
            //将productId set到VO中的ladder List中
           ladder.setProductId(product.getId());
            productLadderMapper.insert(ladder);
        }

    }
    //3.保存商品满减价格到 pms_product_full_reduction
    @Transactional(propagation = Propagation.REQUIRES_NEW) //开新车
    public void saveProductFullReduction(List<ProductFullReduction> productFullReductions) {
         Product product = productThreadLocal.get();
        //以下五个用REQUIRED_NEW
        //2.保存商品的阶梯价格到 pms_product_ladder
        for (ProductFullReduction productFullReduction : productFullReductions) {
            productFullReduction.setProductId(product.getId());
            productFullReductionMapper.insert(productFullReduction);
        }
    }
    //4.保存商品的会员价格pms_member_price
    @Transactional(propagation = Propagation.REQUIRES_NEW) //开新车
    public void saveMemberPrice(List<MemberPrice> memberPriceList) {
        Product product = productThreadLocal.get();
        //以下五个用REQUIRED_NEW
        //2.保存商品的阶梯价格到 pms_product_ladder
        for (MemberPrice memberPrice : memberPriceList) {
            memberPrice.setProductId(product.getId());
            memberPriceMapper.insert(memberPrice);
        }
    }
    //5.存库:保存Sku信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSkuInfo(List<SkuStock> skuStockList){
        Product product = productThreadLocal.get();
        //1.线程安全的。遍历修改不安全
        AtomicReference<Integer> i = new AtomicReference<>(0);
        //格式化一位数字为两位
        NumberFormat numberInstance = DecimalFormat.getNumberInstance();
        numberInstance.setMinimumIntegerDigits(2);
        numberInstance.setMaximumIntegerDigits(2);

        for (SkuStock skuStock : skuStockList) {
            //1.保存商品id
            skuStock.setProductId(product.getId());
            //SKU编码 k_商品id_自增数字
            String format=numberInstance.format(i.get());

            String code="K_"+product.getId()+"_"+format;
            //2.保存编码
            skuStock.setSkuCode(code);
            //自增
            i.set(i.get()+1);
            skuStockMapper.insert(skuStock);
        }
    }

    //6.保存参数及自定义规格到pms_product_attribute_value
    @Transactional(propagation = Propagation.REQUIRES_NEW) //开新车
    public void saveProductAttributeValue(List<ProductAttributeValue> productAttributeValueList) {
        Product product = productThreadLocal.get();
        //以下五个用REQUIRED_NEW
        //2.保存商品的阶梯价格到 pms_product_ladder
        for (ProductAttributeValue productAttributeValue : productAttributeValueList) {
            productAttributeValue.setProductId(product.getId());
            productAttributeValueMapper.insert(productAttributeValue);
        }
    }
    //7.更新商品分类数目
    @Transactional(propagation = Propagation.REQUIRES_NEW) //开新车
    public void updateProductCategoryCount() {
        Product product = productThreadLocal.get();
        Long id = product.getProductCategoryId();
        //根据ProductCategory的id修改分类数目
        productCategoryMapper.updateCountById(id);
    }

    //id查询编辑信息
    @Override
    public PmsProductParam getProductParam(Long productId) {
        //1..获取到基本信息
        ProductMapper baseMapper = getBaseMapper();
        //创建VO
        PmsProductParam pmsProductParam = new PmsProductParam();
        //查询出Vo父类product数据
        Product product = baseMapper.selectById(productId);
        BeanUtils.copyProperties(product,pmsProductParam);

        //1.ProductLadder集合
        List<ProductLadder> ladderList = productLadderService.getLadderById(productId);
        pmsProductParam.setProductLadderList(ladderList);

        //2.MemberPrice集合
        List<MemberPrice>  memberList=memberPriceService.getMemberById(productId);
        pmsProductParam.setMemberPriceList(memberList);

        //3.ProductFullReduction集合
        List<ProductFullReduction> productFullReductionList=productFullReductionService.getProductFullReductionById(productId);
        pmsProductParam.setProductFullReductionList(productFullReductionList);

        //4.SkuStock集合
        List<SkuStock> skuStockServiceList=skuStockService.getSkuStockServiceById(productId);
        pmsProductParam.setSkuStockList(skuStockServiceList);

        //5.productAttributeValueService
        List<ProductAttributeValue> productAttributeValueList=productAttributeValueService.getProductAttributeValueById(productId);
        pmsProductParam.setProductAttributeValueList(productAttributeValueList);

        //6.subjectProductRelationService集合
        List<SubjectProductRelation> subjectProductRelationList=subjectProductRelationService.getPrefrenceAreaProductRelationServiceById(productId);
        pmsProductParam.setSubjectProductRelationList(subjectProductRelationList);
        //7.prefrenceAreaProductRelationService集合
        List<PrefrenceAreaProductRelation> prefrenceAreaProductRelationList=prefrenceAreaProductRelationService.getPrefrenceAreaProductRelation(productId);
        pmsProductParam.setPrefrenceAreaProductRelationList(prefrenceAreaProductRelationList);
        return pmsProductParam;
     }

    //根据keyword查询
    @Override
    public List<Product> selectProduct(String keyword) {
        ProductMapper baseMapper = getBaseMapper();
        List<Product> products = baseMapper.selectList(new QueryWrapper<Product>().like("keywords", keyword));
        if(products==null){
            return null;
        }
        return products;
    }

    //批量修改状态
    @Override
    public boolean updateByProduct(List<Long> ids, Integer verifyStatus, String detail) {
        ProductMapper baseMapper = getBaseMapper();
        int i=0;
        boolean b=false;
        //1.遍历ids查询Product对象
        for (Long id : ids) {
            //1.根据id获取对应的对象并修改状态码
            Product product = baseMapper.selectById(id);
            product.setVerifyStatus(verifyStatus);
            //2.修改状态码
             i = baseMapper.updateById(product);
            //3.远程修改detail
            b=productVertifyRecordService.updateProductVertifyRecord(detail,id);
        }
        return i>0?true:false;
    }

    //根据商品id修改属性

    @Override
    public void updateProductById(Long id, PmsProductParam productParam) {
        //1.修改基本属性
        ProductMapper baseMapper = getBaseMapper();

        Product product = baseMapper.selectById(id); //根据id获取到修改的数据
        BeanUtils.copyProperties(productParam,product); //前端传过来的数据覆盖bean product
        baseMapper.updateById(product);

        //2.修改各个集合属性
        //修改ProductLadder
        List<ProductLadder> productLadderList = productLadderMapper.selectList(new QueryWrapper<ProductLadder>().eq("product_id", id));
        for (ProductLadder ladder : productLadderList) {
            //VO对象
            List<ProductLadder> ladderList = productParam.getProductLadderList();
            for (ProductLadder productLadder : ladderList) {
                if(ladder.getId()==productLadder.getId()){
                    BeanUtils.copyProperties(productLadder,ladder);
                    productLadderMapper.updateById(ladder);
                }
            }
        }
        //修改productFullReductionList
        List<ProductFullReduction> productFullReductionList = productFullReductionMapper.selectList(new QueryWrapper<ProductFullReduction>().eq("product_id", id));
        for (ProductFullReduction productFullReduction : productFullReductionList) {
            List<ProductFullReduction> list = productParam.getProductFullReductionList();
            for (ProductFullReduction fullReduction : list) {
                if(fullReduction.getId()==productFullReduction.getId()){
                    BeanUtils.copyProperties(fullReduction,productFullReduction);
                    productFullReductionMapper.updateById(productFullReduction);
                }
            }

        }
        //修改memberPriceList
        List<MemberPrice> memberPriceList = memberPriceMapper.selectList(new QueryWrapper<MemberPrice>().eq("product_id",id));
        for (MemberPrice memberPrice : memberPriceList) {
            List<MemberPrice> memberPriceList1 = productParam.getMemberPriceList();
            for (MemberPrice memberList : memberPriceList1) {
                if(memberList.getId()==memberPrice.getId()){
                    BeanUtils.copyProperties(memberList,memberPrice);
                    memberPriceMapper.updateById(memberPrice);
                }
            }
        }
        //更新skuStockList
        List<SkuStock> skuStockList = skuStockMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id", id));
        for (SkuStock skuStock : skuStockList) {
            List<SkuStock> list = productParam.getSkuStockList();
            for (SkuStock stock : list) {
                if(stock.getId()==skuStock.getId()){
                    BeanUtils.copyProperties(stock,skuStock);
                    skuStockMapper.updateById(skuStock);
                }
            }

        }
        //修改productAttributeValueList
        List<ProductAttributeValue> productAttributeValueList = productAttributeValueMapper.selectList(new QueryWrapper<ProductAttributeValue>().eq("product_id", id));
        for (ProductAttributeValue productAttributeValue : productAttributeValueList) {
            List<ProductAttributeValue> list = productParam.getProductAttributeValueList();
            for (ProductAttributeValue attributeValue : list) {
                if(attributeValue.getId()==productAttributeValue.getId()){
                    BeanUtils.copyProperties(attributeValue,productAttributeValue);
                    productAttributeValueMapper.updateById(productAttributeValue);
                }
            }
        }
        //修改subjectProductRelationList
        //远程调用查询
        //List<SubjectProductRelation> subjectProductRelationList =subjectProductRelationService.selectSubjectList(id);
        subjectProductRelationService.updateSubjectProductRelationById(id,productParam);

        //修改prefrenceAreaProductRelationList
        prefrenceAreaProductRelationService.updatePrefrenceAreaProductRelationListById(id,productParam);

    }

    //批量上下架

//    @Override
//    public Integer updateByIdsStatus(List<Long> ids, Integer publishStatus) {
//        ProductMapper baseMapper = getBaseMapper();
//        Integer i= 0;
//        for (Long id : ids) {
//            Product product = baseMapper.selectById(id);
//            if(product!=null){
//                product.setPublishStatus(publishStatus);
//                baseMapper.updateById(product);
//                i++;
//            }
//
//        }
//        return i;
//    }
    //批量推荐商品

    @Override
    public Integer updateRecommendStatus(List<Long> ids, Integer recommendStatus) {
        ProductMapper baseMapper = getBaseMapper();
        Integer i= 0;
        for (Long id : ids) {
            Product product = baseMapper.selectById(id);
            if(product!=null){
                product.setRecommandStatus(recommendStatus);
                baseMapper.updateById(product);
                i++;
            }

        }
        return i;
    }
    //批量设为新品

    @Override
    public Integer updateNewStatus(List<Long> ids, Integer newStatus) {
        ProductMapper baseMapper = getBaseMapper();
        Integer i= 0;
        for (Long id : ids) {
            Product product = baseMapper.selectById(id);
            if(product!=null){
                product.setNewStatus(newStatus);
                baseMapper.updateById(product);
                i++;
            }

        }
        return i;
    }
    //批量修改删除状态
    @Override
    public Integer updateDeleteStatus(List<Long> ids, Integer deleteStatus) {
        ProductMapper baseMapper = getBaseMapper();
        Integer i= 0;
        for (Long id : ids) {
            Product product = baseMapper.selectById(id);
            if(product!=null){
                product.setDeleteStatus(deleteStatus);
                baseMapper.updateById(product);
                i++;
            }

        }
        return i;
    }

    @Override
    public List<Product> productCategoryId(Long productCategoryId) {
        ProductMapper baseMapper = getBaseMapper();
        List<Product> products = baseMapper.selectList(new QueryWrapper<Product>().eq("product_category_id", productCategoryId));
        if(products==null){
            return null;
        }
        return products;
    }

    //批量上下架

    @Override
    public void publishStatus(List<Long> ids, Integer publishStatus) {
        //上架：1 下架：0
        if(publishStatus==1){
             //上架
             publishProduct(ids);
         }else {
             //下架
             removeProduct(ids);
         }
    }

    private void publishProduct(List<Long> ids){
        ProductMapper baseMapper = getBaseMapper();
        //1、查当前需要上架的商品的sku信息和spu信息
        ids.forEach((id)->{
            //(1)SPU:商品信息
            Product product = baseMapper.selectById(id);
            //(2)需要上架的SKU:从库存中查
            List<SkuStock> skuStocks = skuStockMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id", id));
            //(3)这个商品所有的参数值：界面分类显示
            List<EsProductAttributeValue> attributeValues=productAttributeValueMapper.selectProductAttrValue(product.getId());
            //(4)修改信息，将其发布到es：统计上架状态是否全部完成
            //原子操作
            AtomicReference<Integer> count = new AtomicReference<>(0);
            skuStocks.forEach((sku)->{  //sku对应的是一个对象
                EsProduct esProduct = new EsProduct();
                BeanUtils.copyProperties(product,esProduct);
                //5.修改商品标题，加上sku的销售属性
                if(!StringUtils.isEmpty(sku.getSp3())) {
                    esProduct.setName(product.getName() + " " + sku.getSp1() + " " + sku.getSp2() + " " + sku.getSp3());
                }else{
                    esProduct.setName(product.getName() + " " + sku.getSp1() + " " + sku.getSp2());
                }
                esProduct.setPrice(sku.getPrice());
                esProduct.setStock(sku.getStock());
                esProduct.setSale(sku.getSale());
                esProduct.setAttrValueList(attributeValues);
                //改写id，使用sku的id
                esProduct.setId(sku.getId());
                //修改es中：5个成了，3个败了，不成
                //保存到es中
                boolean es = searchService.saveProductInfoToES(esProduct);
                count.set(count.get()+1);
                if(es){
                    //保存当前的id，list.add(id);

                }
            });
            //8.判断是否完全上架成功，成功改数据库状态
            if(count.get()==skuStocks.size()){
                //修改数据库状态；都是包装类型，允许null值
                Product update = new Product();
                update.setId(product.getId());
                update.setPublishStatus(1);
                baseMapper.updateById(update);
            }else {
                //9）、成功的撤销操作；来保证业务数据的一致性；
                //es有失败  list.forEach(remove());
            }
        });
    }

    private void removeProduct(List<Long> ids){

    }

    /**
     * 为了防止系统出毛病（比如突然断电，锁释放不了），，就给锁设置自动超时时间
     *
     * @param productId
     * @return
     */
    //根据sku 的id 获得商品id --》 获取商品详细信息

    /**
     *
     * 分布式锁：
     *    1.占个坑
     *
     *    2.分布式锁伪代码：
     *        function a(){
     *            if(jedis.setnxex("key",token,timeout-3)){
     *                try{
     *                  //执行业务逻辑
     *                }finally{
     *                    jedis.eval(解锁脚本,key,value);
     *                }
     *            }else{
     *                //等待继续，递归锁，自旋锁
     *                a();
     *            }
     *        }
     * @param productId
     * @return
     */
    @Override
    public Product getProductByIdFromCache(Long productId) {
        ProductMapper baseMapper = getBaseMapper();
        //Product product = baseMapper.selectById(productId);

        //1.先去缓存中检索
        Jedis jedis = jedisPool.getResource();
        Product product=null;
        String s = jedis.get(RedisCacheConstant.PRODUCT_INFO_CACHE_KEY + productId);
        if(StringUtils.isEmpty(s)){
            //2.缓存中没有去数据库查询
            //占坑的时候需要给一个唯一的标识UUID,代表当前lock值
            String token = UUID.randomUUID().toString();

            //3.去redis中占坑，抢锁 1：占锁成功 0：占锁失败
            //Long lock = jedis.setnx("lock", "123");
            //即占坑又设置过期时间
            String lock = jedis.set("lock", token, SetParams.setParams().ex(5).nx());
            if(!StringUtils.isEmpty(lock)&&"ok".equalsIgnoreCase(lock)){
                //设置所得超时时间
                //1)有可能占好位后还没设置超时时间突然停机
                //jedis.expire("lock",5);

                //
                /**
                 * 1.获取到锁，查数据，放在缓存中
                 * 2.无论数据是否有值都因该放在缓存中，防止穿透
                 */
                try {
                    product = baseMapper.selectById(productId);
                    String json = JSON.toJSONString(product);
                    /**
                     *
                     * 如果数据库中有查找数据就将 保存到缓存中的数据时间设置长一些
                     *
                     * 如果数据库中没有查找的数据就将 保存到缓存中的数据时间设置短一些
                     */
                    if (product == null) {
                        int anInt = new Random().nextInt(2000);
                        //保存到缓存数据库并设置时间
                        jedis.setex(RedisCacheConstant.PRODUCT_INFO_CACHE_KEY + productId, 60 + anInt, json);
                    } else {
                        //过期时间
                        int anInt = new Random().nextInt(2000);
                        jedis.setex(RedisCacheConstant.PRODUCT_INFO_CACHE_KEY + product, 60 * 60 * 24 * 3 + anInt, json);
                    }
                }finally {
                    //存到数据库之后释放锁(坑)
                    //此处比较与删锁应该原子性:使用luy脚本
//                    if(token.equals(jedis.get(lock))){
//                        jedis.del("lock");
//                    }
                    //luy脚本
                    String script =
                            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    //执行脚本
                    jedis.eval(script,Collections.singletonList("lock"),Collections.singletonList(token));
                }

            }else {  //没有抢到锁就等着
                try {
                    Thread.sleep(1000);
                    //如果没有获取到锁，我们等待一会，再去缓存看
                    getProductByIdFromCache(productId);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }
        }else {
            //4.缓存中有,反序列化成想要的数据
            product = JSON.parseObject(s, Product.class);
        }
        jedis.close();
        return product;
    }

    //查询销售属性的列表
    @Override
    public List<EsProductAttributeValue> getProductSaleAttr(Long productId) {

        return productMapper.getProductSaleAttr(productId);
    }
    //查询基本属性值的列表
    @Override
    public List<EsProductAttributeValue> getProductBaseAttr(Long productId) {

        return productMapper.getProductBaseAttr(productId);
    }

    @Override
    public SkuStock getSkuInfo(Long skuId) {
        //加上缓存查询

        SkuStock skuStock = skuStockMapper.selectById(skuId);
        return skuStock;
    }
}

