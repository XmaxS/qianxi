package com.horizon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.horizon.user.mapper")
public class QxUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(QxUserApplication.class);
    }
}
