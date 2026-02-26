package com.coldchain.device.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 设备实体
 */
@Data
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 设备唯一标识
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型: sensor温湿度计/gps定位器
     */
    private String deviceType;

    /**
     * 关联车辆ID
     */
    private Long vehicleId;

    /**
     * 公司ID
     */
    private String corpId;

    /**
     * 状态: 0禁用 1正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
