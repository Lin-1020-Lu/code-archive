package com.coldchain.mqtt.listener;

import com.alibaba.fastjson2.JSON;
import com.coldchain.common.constant.KafkaTopics;
import com.coldchain.common.model.TemperatureData;
import com.coldchain.mqtt.client.MqttClientManager;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * MQTT 消息监听器
 */
@Slf4j
@Component
public class MqttMessageListener {

    @Resource
    private MqttClientManager mqttClientManager;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 订阅 MQTT Topic
     */
    @PostConstruct
    public void subscribeTopics() {
        try {
            // 订阅所有设备数据
            String[] topics = {
                "coldchain/+/+/temperature",
                "coldchain/+/+/humidity",
                "coldchain/+/+/location",
                "coldchain/+/+/status"
            };

            for (String topic : topics) {
                mqttClientManager.getMqttClient().subscribe(topic, 1, new IMqttMessageListener() {
                    @Override
                    public void messageArrived(String topic, MqttMessage message) {
                        handleMqttMessage(topic, message);
                    }
                });
                log.info("订阅 Topic 成功: {}", topic);
            }
        } catch (Exception e) {
            log.error("订阅 MQTT Topic 失败", e);
        }
    }

    /**
     * 处理 MQTT 消息
     */
    private void handleMqttMessage(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            log.info("收到MQTT消息 - Topic: {}, Payload: {}", topic, payload);

            // 解析 Topic 获取 corpId, vehicleId, dataType
            String[] parts = topic.split("/");
            if (parts.length != 4) {
                log.warn("Topic 格式错误: {}", topic);
                return;
            }

            String corpId = parts[1];
            String vehicleId = parts[2];
            String dataType = parts[3];

            // 解析消息内容
            TemperatureData data = JSON.parseObject(payload, TemperatureData.class);
            data.setCorpId(corpId);
            data.setVehicleId(vehicleId);
            data.setType(dataType);

            // 发送到 Kafka
            String messageJson = JSON.toJSONString(data);
            kafkaTemplate.send(KafkaTopics.DEVICE_DATA, messageJson);
            log.info("设备数据已发送到 Kafka: deviceId={}", data.getDeviceId());

        } catch (Exception e) {
            log.error("处理 MQTT 消息失败", e);
        }
    }
}
