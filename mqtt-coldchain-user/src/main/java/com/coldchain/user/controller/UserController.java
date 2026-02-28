package com.coldchain.user.controller;

import com.coldchain.common.result.Result;
import com.coldchain.user.dto.UserRegisterDTO;
import com.coldchain.user.dto.UserUpdateDTO;
import com.coldchain.user.service.UserService;
import com.coldchain.user.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户管理控制器
 * 
 * 功能：
 * - 用户 CRUD 操作
 * - 用户状态管理
 * - 密码管理
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 查询用户列表（当前租户）
     * 
     * GET /api/users
     * 
     * Header: X-Tenant-Id: corp001
     * Authorization: Bearer {token}
     * 
     * 响应示例：
     * {
     *   "code": 200,
     *   "message": "查询成功",
     *   "data": [
     *     {
     *       "id": 1,
     *       "username": "admin",
     *       "realName": "管理员",
     *       "email": "admin@example.com",
     *       "phone": "13800138000",
     *       "roleCode": "admin",
     *       "roleName": "租户管理员",
     *       "isAdmin": true,
     *       "status": 1,
     *       "createdAt": "2024-01-01 00:00:00"
     *     }
     *   ]
     * }
     */
    @GetMapping
    public Result<List<UserInfoVO>> list(@RequestParam(required = false) String username,
                                         @RequestParam(required = false) Integer status) {
        log.info("查询用户列表: username={}, status={}", username, status);
        List<UserInfoVO> list = userService.list(username, status);
        return Result.success("查询成功", list);
    }
    
    /**
     * 查询用户详情
     * 
     * GET /api/users/{id}
     * 
     * Header: X-Tenant-Id: corp001
     * Authorization: Bearer {token}
     * 
     * 响应示例：
     * {
     *   "code": 200,
     *   "message": "查询成功",
     *   "data": {
     *     "id": 1,
     *     "username": "admin",
     *     "realName": "管理员",
     *     "email": "admin@example.com",
     *     "phone": "13800138000",
     *     "roleCode": "admin",
     *     "roleName": "租户管理员",
     *     "isAdmin": true,
     *     "status": 1,
     *     "createdAt": "2024-01-01 00:00:00"
     *   }
     * }
     */
    @GetMapping("/{id}")
    public Result<UserInfoVO> getById(@PathVariable Long id) {
        log.info("查询用户详情: id={}", id);
        UserInfoVO vo = userService.getById(id);
        return Result.success("查询成功", vo);
    }
    
    /**
     * 创建用户（租户管理员操作）
     * 
     * POST /api/users/register
     * 
     * Header: X-Tenant-Id: corp001
     * Authorization: Bearer {token}
     * 
     * 请求体示例：
     * {
     *   "username": "zhangsan",
     *   "password": "12345678",
     *   "confirmPassword": "12345678",
     *   "realName": "张三",
     *   "email": "zhangsan@example.com",
     *   "phone": "13800138001",
     *   "roleId": 2
     * }
     * 
     * 响应示例：
     * {
     *   "code": 200,
     *   "message": "创建成功",
     *   "data": {
     *     "id": 2,
     *     "username": "zhangsan",
     *     "realName": "张三",
     *     "email": "zhangsan@example.com",
     *     "phone": "13800138001",
     *     "roleCode": "user",
     *     "roleName": "普通用户",
     *     "isAdmin": false,
     *   }
     * }
     */
    @PostMapping("/register")
    public Result<UserInfoVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        log.info("创建用户: username={}", dto.getUsername());
        UserInfoVO vo = userService.register(dto);
        return Result.success("创建成功", vo);
    }
    
    /**
     * 更新用户信息
     * <p>
     * PUT /api/users/{id}
     * <p>
     * Header: X-Tenant-Id: corp001
     * Authorization: Bearer {token}
     * <p>
     * 请求体示例：
     * {
     * "realName": "张三",
     * "email": "newemail@example.com",
     * "phone": "13800138001",
     * "roleId": 2,
     * "status": 1
     * }
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        log.info("更新用户信息: id={}", id);
        userService.update(id, dto);
        return Result.success("更新成功");
    }
    
    /**
     * 删除用户
     * 
     * DELETE /api/users/{id}
     * 
     * Header: X-Tenant-Id: corp001
     * Authorization: Bearer {token}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除用户: id={}", id);
        userService.delete(id);
        return Result.success("删除成功");
    }
    
    /**
     * 更新用户状态
     * 
     * PUT /api/users/{id}/status
     * 
     * Header: X-Tenant-Id: corp001
     * Authorization: Bearer {token}
     * 
     * 参数：
     * - status: 状态（0=禁用, 1=正常, 2=锁定）
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新用户状态: id={}, status={}", id, status);
        userService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }
    
    /**
     * 修改密码
     * 
     * PUT /api/users/{id}/password
     * 
     * Header: X-Tenant-Id: corp001
     * Authorization: Bearer {token}
     * 
     * 请求体示例：
     * {
     *   "oldPassword": "12345678",
     *   "newPassword": "87654321"
     * }
     */
    @PutMapping("/{id}/password")
    public Result<String> changePassword(@PathVariable Long id,
                                      @RequestParam String oldPassword,
                                      @RequestParam String newPassword) {
        log.info("修改用户密码: id={}", id);
        userService.changePassword(id, oldPassword, newPassword);
        return Result.success("密码修改成功");
    }
    
    /**
     * 重置密码（管理员操作）
     * 
     * PUT /api/users/{id}/reset-password
     * 
     * Header: X-Tenant-Id: corp001
     * Authorization: Bearer {token}
     * 
     * 请求体示例：
     * {
     *   "newPassword": "12345678"
     * }
     */
    @PutMapping("/{id}/reset-password")
    public Result<String> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        log.info("重置用户密码: id={}", id);
        userService.resetPassword(id, newPassword);
        return Result.success("密码重置成功");
    }
}
