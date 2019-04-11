package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gmall.cart.bean.Cart;
import com.atguigu.gmall.cart.bean.CartItem;
import com.atguigu.gmall.cart.bean.SkuResponse;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.ums.entity.Member;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Component
public class CartServiceImpl implements CartService{

    @Autowired
    StringRedisTemplate redisTemplate;
    @Reference
    ProductService productService;
    @Autowired
    RedissonClient redissonClient;

    /**
     *
     * 无论登录或不登陆都带cart-key,没等你陆我们会返回给cart-key，以后就用这个
     * 登陆了就额外加上自己的访问令牌：token
     *
     * @param skuId
     * @param num
     * @param cartKey
     * @return
     */
    @Override
    public SkuResponse addToCart(Long skuId, Integer num, String cartKey) {
        SkuResponse skuResponse = new SkuResponse();
        //通过RPC获取远程传输过来的token
        String token = RpcContext.getContext().getAttachment("gmallusertoken");
        System.out.println("token值："+token);

        //通过token获取redis中携带的数据
        String memberJson = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);

        //在线购物车流程
        //解析redis中对象
        Member member = JSON.parseObject(memberJson, Member.class);
        Long memberId=member==null?0L:member.getId();
        String memberName=member==null?"":member.getNickname();

        //1.查询sku信息，放到购物车
        SkuStock skuStock=productService.getSkuInfo(skuId);
        //2.查询spu信息：商品信息
        Product product = productService.getProductByIdFromCache(skuStock.getProductId());
        //4、要添加的购物项：封装成一个cartItem；
        CartItem item = new CartItem(product.getId(),
                skuStock.getId(),
                memberId,
                num,
                skuStock.getPrice(),//加入购物车事价格
                skuStock.getPrice(), //新价格
                num,
                skuStock.getSp1(), skuStock.getSp2(), skuStock.getSp3(),
                product.getPic(),
                product.getName(),
                memberName,
                product.getProductCategoryId(),
                product.getBrandName(),
                false,
                "满199减90"
        );
        //判断用户是否登录，没登录给用户一个临时购物车，登陆了用自己的购物车

