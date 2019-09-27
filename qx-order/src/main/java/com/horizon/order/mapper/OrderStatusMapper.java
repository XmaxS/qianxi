package com.horizon.order.mapper;

import com.horizon.order.pojo.OrderStatus;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


@Repository
public interface OrderStatusMapper extends Mapper<OrderStatus> {
}
