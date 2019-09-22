package com.horizon.auth.client;

import com.horizon.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(value = "user-service")
public interface UserClient extends UserApi {
}
