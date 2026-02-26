package com.coldchain.common.enums;

import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),

    // 设备相关
    DEVICE_NOT_FOUND(1001, "设备不存在"),
    DEVICE_OFFLINE(1002, "设备离线"),
    DEVICE_ALREADY_EXISTS(1003, "设备已存在"),

    // 告警相关
    ALERT_RULE_NOT_FOUND(2001, "告警规则不存在"),
    ALERT_RECORD_NOT_FOUND(2002, "告警记录不存在"),

    // 数据相关
    DATA_PARSE_ERROR(3001, "数据解析失败"),
    DATA_INVALID(3002, "数据格式无效");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
