package com.coldchain.alert.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 告警记录实体
 */
@Data
public class AlertRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 告警规则ID
     */
    private Long alertRuleId;

    /**
     * 车辆ID
     */
    private Long vehicleId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 指标类型
     */
    private String metricType;

    /**
     * 告警值
     */
    private BigDecimal alertValue;

    /**
     * 阈值
     */
    private String thresholdValue;

    /**
     * 告警级别: 1警告 2严重 3紧急
     */
    private Integer alertLevel;

    /**
     * 状态: 0待处理 1已确认 2已恢复
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 恢复时间
     */
    private LocalDateTime resolvedAt;
}
