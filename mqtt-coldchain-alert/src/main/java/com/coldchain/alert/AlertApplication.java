package com.coldchain.alert;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 告警服务启动类
 */
@EnableDiscoveryClient
@MapperScan("com.coldchain.alert.mapper")
@SpringBootApplication
public class AlertApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertApplication.class, args);
    }
}
