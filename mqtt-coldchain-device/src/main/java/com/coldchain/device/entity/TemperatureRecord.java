package com.coldchain.device.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 温度记录实体
 */
@Data
public class TemperatureRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 车辆ID
     */
    private Long vehicleId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 温度值
     */
    private BigDecimal temperature;

    /**
     * 湿度值
     */
    private BigDecimal humidity;

    /**
     * 位置信息(JSON)
     */
    private String location;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
