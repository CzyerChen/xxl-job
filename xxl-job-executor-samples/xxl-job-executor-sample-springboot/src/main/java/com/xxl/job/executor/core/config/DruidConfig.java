/**
 * Author:   claire
 * Date:    2022/3/10 - 5:09 下午
 * Description: druid配置类
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2022/3/10 - 5:09 下午          V1.0.0          druid配置类
 */
package com.xxl.job.executor.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;


/**
 * 功能简述
 * 〈druid配置类〉
 *
 * @author claire
 * @date 2022/3/10 - 5:09 下午
 * @since 1.0.0
 */
@EnableTransactionManagement
@Configuration
public class DruidConfig {
    @Autowired
    private Environment environment;

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druidDataSource(){
        return  new DruidDataSource();
    }


    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("globalConfig")GlobalConfig globalConfig,
                                               @Qualifier("mybatisPlusInterceptor")MybatisPlusInterceptor mybatisPlusInterceptor) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(druidDataSource());

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);
        sqlSessionFactory.setConfiguration(configuration);
        //添加分页/SQL耗时监控功能
        sqlSessionFactory.setGlobalConfig(globalConfig);
        sqlSessionFactory.setPlugins(mybatisPlusInterceptor);
        sqlSessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources(Objects.requireNonNull(environment.getProperty("mybatis-plus.mapper-locations"))));
        return sqlSessionFactory.getObject();
    }


    @Bean(name = "sqlSessionTemplate")
    @ConditionalOnBean(name = "sqlSessionFactory")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 将动态数据源添加到事务管理器中，并生成新的bean
     *
     * @return the platform transaction manager
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(druidDataSource());
    }
}
