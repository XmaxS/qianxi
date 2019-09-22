package com.horizon.order.mapper;

import com.horizon.order.pojo.PayLog;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author bystander
 * @date 2018/10/5
 */
@Repository
public interface PayLogMapper extends Mapper<PayLog> {
}
