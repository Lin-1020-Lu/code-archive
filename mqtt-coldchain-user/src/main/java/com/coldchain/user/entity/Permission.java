package com.coldchain.user.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限实体
 * 
 * 功能：存储系统权限的定义和层级关系
 * 
 * 设计要点：
 * 1. 使用树形结构组织权限
 * 2. 支持三级权限：模块 -> 功能 -> 操作
 * 3. 权限代码格式：模块:功能:操作（如 device:view）
 * 4. 支持租户级别的权限定义
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class Permission implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键 ID
     */
    private Long id;
    
    /**
     * 租户 ID（null 表示全局权限）
     */
    private String tenantId;
    
    /**
     * 权限代码（唯一）
     * 
     * 格式：模块:功能:操作
     * 示例：
     * - device:view（查看设备）
     * - device:create（创建设备）
     * - user:view（查看用户）
     * - user:delete（删除用户）
     * - alert:manage（管理告警）
     */
    private String permissionCode;
    
    /**
     * 权限名称
     */
    private String permissionName;
    
    /**
     * 权限类型
     * 1=菜单, 2=按钮, 3=接口
     */
    private Integer permissionType;
    
    /**
     * 父权限 ID
     * 用于构建权限树
     */
    private Long parentId;
    
    /**
     * 层级
     * 1=一级（模块）, 2=二级（功能）, 3=三级（操作）
     */
    private Integer level;
    
    /**
     * 路径
     * 前端路由路径
     */
    private String path;
    
    /**
     * 组件
     * 前端组件路径
     */
    private String component;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 状态
     * 0=禁用, 1=正常
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 子权限列表（运行时组装）
     */
    private java.util.List<Permission> children;
    
    /**
     * 检查是否为一级权限（模块）
     * 
     * @return true 如果是一级权限，false 否则
     */
    public boolean isModule() {
        return Integer.valueOf(1).equals(this.level);
    }
    
    /**
     * 检查是否为二级权限（功能）
     * 
     * @return true 如果是二级权限，false 否则
     */
    public boolean isFunction() {
        return Integer.valueOf(2).equals(this.level);
    }
    
    /**
     * 检查是否为三级权限（操作）
     * 
     * @return true 如果是三级权限，false 否则
     */
    public boolean isOperation() {
        return Integer.valueOf(3).equals(this.level);
    }
}
