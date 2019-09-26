package com.horizon.service;

import com.horizon.client.BrandClient;
import com.horizon.client.CategoryClient;
import com.horizon.client.GoodsClient;
import com.horizon.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private TemplateEngine templateEngine;

    public Map<String, Object> loadModel(Long spuId) {
        Map<String, Object> model = new HashMap<>();

        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //查询skus
        List<Sku> skus = spu.getSkus();
        //查询详情
        SpuDetail detail = spu.getSpuDetail();
        //查询brand
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //查询商品分类
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));

        model.put("title",spu.getTitle());
        model.put("subTitle",spu.getSubTitle());
        model.put("skus",skus);
        model.put("detail",detail);
        model.put("brand",brand);
        model.put("categories",categories);

        return model;
    }

    //静态化
    public void createHtml(Long spuId){
        //上下文
        Context context = new Context();
        context.setVariables(loadModel(spuId));

        //输出流
        File dest = new File("磁盘路径", spuId + ".html");

        if (dest.exists()){
            dest.delete();
        }

        //该流可以自动释放
        try (PrintWriter writer = new PrintWriter(dest,"UTF-8")){
            //生成html
            templateEngine.process("item",context,writer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteHtml(Long spuId) {

        File dest = new File("磁盘路径", spuId + ".html");

        if(dest.exists()){
            dest.delete();
        }
    }
}
