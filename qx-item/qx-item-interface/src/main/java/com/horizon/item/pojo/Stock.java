package com.horizon.item.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_stock")
@Data
public class Stock {

    @Id
    private Long skuId;
    private Integer seckillStock; //秒杀可用库存
    private Integer seckillTotal; //以秒杀数量
    private Integer stock; //正常库存
}
