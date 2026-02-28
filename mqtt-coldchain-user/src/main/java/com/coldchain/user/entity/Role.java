package com.coldchain.user.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色实体
 * 
 * 功能：存储角色信息和权限配置
 * 
 * 设计要点：
 * 1. role_code 在租户内唯一
 * 2. permissions 使用 JSON 格式存储权限列表
 * 3. 支持租户级别的角色定义
 * 4. 预置角色：admin（管理员）、user（普通用户）
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class Role implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键 ID
     */
    private Long id;
    
    /**
     * 租户 ID
     */
    private String tenantId;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 角色代码
     * 
     * 约束：
     * - 租户内唯一
     * - 预置角色：admin, user
     * - 只能包含字母、数字、下划线
     */
    private String roleCode;
    
    /**
     * 权限列表（JSON 格式）
     * 
     * 示例：
     * ["*:*"]  // 所有权限（超级管理员）
     * ["user:create", "user:view", "user:delete"]  // 用户管理权限
     * ["device:view", "temperature:view"]  // 只读权限
     * 
     * 权限格式：模块:操作
     * - 模块：user, device, vehicle, alert, tenant 等
     * - 操作：create, view, update, delete, export, import 等
     */
    private String permissions;
    
    /**
     * 权限列表解析后的对象（运行时使用）
     */
    private java.util.List<String> permissionList;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 是否预置角色
     * 0=自定义, 1=预置（不可删除）
     */
    private Integer isBuiltIn;
    
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
     * 创建者 ID
     */
    private Long createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新者 ID
     */
    private Long updatedBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 检查角色是否拥有指定权限
     * 
     * @param permission 权限代码
     * @return true 如果拥有该权限，false 否则
     */
    public boolean hasPermission(String permission) {
        // 拥有超级权限
        if (permissionList != null && permissionList.contains("*:*")) {
            return true;
        }
        // 检查具体权限
        return permissionList != null && permissionList.contains(permission);
    }
    
    /**
     * 检查角色是否拥有模块的所有权限
     * 
     * @param module 模块名称
     * @return true 如果拥有该模块的所有权限，false 否则
     */
    public boolean hasModulePermission(String module) {
        if (permissionList == null) {
            return false;
        }
        // 检查模块权限
        return permissionList.contains(module + ":*");
    }
    
    /**
     * 检查是否为预置角色
     * 
     * @return true 如果是预置角色，false 否则
     */
    public boolean isBuiltIn() {
        return Integer.valueOf(1).equals(this.isBuiltIn);
    }
    
    /**
     * 检查是否为管理员角色
     * 
     * @return true 如果是管理员角色，false 否则
     */
    public boolean isAdmin() {
        return "admin".equals(this.roleCode);
    }
}
