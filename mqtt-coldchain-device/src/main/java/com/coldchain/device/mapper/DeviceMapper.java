package com.coldchain.device.mapper;

import com.coldchain.device.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 设备 Mapper
 */
@Mapper
public interface DeviceMapper {

    /**
     * 根据设备ID查询设备
     */
    Device selectByDeviceId(@Param("deviceId") String deviceId);

    /**
     * 插入设备
     */
    int insert(Device device);

    /**
     * 更新设备
     */
    int update(Device device);

    /**
     * 根据ID删除设备
     */
    int deleteById(@Param("id") Long id);
}
