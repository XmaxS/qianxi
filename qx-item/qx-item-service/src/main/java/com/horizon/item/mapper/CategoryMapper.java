package com.horizon.item.mapper;


import com.horizon.item.pojo.Category;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;


@Repository
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category, Long> {
}
