package com.atguigu.gmall.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.atguigu.gmall.cart.bean.Cart;
import com.atguigu.gmall.cart.bean.SkuResponse;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;


@Api(tags = "购物车模块")
@RestController
@CrossOrigin
@RequestMapping("/cart")
public class CartController {
    /**
     * 最终返回json数据
     *注意事项：
     * 1.添加成功返回刚才添加的这一项的数据，包括购物车cart-key
     * 2.前端传一个cartKey,否则就新建一个购物车
     *
     */
    @Reference
    CartService cartService;

    @ApiOperation(value = "添加商品到购物车")
    @PostMapping("/add")
    public CommonResult addToCart(@RequestParam("skuId")Long skuId,
                                  @RequestParam("num") Integer num,
                                  @RequestParam("token")String token,
                                  @RequestParam("cartKey")String cartKey)
    {
        // oken
        RpcContext.getContext().setAttachment("gmallusertoken",token);
        //给客户返回一个购物车标志,包含购物项信息（CartItem）
        SkuResponse skuResponse=cartService.addToCart(skuId,num,cartKey);
        return new CommonResult().success(skuResponse);
    }

    @ApiOperation(value = "修改商品数量")
    @PostMapping("/update")
    public CommonResult updateCart(
            @ApiParam(value = "需要添加的商品的skuId")
            @RequestParam("skuId") Long skuId,
            @ApiParam(value = "需要添加的商品的数量")
            @RequestParam("num") Integer num,
            @ApiParam(value = "用户登陆后传递自己的token，没有可以不传递")
            @RequestParam("token") String token,
            @ApiParam(value = "传递之前后台返回的购物车的标识，没有可以不传递")
            @RequestParam("cartKey") String cartKey
    ){

        RpcContext.getContext().setAttachment("gmallusertoken",token);
        boolean update=cartService.updateCount(skuId,num,cartKey);
        return new CommonResult().success(update);

    }
    @ApiOperation(value = "删除商品")
    @PostMapping("/delete")
    public CommonResult deleteCart(
            @ApiParam(value = "需要删除的商品的skuId")
            @RequestParam("skuId") Long skuId,
            @ApiParam(value = "用户登陆后传递自己的token，没有可以不传递")
            @RequestParam("token") String token,
            @ApiParam(value = "传递之前后台返回的购物车的标识，没有可以不传递")
            @RequestParam("cartKey") String cartKey
    ){

        RpcContext.getContext().setAttachment("gmallusertoken",token);
        boolean delete=cartService.deleteCount(skuId,cartKey);
        return new CommonResult().success(delete);

    }
    @ApiOperation(value = "选中不选中商品数量")
    @PostMapping("/check")
    public CommonResult addCart(
            @ApiParam(value = "选中不选中商品的skuId")
            @RequestParam("skuId") Long skuId,
            @ApiParam(value = "需要选中的商品，0不选中，1选中")
            @RequestParam("flag") Integer flag,
            @ApiParam(value = "用户登陆后传递自己的token，没有可以不传递")
            @RequestParam("token") String token,
            @ApiParam(value = "传递之前后台返回的购物车的标识，没有可以不传递")
            @RequestParam("cartKey") String cartKey
    ){

        RpcContext.getContext().setAttachment("gmallusertoken",token);
        boolean check=cartService.checkCount(skuId,flag,cartKey);
        return new CommonResult().success(check);

    }

    @GetMapping("/list")
    public CommonResult list(@ApiParam(value = "用户登陆后传递自己的token，没有可以不传递")
                             @RequestParam("token") String token,
                             @ApiParam(value = "传递之前后台返回的购物车的标识，没有可以不传递")
                             @RequestParam("cartKey") String cartKey){

        RpcContext.getContext().setAttachment("gmallusertoken",token);
        Cart cart = cartService.cartItemsList(cartKey);
        return new CommonResult().success(cart);

    }

}
