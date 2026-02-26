package com.coldchain.common.constant;

/**
 * MQTT Topic 常量
 */
public class MqttTopics {

    /**
     * Topic 前缀
     */
    public static final String TOPIC_PREFIX = "coldchain";

    /**
     * 数据类型：温度
     */
    public static final String DATA_TYPE_TEMPERATURE = "temperature";

    /**
     * 数据类型：湿度
     */
    public static final String DATA_TYPE_HUMIDITY = "humidity";

    /**
     * 数据类型：位置
     */
    public static final String DATA_TYPE_LOCATION = "location";

    /**
     * 数据类型：状态
     */
    public static final String DATA_TYPE_STATUS = "status";

    /**
     * 构建完整 Topic
     * 格式: coldchain/{corpId}/{vehicleId}/{dataType}
     */
    public static String buildTopic(String corpId, String vehicleId, String dataType) {
        return String.format("%s/%s/%s/%s", TOPIC_PREFIX, corpId, vehicleId, dataType);
    }

    /**
     * 解析 Topic
     * 返回数组: [corpId, vehicleId, dataType]
     */
    public static String[] parseTopic(String topic) {
        if (topic == null || !topic.startsWith(TOPIC_PREFIX + "/")) {
            return null;
        }
        String[] parts = topic.split("/");
        if (parts.length != 4) {
            return null;
        }
        return new String[]{parts[1], parts[2], parts[3]};
    }
}
