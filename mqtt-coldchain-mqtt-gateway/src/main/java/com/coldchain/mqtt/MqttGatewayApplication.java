package com.coldchain.mqtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * MQTT 网关服务启动类
 */
@EnableDiscoveryClient
@SpringBootApplication
public class MqttGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttGatewayApplication.class, args);
    }
}
