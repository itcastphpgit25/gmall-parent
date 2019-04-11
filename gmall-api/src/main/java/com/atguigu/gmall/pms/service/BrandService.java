package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.Brand;
import com.atguigu.gmall.pms.vo.PmsBrandParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 品牌表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface BrandService extends IService<Brand> {

    Map<String,Object> pageBrand(String keyword, Integer pageNum, Integer pageSize);

    Map<String,Object> listAll();

    boolean deleteBrand(Integer id);

    void addBrand(PmsBrandParam pmsBrand);

    void updateBrandVoById(Brand brand);

    Brand selectById(Long id);

    boolean deleteBatch(List<Long> ids);

    Integer updateByIdsshowStatus(List<Long> ids, Integer showStatus);

    Integer updateByIdsFactoryStatus(List<Long> ids, Integer factoryStatus);
}
