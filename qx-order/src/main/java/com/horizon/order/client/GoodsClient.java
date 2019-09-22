package com.horizon.order.client;

import com.horizon.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author bystander
 * @date 2018/10/4
 */
@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {
}
