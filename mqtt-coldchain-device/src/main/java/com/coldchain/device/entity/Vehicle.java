package com.coldchain.device.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 车辆实体
 */
@Data
public class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 车辆编号
     */
    private String vehicleId;

    /**
     * 车牌号
     */
    private String vehicleName;

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
