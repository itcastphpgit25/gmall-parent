package com.atguigu.gmall.ums.config;

import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import io.shardingjdbc.core.jdbc.core.datasource.MasterSlaveDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;

@Configuration
public class GmallShardingJdbcConfig{
    //使用sharing创建出具有主从复制的数据源
    @Bean
    public DataSource dataSource()throws Exception{
        DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(ResourceUtils.getFile("classpath:sharding.yml"));
        return dataSource;
    }
}
