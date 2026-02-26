package com.coldchain.device.mapper;

import com.coldchain.device.entity.TemperatureRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 温度记录 Mapper
 */
@Mapper
public interface TemperatureRecordMapper {

    /**
     * 插入温度记录
     */
    int insert(TemperatureRecord record);
}
