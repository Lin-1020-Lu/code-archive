package com.coldchain.user.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据源配置
 *
 * 功能：
 * 1. 创建租户数据源路由器
 * 2. 配置默认数据源
 * 3. 避免循环依赖问题
 *
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    /**
     * 创建默认数据源（HikariCP）
     * 只在不存在其他 DataSource Bean 时才创建，避免重复
     * 注意：如果数据库连接失败，请检查数据库地址、用户名、密码是否正确
     */
    @Bean(name = "defaultDataSource")
    @ConditionalOnMissingBean(name = "defaultDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariDataSource defaultDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://111.231.53.57:3306/coldchain?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true");
        config.setUsername("root");
        config.setPassword("123456");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("ColdchainHikariPool");
        config.setConnectionTestQuery("SELECT 1");
        config.setInitializationFailTimeout(30000);
        return new HikariDataSource(config);
    }

    /**
     * 创建租户数据源路由器（主数据源）
     * 使用 @Bean 方式配置，避免构造函数注入导致的循环依赖
     */
    @Bean
    @Primary
    public TenantDataSourceRouter tenantDataSourceRouter(javax.sql.DataSource defaultDataSource) {
        TenantDataSourceRouter router = new TenantDataSourceRouter();
        router.setDefaultDataSource(defaultDataSource);
        log.info("租户数据源路由器初始化完成");
        return router;
    }
}
