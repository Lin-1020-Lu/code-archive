package com.coldchain.user.controller;

import com.coldchain.common.result.Result;
import com.coldchain.user.dto.TenantRegisterDTO;
import com.coldchain.user.service.TenantService;
import com.coldchain.user.vo.TenantInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 租户管理控制器
 * 
 * 功能：
 * - 租户注册
 * - 租户信息查询
 * - 租户状态管理
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/tenants")
public class TenantController {
    
    @Autowired
    private TenantService tenantService;
    
    /**
     * 租户注册
     * 
     * POST /api/tenants/register
     * 
     * 请求体示例：
     * {
     *   "tenantName": "XX冷链物流",
     *   "tenantType": 2,
     *   "contactName": "张三",
     *   "contactPhone": "13800138000",
     *   "contactEmail": "zhangsan@example.com",
     *   "adminUsername": "admin",
     *   "adminPassword": "12345678",
     *   "adminRealName": "管理员"
     * }
     * 
     * 响应示例：
     * {
     *   "code": 200,
     *   "message": "注册成功",
     *   "data": {
     *     "tenantId": "corp1001",
     *     "tenantName": "XX冷链物流",
     *     "tenantType": 2,
     *     "tenantTypeName": "标准版",
     *     "status": 1,
     *     "statusName": "正常",
     *     "maxUsers": 50,
     *     "currentUsers": 1,
     *     "userUsageRate": 2.0,
     *     "contactName": "张三",
     *     "contactPhone": "13800138000",
     *     "contactEmail": "zhangsan@example.com"
     *   }
     * }
     */
    @PostMapping("/register")
    public Result<TenantInfoVO> register(@Valid @RequestBody TenantRegisterDTO dto) {
        log.info("租户注册: tenantName={}", dto.getTenantName());
        TenantInfoVO vo = tenantService.register(dto);
        return Result.success("注册成功", vo);
    }
    
    /**
     * 查询当前租户信息
     * 
     * GET /api/tenants/current
     * 
     * Header: X-Tenant-Id: corp001
     * Authorization: Bearer {token}
     * 
     * 响应示例：
     * {
     *   "code": 200,
     *   "message": "查询成功",
     *   "data": {
     *     "tenantId": "corp001",
     *     "tenantName": "XX冷链物流",
     *     "tenantType": 2,
     *     "tenantTypeName": "标准版",
     *     "status": 1,
     *     "statusName": "正常",
     *     "expireDate": "2025-01-01 00:00:00",
     *     "isExpired": false,
     *     "maxUsers": 50,
     *     "currentUsers": 5,
     *     "userUsageRate": 10.0,
     *     "contactName": "张三",
     *     "contactPhone": "13800138000",
     *     "contactEmail": "zhangsan@example.com"
     *   }
     * }
     */
    @GetMapping("/current")
    public Result<TenantInfoVO> getCurrentTenant() {
        log.info("查询当前租户信息");
        TenantInfoVO vo = tenantService.getCurrentTenant();
        return Result.success("查询成功", vo);
    }
    
    /**
     * 查询租户信息（根据租户 ID）
     * 
     * GET /api/tenants/{tenantId}
     * 
     * Header: Authorization: Bearer {token}（需要管理员权限）
     */
    @GetMapping("/{tenantId}")
    public Result<TenantInfoVO> getTenantById(@PathVariable String tenantId) {
        log.info("查询租户信息: tenantId={}", tenantId);
        TenantInfoVO vo = tenantService.getTenantByTenantId(tenantId);
        return Result.success("查询成功", vo);
    }
    
    /**
     * 查询所有租户列表（超级管理员）
     * 
     * GET /api/tenants
     * 
     * Header: Authorization: Bearer {token}（需要超级管理员权限）
     * 
     * 参数：
     * - status: 状态（可选）
     * - tenantType: 租户类型（可选）
     */
    @GetMapping
    public Result<List<TenantInfoVO>> list(@RequestParam(required = false) Integer status,
                                          @RequestParam(required = false) Integer tenantType) {
        log.info("查询租户列表: status={}, tenantType={}", status, tenantType);
        List<TenantInfoVO> list = tenantService.list(status, tenantType);
        return Result.success("查询成功", list);
    }
    
    /**
     * 更新租户状态（超级管理员）
     * <p>
     * PUT /api/tenants/{id}/status
     * <p>
     * Header: Authorization: Bearer {token}（需要超级管理员权限）
     * <p>
     * 参数：
     * - status: 状态（0=禁用, 1=正常, 2=过期, 3=审核中, 4=已删除）
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新租户状态: id={}, status={}", id, status);
        tenantService.updateStatus(id, status);
        return Result.success("更新成功");
    }
}
