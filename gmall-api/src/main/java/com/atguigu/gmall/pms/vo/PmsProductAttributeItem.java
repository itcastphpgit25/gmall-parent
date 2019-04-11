package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.entity.ProductAttributeCategory;
import lombok.Data;

import java.util.List;

@Data
public class PmsProductAttributeItem{
     //ProductAttributeCategory productAttributeCategory;
     List<ProductAttribute> productAttributeList;
     ProductAttributeCategory productAttributeCategory;

}
