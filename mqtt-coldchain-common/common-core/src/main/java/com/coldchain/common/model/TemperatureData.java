package com.coldchain.common.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 温度数据模型
 */
@Data
public class TemperatureData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 车辆ID
     */
    private String vehicleId;

    /**
     * 公司ID
     */
    private String corpId;

    /**
     * 数据类型: temperature/humidity/location/status
     */
    private String type;

    /**
     * 数值
     */
    private BigDecimal value;

    /**
     * 单位
     */
    private String unit;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 位置信息
     */
    private Location location;

    @Data
    public static class Location implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * 纬度
         */
        private BigDecimal lat;

        /**
         * 经度
         */
        private BigDecimal lng;
    }
}
