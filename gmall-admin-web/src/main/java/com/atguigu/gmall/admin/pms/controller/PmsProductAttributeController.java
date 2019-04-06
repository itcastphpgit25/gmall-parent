package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.service.ProductAttributeService;
import com.atguigu.gmall.pms.vo.PmsProductAttributeCategoryItem;
import com.atguigu.gmall.pms.vo.PmsProductAttributeItem;
import com.atguigu.gmall.pms.vo.PmsProductAttributeParam;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 商品属性管理Controller
 */
@CrossOrigin
@RestController
@Api(tags = "PmsProductAttributeController", description = "商品属性管理")
@RequestMapping("/productAttribute")
public class PmsProductAttributeController {
    @Reference
    private ProductAttributeService productAttributeService;
    //有毛病
    @ApiOperation("根据分类查询属性列表或参数列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "type", value = "0表示属性，1表示参数", required = true, paramType = "query", dataType = "integer")})
    @GetMapping(value = "/list/{cid}")
    public Object getList(@PathVariable Long cid,
                          @RequestParam(value = "type") Integer type,
                          @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        //TODO 根据分类查询属性列表或参数列表
        Map<String,Object> pages=productAttributeService.selectProductAttributeByCategory(cid,type,pageNum,pageSize);
        return new CommonResult().success(pages);
    }

    @ApiOperation("添加商品属性信息")
    @PostMapping(value = "/create")
    public Object create(@Valid @RequestBody PmsProductAttributeParam productAttributeParam, BindingResult bindingResult) {
        //TODO 添加商品属性信息
        boolean result=productAttributeService.addPmsProductAttributeParam(productAttributeParam);
        if(!result){
            return new CommonResult().failed();
        }
        return new CommonResult().successMessage();
    }

    @ApiOperation("修改商品属性信息")
    @PostMapping(value = "/update/{id}")
    public Object update(@PathVariable Long id, @RequestBody PmsProductAttributeParam productAttributeParam, BindingResult bindingResult){
        //TODO 修改商品属性信息
        boolean result=productAttributeService.updateProductAttribute(id,productAttributeParam);
        if(!result){
            return new CommonResult().failed();
        }
        return new CommonResult().successMessage();
    }

    @ApiOperation("查询单个商品属性")
    @GetMapping(value = "/{id}")
    public Object getItem(@PathVariable Long id){
        //TODO 查询单个商品属性
        ProductAttribute productAttribute=productAttributeService.selectById(id);
        if(productAttribute==null){
            return new CommonResult().success(null);
        }
        return new CommonResult().success(productAttribute);
    }

    @ApiOperation("批量删除商品属性")
    @PostMapping(value = "/delete")
    public Object delete(@RequestParam("ids") List<Long> ids){
        //TODO 批量删除商品属性
        Integer result=productAttributeService.deleteIds(ids);
        if(result>0){
            return new CommonResult().success(result);
        }else{
            return new CommonResult().dataMessage(result);
        }
    }
    //未做
    @ApiOperation("根据商品分类的id获取商品属性及属性分类")
    @GetMapping(value = "/attrInfo/{productCategoryId}")
    public Object getAttrInfo(@PathVariable Long productCategoryId){
        //TODO 根据分类查询属性列表或参数列表
       // PmsProductAttributeItem pmsProductAttributeItem=productAttributeService.getBoolData(productCategoryId);
        return new CommonResult().success(null);
    }
}
