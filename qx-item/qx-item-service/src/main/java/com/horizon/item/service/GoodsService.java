package com.horizon.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.horizon.common.enums.ExceptionEnums;
import com.horizon.common.exception.QxException;
import com.horizon.common.vo.PageResult;
import com.horizon.item.mapper.SpuDetailMapper;
import com.horizon.item.mapper.SpuMapper;
import com.horizon.item.pojo.Category;
import com.horizon.item.pojo.Spu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.Array;
import java.sql.Struct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper detailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page,rows);
        //过滤
        Example example =  new Example(Spu.class);
        //搜索字段过滤
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //上下架过滤
        if (saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }
        //默认排序
        example.setOrderByClause("last_update_time DESC");

        //查询
        List<Spu> list = spuMapper.selectByExample(example);

        //判断
        if (CollectionUtils.isEmpty(list)){
            throw new QxException(ExceptionEnums.GOODS_NOT_FOUND);
        }

        //解析分类和品牌名称
        loadCategoryAndBrandName(list);

        //解析分页结果
        PageInfo<Spu> info = new PageInfo<>(list);

        return new PageResult<>(info.getTotal(),list);
    }

    private void loadCategoryAndBrandName(List<Spu> list) {
        for (Spu spu : list){
            //处理分类名称
            List<String> names = categoryService.queryBuIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names,"/"));

            //处理品牌名称
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());

        }
    }
}
