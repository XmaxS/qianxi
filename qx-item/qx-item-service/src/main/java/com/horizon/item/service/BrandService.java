package com.horizon.item.service;


import com.horizon.common.vo.PageResult;
import com.horizon.item.pojo.Brand;
import com.horizon.item.pojo.Category;
import com.horizon.item.vo.BrandVo;

import java.util.List;

/**
 * @author bystander
 * @date 2018/9/15
 */
public interface BrandService {

    PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key);

    void saveBrand(Brand brand, List<Long> cids);

    List<Category> queryCategoryByBid(Long bid);

    void updateBrand(BrandVo brandVo);

    void deleteBrand(Long bid);

    List<Brand> queryBrandByCid(Long cid);

    Brand queryBrandByBid(Long id);

    List<Brand> queryBrandByIds(List<Long> ids);

}
