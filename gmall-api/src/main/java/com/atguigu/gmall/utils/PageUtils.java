package com.atguigu.gmall.utils;

import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.HashMap;
import java.util.Map;

public class PageUtils {

    public static Map<String, Object> getPageMap(IPage pages,Integer pageSize) {
        //封装数据
        Map<String, Object> map = new HashMap<>();
        map.put("pagetSize",pageSize);
        map.put("totalPage",pages.getPages());
        map.put("total",pages.getTotal());
        map.put("pageNum",pages.getCurrent());
        map.put("list",pages.getRecords());

        return map;
    }
}
