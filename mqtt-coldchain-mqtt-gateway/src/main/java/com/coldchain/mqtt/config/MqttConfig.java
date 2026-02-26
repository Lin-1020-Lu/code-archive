package com.coldchain.mqtt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT 配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {

    /**
     * MQTT Broker 地址
     */
    private String broker;

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * QoS 等级
     */
    private Integer qos;

    /**
     * 连接超时时间(秒)
     */
    private Integer connectionTimeout;

    /**
     * 心跳间隔(秒)
     */
    private Integer keepAliveInterval;
}
