package com.coldchain.alert.mapper;

import com.coldchain.alert.entity.AlertRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 告警记录 Mapper
 */
@Mapper
public interface AlertRecordMapper {

    /**
     * 插入告警记录
     */
    int insert(AlertRecord record);

    /**
     * 根据ID更新告警状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
