package com.atguigu.gmall.pms.entity;

import lombok.Data;

import java.util.List;

@Data
public class Test {

       Integer pagetSize,total,totalPage,pageNum;
       List<Product> list;

}
