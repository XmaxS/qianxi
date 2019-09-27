package com.horizon.order.mapper;

import com.horizon.order.pojo.Order;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


@Repository
public interface OrderMapper extends Mapper<Order> {
}
