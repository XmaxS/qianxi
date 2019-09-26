package com.horizon.item.service;



import com.horizon.item.pojo.Category;

import java.util.List;


public interface CategoryService {


    List<Category> queryCategoryByPid(Long pid);

    List<Category> queryCategoryByIds(List<Long> ids);

    List<Category> queryAllByCid3(Long id);
}
