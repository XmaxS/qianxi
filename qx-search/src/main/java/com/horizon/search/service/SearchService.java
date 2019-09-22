package com.horizon.search.service;

import com.alibaba.fastjson.JSON;
import com.horizon.common.enums.ExceptionEnums;
import com.horizon.common.exception.QxException;
import com.horizon.common.vo.PageResult;
import com.horizon.item.pojo.*;
import com.horizon.search.client.BrandClient;
import com.horizon.search.client.CategoryClient;
import com.horizon.search.client.GoodsClient;
import com.horizon.search.pojo.Goods;
import com.horizon.search.pojo.SearchRequest;
import com.horizon.search.pojo.SearchResult;
import com.horizon.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
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

    @Autowired
    private ElasticsearchTemplate template;

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

        String key = request.getKey();

        Integer page = request.getPage();
        Integer size = request.getSize();
        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));

        //分页
        queryBuilder.withPageable(PageRequest.of(page,size));

        //过滤
        QueryBuilder basicQuery = buildBasicQuery(request);
        queryBuilder.withQuery(basicQuery);

        //聚合分类和品牌
        //先聚合分类
        String categoryName = "category_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryName).field("cid3"));
        //再聚合品牌
        String brandName = "brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandName).field("brandId"));

        //查询
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(),Goods.class);

        //解析结果
        //分页结果
        long totalElements = result.getTotalElements();
        int totalPages = result.getTotalPages();
        List<Goods> goodsList = result.getContent();
        //聚合结果
        Aggregations aggregations = result.getAggregations();
        List<Category> categories = parseCategoryAgg(aggregations.get(categoryName));
        List<Brand> brands = parseBrandAgg(aggregations.get(brandName));

//        //规格参数聚合
//        List<Map<String,Object>> specs = null;
//        if (categories != null && categories.size() ==1){
//            //商品分类存在并且数量为1，才可以聚合规格参数
//            specs = buildSpecificationAgg(categories.get(0).getId(),basicQuery);
//        }

        return new SearchResult(totalElements,totalPages,goodsList,categories,brands,null);
    }

    private QueryBuilder buildBasicQuery(SearchRequest request) {

        //创建布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()));
        //过滤条件
        Map<String,String> map = request.getFilter();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            //处理key
            if (!"cid3".equals(key) && !"brandId".equals(key)){
                key = "specs." + key + ".keyword";
            }
            String value = entry.getValue();
            queryBuilder.filter(QueryBuilders.termQuery(key,value));
        }

        return queryBuilder;

    }

//    private List<Map<String, Object>> buildSpecificationAgg(Long id, MatchQueryBuilder basicQuery) {
//        List<Map<String,Object>> specs = new ArrayList<>();
//        //缺少数据库，不做
//
//        //查询需要聚合的规格参数
//        List<SpecParam> params = specClient.queryParamList(null,,cid,true);
//
//        //完成聚合
//        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        //带上查询条件
//        queryBuilder.withQuery(basicQuery);
//        //聚合
//        for (SpecParam param : params){
//            String name = param.getName();
//            queryBuilder.addAggregation(
//                    AggregationBuilders.terms(name).field("specs."+name+".keyword")
//            );
//        }
//
//        //获取结果
//        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
//
//        //解析结果
//        Aggregations aggregations = result.getAggregations();
//        for (SpecParam param : params){
//            String name = param.getName();
//            StringTerms terms = aggregations.get(name);
//            List<String> options = terms.getBuckets()
//                    .stream().map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList());
//            //准备map
//            Map<String,Object> map = new HashMap<>();
//            map.put("k",name);
//            map.put("options",options);
//        }
//
//        return specs;
//    }

    private List<Brand> parseBrandAgg(LongTerms terms) {

        try {
            List<Long> ids = terms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Brand> brands = brandClient.queryBrandByIds(ids);
            return brands;
        }catch (Exception e){
            return null;
        }


    }

    private List<Category> parseCategoryAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Category>  categories = categoryClient.queryCategoryByIds(ids);
            return categories;
        }catch (Exception e){
            return null;
        }
    }

    public void createOrUpdateIndex(Long spuId) {

        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);

        //构建goods对象
        Goods goods = buildGoods(spu);

        //存入索引库
        goodsRepository.save(goods);
    }

    public void deleteIndex(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
