package com.atguigu.gmall.portal.controller;


import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



@Api(tags = "订单服务")
@Controller
@RequestMapping("/order")
public class OrderController {

//    @Reference
//    OrderAndPayService orderAndPayService;
//
//    @Reference
//    CartService cartService;
//
//    @Reference
//    MemberService memberService;
//    /**
//     * 订单确认页需要的所有数据
//     *
//     */
//    @ResponseBody
//    @PostMapping("/orderconfirm")
//    public CommonResult jiesuan(@RequestParam(value = "token")String token){
//        //1.需要结算的商品的信息，目前是获取到的购物车里面的商品的信息
//        //List<CartItem> cartItemList=cartService.cartItemsForJieSuan(token);
//        //2.查优惠劵
//        //3.用户可选的地址列表
//
//        return new CommonResult().success("");
//    }
}