        //2.没登录添加购物车  gmmall:cart:temp:cart-key 购物车数据
        if(StringUtils.isEmpty(memberJson)){
            //要么令牌没数据过期 要么没登录 进入离线购物车流程
            if(!StringUtils.isEmpty(cartKey)){ //说明离线状态有购物车
                skuResponse.setCartKey(cartKey);
                //用户有老购物车:只要是没登录都用临时cartKey
                cartKey=RedisCacheConstant.CART_TEMP+cartKey;
                //5.将购物项加入到了老服务购物车中
                addItemToCart(item,num,cartKey);//说明离线状态无购物车
            }else {
                 //新建一个购物车
                String replace = UUID.randomUUID().toString().replace("-","");
                String newCartKey = RedisCacheConstant.CART_TEMP + replace;
                skuResponse.setCartKey(replace);
                addItemToCart(item,num,newCartKey);
            }
        }else{
            //在线购物车状态
            //1.登陆后添加购物车 gmall:cart:userId --。购物车ID
            String loginCarKey=RedisCacheConstant.USER_CART+member.getId();
            //合并购物车
            mergeCart(RedisCacheConstant.CART_TEMP+cartKey,loginCarKey);
            //放入购物车
            addItemToCart(item,num,loginCarKey);
        }
        skuResponse.setItem(item);
        return skuResponse;
    }

    //修改商品数量
    @Override
    public boolean updateCount(Long skuId, Integer num, String cartKey) {
        String token = RpcContext.getContext().getAttachment("gmallusertoken");
        String memberJson= redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY+token);
        Member member = JSON.parseObject(memberJson, Member.class);
        RMap<String, String> map=null;
        if(member==null){
            //用户未登录
           map = redissonClient.getMap(RedisCacheConstant.CART_TEMP + cartKey);
        }else {
            //已经登录
            map = redissonClient.getMap(RedisCacheConstant.USER_CART + member.getId());
        }
        String s = map.get(skuId + "");
        CartItem cartItem = JSON.parseObject(s, CartItem.class);
        cartItem.setNum(num);
        String json = JSON.toJSONString(cartItem);
        map.put(skuId+"",json);  //直接覆盖掉原来key值相同的购物项
        return true;
    }
    //删除商品
    @Override
    public boolean deleteCount(Long skuId, String cartKey) {
        String token = RpcContext.getContext().getAttachment("gmallusertoken");
        String memberJson= redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY+token);
        Member member = JSON.parseObject(memberJson, Member.class);
        RMap<String, String> map=null;
        if(member==null){
            //用户未登录
            map = redissonClient.getMap(RedisCacheConstant.CART_TEMP + cartKey);
        }else {
            //已经登录
            map = redissonClient.getMap(RedisCacheConstant.USER_CART + member.getId());
        }
        map.remove(skuId+"");
        return true;
    }

    @Override
    public boolean checkCount(Long skuId, Integer flag, String cartKey) {
        String token = RpcContext.getContext().getAttachment("gmallusertoken");
        String memberJson = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberJson, Member.class);
        RMap<String, String> map=null;
        if(member==null){
            map = redissonClient.getMap(RedisCacheConstant.CART_TEMP+cartKey);
        }else{
            map=redissonClient.getMap(RedisCacheConstant.USER_CART+member.getId());
        }
        String s = map.get(skuId + "");
        CartItem cartItem = JSON.parseObject(s, CartItem.class);
        cartItem.setChecked(flag==0?false:true);
        String json = JSON.toJSONString(cartItem);
        map.put(skuId+"",json); //此处不是新增，而是修改了


        /**
         *
         * 目的：去结账，要在订单结算也刷出要结算的商品 0：没勾中，去除 1：勾中
         */
        //维护checked字段的set
        String checked = map.get("checked");
        //保存的是购物车里面的skuid
        HashSet<String> checkedSkuIds = new HashSet<>();

        //复杂的泛型数据转换

        if(!StringUtils.isEmpty(checked)){
            //购物车中有check字段  将其转换为Set字符串，不重复,
            Set<String> strings = JSON.parseObject(checked, new TypeReference<Set<String>>(){});
            if(flag==0){  //前端传来不够重：移除
                //没勾中
                strings.remove(skuId+"");
            }else{
                //勾中
                strings.add(skuId+"");
            }
            String s1 = JSON.toJSONString(strings);
            map.put("checked",s1);
        }else{
            //购物车中没有check字段,就添加一个
            checkedSkuIds.add(skuId+"");
            String s1 = JSON.toJSONString(checkedSkuIds);
            map.put("checked",s1);
        }

        return true;
    }

    //查询所有商品
    @Override
    public Cart cartItemsList(String cartKey) {
        String token = RpcContext.getContext().getAttachment("gmallusertoken");
        String memberJson = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberJson, Member.class);
        RMap<String, String> map=null;
        if(member==null){
            //用户未登录
            map = redissonClient.getMap(RedisCacheConstant.CART_TEMP +cartKey);
        }else{
            //用户登录
            //先合并购物车
            mergeCart(RedisCacheConstant.CART_TEMP+cartKey,RedisCacheConstant.USER_CART+member.getId());
            //在查询购物车
            map = redissonClient.getMap(RedisCacheConstant.USER_CART + member.getId());
        }
        if(map!=null){
            Cart cart = new Cart();
            //先占个位
            cart.setItems(new ArrayList<CartItem>());
            map.entrySet().forEach(entry->{
                if(!entry.getKey().equals("checked")){
                    String json = entry.getValue(); //得到购物项数据
                    CartItem cartItem = JSON.parseObject(json, CartItem.class);
                    cart.getItems().add(cartItem);
                }
            });
            return cart;

        }else{
            return new Cart();
        }
    }

    //合并购物车
    private void mergeCart(String oldCartKey, String newCartKey) {
       //1.获取老购物车
        RMap<String, String> map = redissonClient.getMap(oldCartKey);
        //有这个map并有这个数据entrySet
        if(null!=map&&map.entrySet()!=null){
            map.entrySet().forEach((entry)->{
                String key = entry.getKey();
                if(!key.equals("checked")) {
                    String value = entry.getValue();
                    //获取到购物项
                    CartItem cartItem = JSON.parseObject(value, CartItem.class);

                    //将老购物车的数据转移到新的购物车中
                    addItemToCart(cartItem, cartItem.getNum(), newCartKey);
                    //将老购物车中数据删除
                    map.remove(cartItem.getProductSkuId() + "");
                }
            });

        }
    }

    /**
     *给购物车中添加一项购物项
     * 1.第一次用购物车的时候都必须合并
     * 2.查看购物车数据
     * 3.加入购物车需要合并
     */
    private void addItemToCart(CartItem item,Integer num,String cartKey){
        //1、拿到购物车
        RMap<Object, Object> map = redissonClient.getMap(cartKey);
        //2.根据skuId 查看有没有此商品
        boolean b = map.containsKey(item.getProductSkuId()+"");
        if(b){
            //购物车已有此项:该数量
            String json = (String) map.get(item.getProductSkuId() + "");
            CartItem cartItem = JSON.parseObject(json, CartItem.class);

            cartItem.setNum(cartItem.getNum()+num);

            String s = JSON.toJSONString(cartItem);
            //相加后存入购物车
            map.put(item.getProductSkuId()+"",s);
        }else {
            //直接转换成json字符串存到map（购物车）中
            String s = JSON.toJSONString(item);
            map.put(item.getProductSkuId()+"",s);
        }
    }

    //
    /**
     * 远程调用根据：
     *    根据token查询用户购物车信息
     *
     */
//    @Override
//    public List<com.atguigu.gmall.oms.entity.CartItem> cartItemsForJieSuan(String token) {
//        return null;
//    }
}
