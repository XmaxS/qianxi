package com.horizon.item.mapper;


import com.horizon.item.pojo.Brand;
import com.horizon.item.pojo.Category;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface BrandMapper extends Mapper<Brand>,SelectByIdListMapper<Brand, Long> {

    @Insert("insert into tb_category_brand (category_id, brand_id) values (#{cid}, #{bid})")
    int saveCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Select("select * from tb_category where id in (select category_id from tb_category_brand where brand_id = #{bid})")
    List<Category> queryCategoryByBid(Long bid);

    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    int deleteCategoryBrandByBid(Long bid);

    @Select("select * from tb_brand where id in (select brand_id from tb_category_brand where category_id = #{cid})")
    List<Brand> queryBrandByCid(Long cid);

}
