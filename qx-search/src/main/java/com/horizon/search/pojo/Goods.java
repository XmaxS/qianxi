package com.horizon.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Map;
import java.util.Set;

@Data
@Document(indexName = "goods", type = "docs", shards = 1, replicas = 1)
public class Goods {

    @Id
    private Long id; //spuId

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String all; //所有需要被搜索的信息，包含标题，分类，甚至品牌

    @Field(type = FieldType.Keyword, index = false)
    private String subtitle; //父标题

    private Long brandId; //品牌Id
    private Long cid1; //一级分类Id
    private Long cid2; //二级分类Id
    private Long cid3; //三级分类Id

    private Date createTime; //spu创建时间
    private Set<Double> price; //价格

    @Field(type = FieldType.Keyword, index = false)
    private String skus; //sku信息的json结构
    private Map<String,Object> specs; //可搜索的规格参数，key是参数名，值是参数值
}
