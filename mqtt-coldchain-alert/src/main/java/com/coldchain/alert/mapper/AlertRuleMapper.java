package com.coldchain.alert.mapper;

import com.coldchain.alert.entity.AlertRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 告警规则 Mapper
 */
@Mapper
public interface AlertRuleMapper {

    /**
     * 根据ID查询告警规则
     */
    AlertRule selectById(@Param("id") Long id);

    /**
     * 查询所有启用的告警规则
     */
    List<AlertRule> selectEnabledRules();

    /**
     * 插入告警规则
     */
    int insert(AlertRule rule);

    /**
     * 更新告警规则
     */
    int update(AlertRule rule);

    /**
     * 删除告警规则
     */
    int deleteById(@Param("id") Long id);
}
