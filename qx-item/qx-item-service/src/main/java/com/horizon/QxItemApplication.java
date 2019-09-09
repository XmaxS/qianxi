package com.horizon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.horizon.item.mapper")
public class QxItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(QxItemApplication.class);
    }
}
