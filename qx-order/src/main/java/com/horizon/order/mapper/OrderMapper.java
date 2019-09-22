package com.horizon.order.mapper;

import com.horizon.order.pojo.Order;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author bystander
 * @date 2018/10/4
 */
@Repository
public interface OrderMapper extends Mapper<Order> {
}
