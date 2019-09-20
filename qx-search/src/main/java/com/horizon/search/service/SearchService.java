package com.horizon.search.service;

import com.alibaba.fastjson.JSON;
import com.horizon.common.enums.ExceptionEnums;
import com.horizon.common.exception.QxException;
import com.horizon.common.utils.JsonUtils;
import com.horizon.common.vo.PageResult;
import com.horizon.item.pojo.*;
import com.horizon.search.client.BrandClient;
import com.horizon.search.client.CategoryClient;
import com.horizon.search.client.GoodsClient;
import com.horizon.search.pojo.Goods;
import com.horizon.search.pojo.SearchRequest;
import com.horizon.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    public Goods buildGoods(Spu spu){

        Long spuId = spu.getId();
        //查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(
                Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3())
        );
        if (CollectionUtils.isEmpty(categories)){
            throw new QxException(ExceptionEnums.CATEGORY_NOT_FOUND);
        }
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());

        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (brand == null){
            throw new QxException(ExceptionEnums.Brand_NOT_FOUND);
        }

        //搜索字段
        String all = spu.getTitle() + StringUtils.join(names,"") + brand.getName();

        //查询Sku
        List<Sku> skuList =  goodsClient.querySkuBySpuId(spu.getId());
        if (CollectionUtils.isEmpty(skuList)){
            throw new QxException(ExceptionEnums.GOODS_SKU_NOT_FOUND);
        }

        //对sku进行处理,提取需要的属性
        List<Map<String,Object>> skus = new ArrayList<>();
        Set<Long> priceSet = new HashSet<>();
        for (Sku sku : skuList){
            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("images",StringUtils.substringBefore(sku.getImages(),","));
            skus.add(map);
            //处理价格
            priceSet.add(sku.getPrice());
        }

//        //查询规格参数
//        //缺少数据库，暂时不搞
//
//        //查询商品详情
//        SpuDetail detail =  goodsClient.queryDetailById(spuId);
//        //获取通用规格参数
//        String json = detail.getGenericSpec();
//        //获取特有规格参数
//        String json1 = detail.getSpecialSpec();
//
//        //规格参数
//        Map<String,Object> specs = new HashMap<>();

        //构建goods对象
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        goods.setAll(all); //搜索字段，包含标题，分类，品牌，规格等
        goods.setPrice(priceSet); //所有sku的价格集合
        goods.setSkus(JSON.toJSONString(skus)); //所有sku的集合的json形式
        //goods.setSpecs(specs); //所有的可搜索的规格参数
        goods.setSubTitle(spu.getSubTitle());

        return goods;
    }

    public PageResult<Goods> search(SearchRequest request) {

        Integer page = request.getPage();
        Integer size = request.getSize();
        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));

        //分页
        queryBuilder.withPageable(PageRequest.of(page,size));

        //过滤
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()));

        //查询
        Page<Goods> result = goodsRepository.search(queryBuilder.build());

        //解析结果
        long totalElements = result.getTotalElements();
        int totalPages = result.getTotalPages();
        List<Goods> goodsList = result.getContent();

        return new PageResult<>(totalElements,totalPages,goodsList);
    }
}
