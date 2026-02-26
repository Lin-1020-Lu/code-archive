package com.coldchain.alert.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 告警规则实体
 */
@Data
public class AlertRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 公司ID
     */
    private String corpId;

    /**
     * 车辆ID，null表示该公司所有车辆
     */
    private Long vehicleId;

    /**
     * 指标类型: temperature/humidity
     */
    private String metricType;

    /**
     * 最小阈值
     */
    private BigDecimal thresholdMin;

    /**
     * 最大阈值
     */
    private BigDecimal thresholdMax;

    /**
     * 持续多少秒才算告警
     */
    private Integer durationSeconds;

    /**
     * 是否启用: 0禁用 1启用
     */
    private Integer enabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
