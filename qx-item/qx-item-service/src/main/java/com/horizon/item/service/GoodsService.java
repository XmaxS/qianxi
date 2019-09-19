package com.horizon.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.horizon.common.enums.ExceptionEnums;
import com.horizon.common.exception.QxException;
import com.horizon.common.vo.PageResult;
import com.horizon.item.mapper.SkuMapper;
import com.horizon.item.mapper.SpuDetailMapper;
import com.horizon.item.mapper.SpuMapper;
import com.horizon.item.mapper.StockMapper;
import com.horizon.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
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

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

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

    @Transactional
    public void saveGoods(Spu spu) {
        //新增Spu
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);

        int count = spuMapper.insert(spu);
        if (count != 1){
            throw new QxException(ExceptionEnums.GOODS_SAVE_ERROR);
        }

        //新增detail
        SpuDetail detail = spu.getSpuDetail();
        detail.setSpuId(spu.getId());
        detailMapper.insert(detail);
        saveSkuAndStock(spu);
    }

    public SpuDetail queryDetailById(Long id) {
        SpuDetail detail = detailMapper.selectByPrimaryKey(id);
        if (detail == null){
            throw new QxException(ExceptionEnums.GOODS_DETAIL_NOT_FOUND);
        }
        return detail;
    }

    public List<Sku> querySkuBySpuId(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skuList =  skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)){
            throw new QxException(ExceptionEnums.GOODS_SKU_NOT_FOUND);
        }

        //查询库存
//        for (Sku s : skuList){
//            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
//            if (stock == null){
//                throw new QxException(ExceptionEnums.GOODS_STOCK_NOT_FOUND);
//            }
//            s.setStock(stock.getStock());
//        }

        //查询库存
        List<Long> ids =  skuList.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stockList =  stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stockList)){
            throw new QxException(ExceptionEnums.GOODS_STOCK_NOT_FOUND);
        }

        //我们把Stock变成一个Map，其Key是： sku的id ，值是： 库存值
        Map<Long,Integer> stockMap =  stockList.stream()
                .collect(Collectors.toMap(Stock::getSkuId,Stock::getStock));
        skuList.forEach(s -> s.setStock(stockMap.get(sku.getId())));
        return skuList;
    }

    @Transactional
    public void updateGoods(Spu spu) {
        if (spu.getId() == null){
            throw new QxException(ExceptionEnums.GOODS_ID_CANNOT_BE_NULL);
        }
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        //查询sku
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)){
            //删除sku
            skuMapper.delete(sku);
            //删除stock
            List<Long> ids =  skuList.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(ids);
        }
        //修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);

        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count != 1){
            throw new QxException(ExceptionEnums.GOODS_UPDATE_ERROR);
        }
        //修改detail
        count =  detailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if (count != 1){
            throw new QxException(ExceptionEnums.GOODS_UPDATE_ERROR);
        }

        //新增sku和stock
        saveSkuAndStock(spu);
    }

    private void saveSkuAndStock(Spu spu){
        int count;
        //定义库存集合
        List<Stock> stocks = new ArrayList<>();

        //新增Sku
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus){
            sku.setCreateTime(new Date());
            sku.setCreateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());

            count =  skuMapper.insert(sku);

            if (count != 1){
                throw new QxException(ExceptionEnums.GOODS_SAVE_ERROR);
            }

            //新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());

            stocks.add(stock);
        }

        //批量新增库存
        count = stockMapper.insertList(stocks);

        if (count != stocks.size()){
            throw new QxException(ExceptionEnums.GOODS_SAVE_ERROR);
        }
    }
}
