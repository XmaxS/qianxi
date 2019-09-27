package com.horizon.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class QxSmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(QxSmsApplication.class);
    }
}
