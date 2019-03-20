package com.atguigu.gmall.admin.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.cms.service.PrefrenceAreaService;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 商品优选管理Controller
 */
@RestController
@Api(tags = "CmsPrefrenceAreaController", description = "商品优选管理")
@RequestMapping("/prefrenceArea")
public class CmsPrefrenceAreaController {
    @Reference
    private PrefrenceAreaService prefrenceAreaService;

    @ApiOperation("获取所有商品优选")
    @GetMapping(value = "/listAll")
    public Object listAll() {
       // List<PrefrenceArea> prefrenceAreaList = prefrenceAreaService.listAll();
        //TODO 获取所有商品优选
        return new CommonResult().success(null);
    }
}
