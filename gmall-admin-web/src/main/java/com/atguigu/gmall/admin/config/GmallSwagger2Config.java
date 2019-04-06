package com.atguigu.gmall.admin.config;

import com.google.common.base.Predicates;
import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class GmallSwagger2Config {

    @Bean("后台用户模块")
    public Docket userApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户模块")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/admin.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(true);
    }
    @Bean("后台商品模块")
    public Docket brandApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("商品模块")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/brand.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(true);
    }
    @Bean("商品分类管理模块")
    public Docket productCategoryApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("商品分类管理模块")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/productCategory.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(true);
    }
    @Bean("后台商品管理")
    public Docket productApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("商品管理")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/product.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(true);
    }
    @Bean("后台商品管理")
    public Docket cmsApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("商品专题管理")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/product.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(true);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("后台管理系统平台接口文档")
                .description("提供pms、oms、ums、cms、sms模块的文档")
                .termsOfServiceUrl("http://www.atguigu.com/")
                .version("1.0")
                .build();
    }



}
