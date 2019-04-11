package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.ProductVertifyRecord;
import com.atguigu.gmall.pms.mapper.ProductVertifyRecordMapper;
import com.atguigu.gmall.pms.service.ProductVertifyRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 商品审核记录 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductVertifyRecordServiceImpl extends ServiceImpl<ProductVertifyRecordMapper, ProductVertifyRecord> implements ProductVertifyRecordService {
//    @Override
//    public boolean updateProductVertifyRecord(List<Long> ids) {
//        ProductVertifyRecordMapper baseMapper = getBaseMapper();
//        //先根据ids查询出数据对象
//        baseMapper.selectList(new QueryWrapper<Product>().eq("product_id",ids))
//        return false;
//    }



    /**
     * List<Product> products:根据集合ids获得的对象
     * id：ids
     *
     * @param
     * @param id
     * @return
     */
    @Override
    public boolean updateProductVertifyRecord(String detail,Long id){
        ProductVertifyRecordMapper baseMapper = getBaseMapper();
        QueryWrapper<ProductVertifyRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id",id);
        //查询出要修改的数据对象
        List<ProductVertifyRecord> recordList = baseMapper.selectList(queryWrapper);

        for (ProductVertifyRecord productVertifyRecord : recordList) {
                  productVertifyRecord.setDetail(detail);
                  baseMapper.updateById(productVertifyRecord);
        }

        return null!=recordList?true:false;
    }
}
