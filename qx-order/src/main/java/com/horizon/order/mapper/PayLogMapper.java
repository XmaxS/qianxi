package com.horizon.order.mapper;

import com.horizon.order.pojo.PayLog;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


@Repository
public interface PayLogMapper extends Mapper<PayLog> {
}
