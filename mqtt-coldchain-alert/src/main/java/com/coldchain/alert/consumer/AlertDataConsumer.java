package com.coldchain.alert.consumer;

import com.alibaba.fastjson2.JSON;
import com.coldchain.alert.entity.AlertRecord;
import com.coldchain.alert.entity.AlertRule;
import com.coldchain.alert.mapper.AlertRecordMapper;
import com.coldchain.alert.mapper.AlertRuleMapper;
import com.coldchain.common.constant.KafkaTopics;
import com.coldchain.common.model.TemperatureData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 告警数据消费者
 */
@Slf4j
@Component
public class AlertDataConsumer {

    @Resource
    private AlertRuleMapper alertRuleMapper;

    @Resource
    private AlertRecordMapper alertRecordMapper;

    /**
     * 消费设备数据，检查告警规则
     */
    @KafkaListener(topics = KafkaTopics.DEVICE_DATA, groupId = "alert-service-group")
    public void checkAlertRules(String message) {
        try {
            log.info("检查告警规则, 消息: {}", message);

            TemperatureData data = JSON.parseObject(message, TemperatureData.class);

            // 只处理温度/湿度数据
            if (!"temperature".equals(data.getType()) && !"humidity".equals(data.getType())) {
                return;
            }

            // 查询所有启用的告警规则
            List<AlertRule> rules = alertRuleMapper.selectEnabledRules();

            for (AlertRule rule : rules) {
                // 检查规则是否匹配
                if (matchRule(rule, data)) {
                    // 创建告警记录
                    createAlertRecord(rule, data);
                }
            }

        } catch (Exception e) {
            log.error("检查告警规则失败", e);
        }
    }

    /**
     * 检查规则是否匹配
     */
    private boolean matchRule(AlertRule rule, TemperatureData data) {
        // 检查公司ID
        if (!rule.getCorpId().equals(data.getCorpId())) {
            return false;
        }

        // 检查车辆ID（如果规则指定了车辆）
        if (rule.getVehicleId() != null) {
            try {
                if (!rule.getVehicleId().toString().equals(data.getVehicleId())) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        // 检查指标类型
        if (!rule.getMetricType().equals(data.getType())) {
            return false;
        }

        // 检查阈值
        BigDecimal value = data.getValue();
        if (rule.getThresholdMin() != null && value.compareTo(rule.getThresholdMin()) < 0) {
            return true;
        }
        if (rule.getThresholdMax() != null && value.compareTo(rule.getThresholdMax()) > 0) {
            return true;
        }

        return false;
    }

    /**
     * 创建告警记录
     */
    private void createAlertRecord(AlertRule rule, TemperatureData data) {
        try {
            AlertRecord record = new AlertRecord();
            record.setAlertRuleId(rule.getId());
            record.setVehicleId(Long.valueOf(data.getVehicleId()));
            record.setDeviceId(data.getDeviceId());
            record.setMetricType(data.getType());
            record.setAlertValue(data.getValue());

            // 构建阈值描述
            String thresholdValue = "";
            if (rule.getThresholdMin() != null) {
                thresholdValue += "最小: " + rule.getThresholdMin();
            }
            if (rule.getThresholdMax() != null) {
                thresholdValue += (thresholdValue.isEmpty() ? "" : ", ") + "最大: " + rule.getThresholdMax();
            }
            record.setThresholdValue(thresholdValue);

            // 计算告警级别（简单实现）
            record.setAlertLevel(1);
            record.setStatus(0);

            alertRecordMapper.insert(record);
            log.warn("触发告警 - 规则: {}, 设备: {}, 值: {}", rule.getRuleName(), data.getDeviceId(), data.getValue());

        } catch (Exception e) {
            log.error("创建告警记录失败", e);
        }
    }
}
