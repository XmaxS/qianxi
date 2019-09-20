package com.horizon.item.api;

import com.horizon.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface BrandApi {
    //根据id查询品牌
    @GetMapping("brand/{id}")
    Brand queryBrandById(@PathVariable("id")Long id);
}
