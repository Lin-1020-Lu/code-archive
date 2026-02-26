package com.coldchain.device.mapper;

import com.coldchain.device.entity.Vehicle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 车辆 Mapper
 */
@Mapper
public interface VehicleMapper {

    /**
     * 根据车辆ID查询车辆
     */
    Vehicle selectByVehicleId(@Param("vehicleId") String vehicleId);

    /**
     * 插入车辆
     */
    int insert(Vehicle vehicle);

    /**
     * 更新车辆
     */
    int update(Vehicle vehicle);

    /**
     * 根据ID删除车辆
     */
    int deleteById(@Param("id") Long id);
}
