package com.atguigu.gmall.cas.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cas.config.WeiboOAuthConfig;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.to.social.WeiboAccessTokenVo;
import com.atguigu.gmall.ums.entity.Member;
import com.atguigu.gmall.ums.entity.MemberSocial;
import com.atguigu.gmall.ums.service.MemberService;
import com.atguigu.gmall.ums.service.MemberSocialService;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@CrossOrigin
public class OAuth2Controller {
    @Autowired
    WeiboOAuthConfig config;
    //可以自己发送post get请求
    RestTemplate restTemplate=new RestTemplate();

    @Autowired
    StringRedisTemplate redisTemplate;

    @Reference(version = "1.0")
    MemberSocialService memberSocialService;
    /**
     *
     * 用户登录授权
     *
     * url:当完成一切流程后要跳转的页面路径
     * @param authType
     * @return
     */
    @GetMapping("/register/authorization")
    public String registerAuthorization(
            @RequestParam("authType")String authType,
            @RequestParam("url")String url,
            HttpSession session){

        session.setAttribute("url",url);
        if("weibo".equals(authType)){
            return "redirect:"+config.getAuthPage();
        }
        return "redirect:"+config.getAuthPage();
    }

    /**
     *
     * 授权后系统会自动返回一个code码
     * 此controller是微博调用的
     *
     * url:登录成功后要去哪个页面
     */
    @GetMapping("/auth/success")
    public String codeGetToken(@RequestParam("code")String code,
                               HttpSession session){
        //获取到code码
        System.out.println("获取到的code码"+code);
        /**
         * 1.根据code码去 weibo换取access_token
         * 2.换取access_token
         */
        String authPage=config.getAccessTokenPage()+"&code="+code;
        //自己发送请求,获取token                               发送获取token 地址   请求参数   token返回类型
        WeiboAccessTokenVo tokenVo = restTemplate.postForObject(authPage, null, WeiboAccessTokenVo.class);
        //3.用户第一次登录进来 注册用户：将用户注册进系统
        //获取到Accesstoken获取用户信息查看是否第一次登录：第一次登陆保存用户
        Member memberInfo=memberSocialService.getMemberInfo(tokenVo);
        //获取到跳转的页面路径
        String url = (String)session.getAttribute("url");

        //次响应命令浏览器保存一个cookie；仅在访问 www.gmallshop.com有效
        String token = UUID.randomUUID().toString();
        String memberInfoJson = JSON.toJSONString(memberInfo);
        //用户信息保存在redis中
        redisTemplate.opsForValue().set(RedisCacheConstant.USER_INFO_CACHE_KEY+token,memberInfoJson);
        //地之中有token，会保存到浏览器的
         return "redirect:"+url+"?token="+token;  //获取token成功后跳转的页面地址
         //return tokenVo.getAccess_token();
    }
    /**
     * 登陆成功的用户以后的任何请求都带上token
     * @param token
     * @return
     */
    @ResponseBody
    @GetMapping("/userinfo")
    public Member getUserInfo(String token){

        String memberInfo = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);

        Member member = JSON.parseObject(memberInfo, Member.class);
        return member;
    }

}
