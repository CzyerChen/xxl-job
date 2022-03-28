/**
 * Author:   claire
 * Date:    2022/3/25 - 5:05 下午
 * Description:
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2022/3/25 - 5:05 下午          V1.0.0
 */
package com.xxl.job.executor.core.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 功能简述 
 * 〈〉
 *
 * @author claire
 * @date 2022/3/25 - 5:05 下午
 * @since 1.0.0
 */
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mybatisPlusInterceptor;
    }

    @Bean
    public GlobalConfig globalConfig() {
        return new GlobalConfig();
    }
}
