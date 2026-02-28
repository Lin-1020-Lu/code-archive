package com.coldchain.user.config;

import com.coldchain.user.aspect.TenantSchemaInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 租户 Schema 拦截器配置类
 * 
 * 功能：将 Schema 拦截器注册到所有 SqlSessionFactory 中
 * 
 * 设计要点：
 * 1. 自动扫描项目中的所有 SqlSessionFactory
 * 2. 将拦截器添加到 MyBatis 配置中
 * 3. 支持动态配置拦截器参数
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Configuration
public class TenantSchemaConfig {
    
    /**
     * 所有的 SqlSessionFactory
     * Spring Boot 可能会创建多个（如动态数据源场景）
     */
    @Autowired(required = false)
    private List<SqlSessionFactory> sqlSessionFactoryList;
    
    /**
     * Schema 拦截器
     */
    @Autowired
    private TenantSchemaInterceptor schemaInterceptor;
    
    /**
     * 初始化：将拦截器注册到所有 SqlSessionFactory
     */
    @PostConstruct
    public void addTenantSchemaInterceptor() {
        if (sqlSessionFactoryList == null || sqlSessionFactoryList.isEmpty()) {
            System.out.println("\n" +
                "===============================================\n" +
                "  未检测到 SqlSessionFactory，\n" +
                "  Schema 拦截器未注册\n" +
                "===============================================\n");
            return;
        }
        
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            // 获取 MyBatis 配置
            var configuration = sqlSessionFactory.getConfiguration();
            
            // 添加拦截器
            configuration.addInterceptor(schemaInterceptor);
        }
        
        System.out.println("\n" +
            "===============================================\n" +
            "  多租户 Schema 拦截器已注册\n" +
            "  已注册到 " + sqlSessionFactoryList.size() + " 个 SqlSessionFactory\n" +
            "===============================================\n");
    }
}
