/**
 * Author:   claire
 * Date:    2022/3/10 - 5:24 下午
 * Description: redis配置类
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2022/3/10 - 5:24 下午          V1.0.0          redis配置类
 */
package com.xxljob.redis.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;

/**
 * 功能简述
 * 〈redis配置类〉
 *
 * @author claire
 * @date 2022/3/10 - 5:24 下午
 * @since 1.0.0
 */
@EnableConfigurationProperties
@ConfigurationProperties(
        prefix = "spring.redisson"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedissonProperties {
    private int database = 0;
    private String url;
    private String host = "localhost";
    private String username;
    private String password;
    private int port = 6379;
    private boolean ssl;
    private int timeout = 3000;
    private int connectTimeout = 3000;
    private String clientName;

    private int connectionMinimumIdleSize = 10;
    private int idleConnectionTimeout = 10000;
    @Deprecated
    private int pingTimeout = 1000;
    private int retryAttempts = 3;
    private int retryInterval = 1500;
    @Deprecated
    private int reconnectionTimeout = 3000;
    @Deprecated
    private int failedAttempts = 3;
    private int subscriptionsPerConnection = 5;
    private int subscriptionConnectionMinimumIdleSize = 1;
    private int subscriptionConnectionPoolSize = 50;
    private int connectionPoolSize = 64;
    @Deprecated
    private boolean dnsMonitoring = false;
    private int dnsMonitoringInterval = 5000;
    private int thread = 16;
    private String codec = "org.redisson.codec.JsonJacksonCodec";

    private ClientType clientType;
    private Sentinel sentinel;
    private Cluster cluster;
    private final Jedis jedis = new Jedis();

    public static class Jedis {
        private final Pool pool = new Pool();

        public Jedis() {
        }

        public Pool getPool() {
            return this.pool;
        }
    }

    public static class Sentinel {
        private String master;
        private List<String> nodes;
        private String password;

        public Sentinel() {
        }

        public String getMaster() {
            return this.master;
        }

        public void setMaster(String master) {
            this.master = master;
        }

        public List<String> getNodes() {
            return this.nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class Cluster {
        private List<String> nodes;
        private Integer maxRedirects;

        public Cluster() {
        }

        public List<String> getNodes() {
            return this.nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

        public Integer getMaxRedirects() {
            return this.maxRedirects;
        }

        public void setMaxRedirects(Integer maxRedirects) {
            this.maxRedirects = maxRedirects;
        }
    }

    public static class Pool {
        private Boolean enabled;
        private int maxIdle = 8;
        private int minIdle = 0;
        private int maxActive = 8;
        private Duration maxWait = Duration.ofMillis(-1L);
        private Duration timeBetweenEvictionRuns;

        public Pool() {
        }

        public Boolean getEnabled() {
            return this.enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxIdle() {
            return this.maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public int getMinIdle() {
            return this.minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public int getMaxActive() {
            return this.maxActive;
        }

        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
        }

        public Duration getMaxWait() {
            return this.maxWait;
        }

        public void setMaxWait(Duration maxWait) {
            this.maxWait = maxWait;
        }

        public Duration getTimeBetweenEvictionRuns() {
            return this.timeBetweenEvictionRuns;
        }

        public void setTimeBetweenEvictionRuns(Duration timeBetweenEvictionRuns) {
            this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
        }
    }

    public static enum ClientType {
        /**
         * lettuce:
         */
        LETTUCE,
        /**
         * jedisz:
         */
        JEDIS;

        private ClientType() {
        }
    }


    @PostConstruct
    public void fillUrl() {
        setUrl("redis://" + getHost() + ":" + getPort());
    }
}
