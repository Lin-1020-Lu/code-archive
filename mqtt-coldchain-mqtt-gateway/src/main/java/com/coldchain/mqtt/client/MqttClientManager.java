package com.coldchain.mqtt.client;

import com.coldchain.mqtt.config.MqttConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * MQTT 客户端管理器
 */
@Slf4j
@Component
public class MqttClientManager {

    @Resource
    private MqttConfig mqttConfig;

    private IMqttAsyncClient mqttClient;

    /**
     * 初始化 MQTT 客户端
     */
    @PostConstruct
    public void init() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(mqttConfig.getUsername());
            options.setPassword(mqttConfig.getPassword().toCharArray());
            options.setConnectionTimeout(mqttConfig.getConnectionTimeout());
            options.setKeepAliveInterval(mqttConfig.getKeepAliveInterval());
            options.setAutomaticReconnect(true);

            mqttClient = new MqttAsyncClient(
                    mqttConfig.getBroker(),
                    mqttConfig.getClientId(),
                    new MemoryPersistence()
            );

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    log.error("MQTT 连接断开: {}", cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    log.info("收到MQTT消息 - Topic: {}, Payload: {}", topic, new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    try {
                        log.debug("消息发送完成: {}", token.getMessageId());
                    } catch (Exception e) {
                        log.error("获取消息ID失败", e);
                    }
                }
            });

            mqttClient.connect(options).waitForCompletion();
            log.info("MQTT 客户端连接成功: {}", mqttConfig.getBroker());

            // 订阅所有冷链设备数据 topic
            String topicPattern = "coldchain/+/+/+";
            mqttClient.subscribe(topicPattern, mqttConfig.getQos()).waitForCompletion();
            log.info("订阅 Topic 成功: {}", topicPattern);

        } catch (Exception e) {
            log.error("MQTT 客户端初始化失败", e);
            throw new RuntimeException("MQTT 客户端初始化失败", e);
        }
    }

    /**
     * 关闭 MQTT 客户端
     */
    @PreDestroy
    public void destroy() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect().waitForCompletion();
                mqttClient.close();
                log.info("MQTT 客户端已关闭");
            }
        } catch (Exception e) {
            log.error("关闭 MQTT 客户端失败", e);
        }
    }

    /**
     * 获取 MQTT 客户端
     */
    public IMqttAsyncClient getMqttClient() {
        return mqttClient;
    }
}
