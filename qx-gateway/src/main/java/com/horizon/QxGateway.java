package com.horizon;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringCloudApplication
public class QxGateway {
    public static void main(String[] args) {
        SpringApplication.run(QxGateway.class);
    }
}
