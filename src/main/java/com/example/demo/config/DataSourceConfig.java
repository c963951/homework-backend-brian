package com.example.demo.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        // 使用 Spring Boot 提供的 Builder 獲取基本的連線信息
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        // 針對本地開發環境的調優設定
        dataSource.setMaximumPoolSize(10); // 最大連線數 (本地環境 5-10 即可)
        dataSource.setMinimumIdle(2);     // 最小閒置連線數
        dataSource.setConnectionTimeout(20000); // 20秒超時
        dataSource.setIdleTimeout(600000);     // 10分鐘閒置關閉

        return dataSource;
    }
}
