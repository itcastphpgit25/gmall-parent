package com.atguigu.gmall.pms.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;

@Configuration
public class GmallShardingJdbcConfig {
    //使用sharing创建出具有主从复制的数据源
    @Bean
    public DataSource dataSource()throws Exception{
        DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(ResourceUtils.getFile("classpath:sharding.yml"));
        return dataSource;
    }

    /**
     *
     * 分页插件
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){

        return new PaginationInterceptor();
    }
}
