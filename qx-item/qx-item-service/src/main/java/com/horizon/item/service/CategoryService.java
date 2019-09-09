package com.horizon.item.service;

import com.horizon.common.enums.ExceptionEnums;
import com.horizon.common.exception.QxException;
import com.horizon.item.mapper.CategoryMapper;
import com.horizon.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryListByPid(Long pid) {
        //mapper把对象中的非空属性作为查询条件
        Category category = new Category();
        category.setParentId(pid);
        List<Category> list= categoryMapper.select(category);
        //判断结果
        if (CollectionUtils.isEmpty(list)){
            throw new QxException(ExceptionEnums.CATEGORY_NOT_FOUND);
        }
        return list;
    }
}
