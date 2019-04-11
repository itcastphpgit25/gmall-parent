package com.atguigu.gmall.cas.config;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveBoxingDelegate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//此注解表示将配置文件内的所有以 oauth.weibo 开头的属性值赋值给此类的属性
@ConfigurationProperties(prefix = "oauth.weibo")
@Configuration
@Data
public class WeiboOAuthConfig {

    /**
     *
     * oauth.weibo.appKey=445142506
     oauth.weibo.appSecret=15c40922344468fcfc7446f8e473a3bd
     oauth.weibo.authSuccessUrl=http://localhost:1100/auth/success
     oauth.weibo.authSuccessFail
     */

    private String appKey;
    private String appSecret;
    private String authSuccessUrl;
    private String authSuccessFail;
    private String authPage;
    private String accessTokenPage; //获取accesstoken地址
}
