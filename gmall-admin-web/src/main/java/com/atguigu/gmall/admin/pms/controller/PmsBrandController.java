package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.pms.entity.Brand;
import com.atguigu.gmall.pms.service.BrandService;
import com.atguigu.gmall.pms.vo.PmsBrandParam;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 品牌功能Controller
 */
@CrossOrigin
@RestController
@Api(tags = "PmsBrandController",description = "商品品牌管理")
@RequestMapping("/brand")
public class PmsBrandController {
    @Reference
    private BrandService brandService;
    //正在运行
    //已经运行
    @ApiOperation(value = "获取全部品牌列表")
    @GetMapping(value = "/listAll")
    public Object getList() {
         Map<String,Object> brandList=brandService.listAll();
         if(brandList==null){
             return  new CommonResult().validateFailed("所查询内容为空...");
         }
        //TODO 获取全部品牌列表  brandService.listAll()
        return new CommonResult().success(brandList);
    }

    @ApiOperation(value = "添加品牌")
    @PostMapping(value = "/create")
    public Object create( @RequestBody PmsBrandParam pmsBrand, BindingResult result) {
        CommonResult commonResult = new CommonResult();
        //TODO 添加品牌
        System.out.println(pmsBrand);
        brandService.addBrand(pmsBrand);
        return commonResult.successMessage();
    }

    @ApiOperation(value = "更新品牌")
    @PutMapping(value = "/update/{id}")
    public Object update(@PathVariable("id") Long id,
                              @Validated @RequestBody PmsBrandParam pmsBrandParam,
                              BindingResult result){
         CommonResult commonResult = new CommonResult();

        //TODO 更新品牌
        Brand brand = new Brand();
        BeanUtils.copyProperties(pmsBrandParam,brand);
        brand.setId(id);
        brandService.updateBrandVoById(brand);
        return commonResult.successMessage();
    }

    @ApiOperation(value = "删除品牌")
    @DeleteMapping(value = "/delete/{id}")
    public Object delete(@PathVariable("id") Integer id) {
        CommonResult commonResult = new CommonResult();
        boolean result= brandService.deleteBrand(id);
        //TODO 删除品牌
        if(!result){
            return commonResult.failed();
        }
        return commonResult.success("删除成功");
    }

    @ApiOperation(value = "根据品牌名称分页获取品牌列表")
    @GetMapping(value = "/list")
    public Object getList(@RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        CommonResult commonResult = new CommonResult();

        //TODO 根据品牌名称分页获取品牌列表
        Map<String,Object> brandPageInfo=brandService.pageBrand(keyword,pageNum,pageSize);

        return commonResult.success(brandPageInfo);
    }

    @ApiOperation(value = "根据编号查询品牌信息")
    @GetMapping(value = "/{id}")
    public Object getItem(@PathVariable("id") Long id) {
        CommonResult commonResult = new CommonResult();
        //TODO 根据编号查询品牌信息
        Brand brand=brandService.selectById(id);
        if(brand==null){
           return commonResult.validateFailed("查询有误，可能没有您查找的数据...");
        }
        return commonResult.success(brand);
    }

    @ApiOperation(value = "批量删除品牌")
    @PostMapping(value = "/delete/batch")
    public Object deleteBatch(@RequestParam("ids") List<Long> ids) {
        CommonResult commonResult = new CommonResult();
        //TODO 批量删除品牌
        boolean result=brandService.deleteBatch(ids);
        if(!result){
           return commonResult.failed();
        }
        return commonResult.successMessage();
    }

    @ApiOperation(value = "批量更新显示状态")
    @PostMapping(value = "/update/showStatus")
    public Object updateShowStatus(@RequestParam("ids") List<Long> ids,
                                   @RequestParam("showStatus") Integer showStatus) {
        CommonResult commonResult = new CommonResult();
        //TODO 批量更新显示状态
        Integer result=brandService.updateByIdsshowStatus(ids,showStatus);
        if(result>0){
            return commonResult.success(result);
        }else{
            return commonResult.dataMessage(result);
        }
    }

    @ApiOperation(value = "批量更新厂家制造商状态")
    @PostMapping(value = "/update/factoryStatus")
    public Object updateFactoryStatus(@RequestParam("ids") List<Long> ids,
                                      @RequestParam("factoryStatus") Integer factoryStatus) {

        //TODO 批量更新厂家制造商状态
        CommonResult commonResult = new CommonResult();
        Integer result=brandService.updateByIdsFactoryStatus(ids,factoryStatus);
        if(result>0){
            return commonResult.success(result);
        }else{
            return commonResult.dataMessage(result);
        }
    }
}
