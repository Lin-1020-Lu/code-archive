package com.coldchain.user.service;

import com.coldchain.user.dto.UserRegisterDTO;
import com.coldchain.user.dto.UserUpdateDTO;
import com.coldchain.user.entity.User;
import com.coldchain.user.vo.UserInfoVO;

import java.util.List;

/**
 * 用户服务接口
 * 
 * 功能：
 * - 用户 CRUD 操作
 * - 用户认证
 * - 用户状态管理
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
public interface UserService {
    
    /**
     * 根据用户 ID 查询用户
     * 
     * @param id 用户 ID
     * @return 用户信息 VO
     */
    UserInfoVO getById(Long id);
    
    /**
     * 根据用户名查询用户（自动过滤租户）
     * 
     * @param username 用户名
     * @return 用户实体
     */
    User getByUsername(String username);
    
    /**
     * 创建用户
     * 
     * @param dto 用户注册 DTO
     * @return 用户信息 VO
     */
    UserInfoVO register(UserRegisterDTO dto);
    
    /**
     * 更新用户信息
     * 
     * @param id 用户 ID
     * @param dto 用户更新 DTO
     */
    void update(Long id, UserUpdateDTO dto);
    
    /**
     * 删除用户
     * 
     * @param id 用户 ID
     */
    void delete(Long id);
    
    /**
     * 查询用户列表（自动过滤租户）
     * 
     * @param username 用户名（模糊查询，可选）
     * @param status 状态（可选）
     * @return 用户列表
     */
    List<UserInfoVO> list(String username, Integer status);
    
    /**
     * 修改密码
     * 
     * @param userId 用户 ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码（管理员操作）
     * 
     * @param userId 用户 ID
     * @param newPassword 新密码
     */
    void resetPassword(Long userId, String newPassword);
    
    /**
     * 启用/禁用用户
     * 
     * @param userId 用户 ID
     * @param status 状态
     */
    void updateStatus(Long userId, Integer status);
    
    /**
     * 更新最后登录信息
     * 
     * @param userId 用户 ID
     * @param ip IP 地址
     */
    void updateLastLoginInfo(Long userId, String ip);
    
    /**
     * 增加登录失败次数
     * 
     * @param userId 用户 ID
     */
    void increaseLoginFailCount(Long userId);
    
    /**
     * 重置登录失败次数
     * 
     * @param userId 用户 ID
     */
    void resetLoginFailCount(Long userId);
}
