package com.horizon.search.client;


import com.horizon.item.api.SpecApi;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient("item-service")
public interface SpecClient extends SpecApi {
}
