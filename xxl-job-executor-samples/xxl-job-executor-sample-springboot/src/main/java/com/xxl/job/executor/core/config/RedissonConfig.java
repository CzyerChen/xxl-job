/**
 * Author:   claire
 * Date:    2022/3/10 - 5:23 下午
 * Description: redis配置
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2022/3/10 - 5:23 下午          V1.0.0          redis配置
 */
package com.xxl.job.executor.core.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import io.netty.channel.nio.NioEventLoopGroup;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * 功能简述
 * 〈redis配置〉
 *
 * @author claire
 * @date 2022/3/10 - 5:23 下午
 * @since 1.0.0
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonConfig {
    @Autowired
    private RedissonProperties redisProperties;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(redisProperties.getUrl())
                .setDatabase(redisProperties.getDatabase())
                .setClientName(redisProperties.getClientName())
                .setTimeout(redisProperties.getTimeout())
                .setConnectTimeout(redisProperties.getConnectTimeout())
                .setIdleConnectionTimeout(redisProperties.getIdleConnectionTimeout())
                .setPingConnectionInterval(redisProperties.getPingTimeout())
                .setRetryAttempts(redisProperties.getRetryAttempts())
                .setRetryInterval(redisProperties.getRetryInterval())
                .setConnectionMinimumIdleSize(redisProperties.getConnectionMinimumIdleSize())
                .setConnectionPoolSize(redisProperties.getConnectionPoolSize())
                .setDnsMonitoringInterval(redisProperties.getDnsMonitoringInterval())
                .setSubscriptionConnectionMinimumIdleSize(redisProperties.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(redisProperties.getSubscriptionConnectionPoolSize())
                .setSubscriptionsPerConnection(redisProperties.getSubscriptionsPerConnection());
        config.setThreads(redisProperties.getThread());
        config.setEventLoopGroup(new NioEventLoopGroup());
        return Redisson.create(config);
    }

    /**
     * redis connectionFactory 目前是单节点的配置
     *
     * @return
     */
    @Bean(name = "redisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
        lettuceConnectionFactory.setDatabase(redisProperties.getDatabase());
        return lettuceConnectionFactory;
    }

    /**
     * 配置
     *
     * @return
     */
    @Bean(name = "redisCacheConfiguration")
    public RedisCacheConfiguration redisCacheConfiguration() {
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer);
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
        RedisCacheConfiguration redisCacheConfiguration = configuration.entryTtl(Duration.ofHours(1));
        return redisCacheConfiguration;
    }

    /**
     * 缓存管理器
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "cacheManager")
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisCacheWriter, redisCacheConfiguration());
        ParserConfig.getGlobalInstance().addAccept("com.xxl.job.executor");
        return redisCacheManager;
    }



    /**
     * redisTemplate
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);

        ParserConfig.getGlobalInstance().addAccept("com.xxl.job.executor");
        template.setValueSerializer(fastJsonRedisSerializer);
        template.setHashValueSerializer(fastJsonRedisSerializer);

        template.setKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        //开启分布式缓存事务，会存在问题
        template.setEnableTransactionSupport(Boolean.FALSE);
        return template;
    }
}
