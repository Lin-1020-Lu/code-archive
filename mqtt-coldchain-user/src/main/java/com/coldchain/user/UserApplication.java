package com.coldchain.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户服务启动类
 * 
 * 功能：
 * - 多租户用户管理服务
 * - 用户认证授权
 * - 租户管理
 * 
 * 端口：8084
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"com.coldchain.user", "com.coldchain.common"})
@EnableDiscoveryClient
public class UserApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
        System.out.println("\n" +
            "=======================================================\n" +
            "  多租户用户管理服务启动成功！\n" +
            "  服务地址: http://localhost:8084\n" +
            "  API 文档: http://localhost:8084/swagger-ui.html\n" +
            "=======================================================\n");
    }
}
