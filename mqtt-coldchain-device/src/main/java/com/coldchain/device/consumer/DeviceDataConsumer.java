package com.coldchain.device.consumer;

import com.alibaba.fastjson2.JSON;
import com.coldchain.common.constant.KafkaTopics;
import com.coldchain.common.model.TemperatureData;
import com.coldchain.device.entity.TemperatureRecord;
import com.coldchain.device.mapper.TemperatureRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 设备数据消费者
 */
@Slf4j
@Component
public class DeviceDataConsumer {

    @Resource
    private TemperatureRecordMapper temperatureRecordMapper;

    /**
     * 消费设备数据
     */
    @KafkaListener(topics = KafkaTopics.DEVICE_DATA, groupId = "device-service-group")
    public void consumeDeviceData(String message) {
        try {
            log.info("收到设备数据: {}", message);

            TemperatureData data = JSON.parseObject(message, TemperatureData.class);
            TemperatureRecord record = new TemperatureRecord();
            record.setDeviceId(data.getDeviceId());
            record.setVehicleId(Long.valueOf(data.getVehicleId()));
            record.setTemperature(data.getValue());
            record.setTimestamp(data.getTimestamp());

            // 位置信息转为 JSON 字符串存储
            if (data.getLocation() != null) {
                record.setLocation(JSON.toJSONString(data.getLocation()));
            }

            temperatureRecordMapper.insert(record);
            log.info("温度数据保存成功: deviceId={}, temperature={}", data.getDeviceId(), data.getValue());

        } catch (Exception e) {
            log.error("处理设备数据失败: {}", message, e);
        }
    }
}
