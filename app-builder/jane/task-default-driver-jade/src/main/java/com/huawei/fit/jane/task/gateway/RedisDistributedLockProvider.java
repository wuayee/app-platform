/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fitframework.annotation.Alias;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.StringUtils;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;

/**
 * 为 {@link DistributedLockProvider} 提供基于 Redis 的实现。
 *
 * @author 梁济时 l00815032
 * @since 2023-11-16
 */
@Component
@Alias("redisDistributedLockProvider")
public class RedisDistributedLockProvider implements DistributedLockProvider {
    private final String redisHost;
    private final String redisPassword;
    private final Integer dataBase;
    private final String redisClusterNodes;
    private final String redisClusterPassword;

    private final LazyLoader<RedissonClient> redis;

    public RedisDistributedLockProvider(
            @Value("${redis.host}") String redisHost,
            @Value("${redis.password}") String redisPassword,
            @Value("${redis.database}") Integer dataBase,
            @Value("${redis.cluster.nodes}") String redisClusterNodes,
            @Value("${redis.cluster.password}") String redisClusterPassword) {
        this.redisHost = redisHost;
        this.redisPassword = redisPassword;
        this.dataBase = dataBase;
        this.redisClusterNodes = redisClusterNodes;
        this.redisClusterPassword = redisClusterPassword;

        this.redis = new LazyLoader<>(this::connectRedis);
    }

    private RedissonClient connectRedis() {
        Config config = new Config();
        if (StringUtils.isBlank(this.redisClusterNodes)) {
            config.useSingleServer()
                    .setAddress("redis://" + this.redisHost + ":6379")
                    .setPassword(this.redisPassword)
                    .setDatabase(this.dataBase);
        } else {
            config.useClusterServers()
                    .addNodeAddress(Arrays.stream(this.redisClusterNodes.split(","))
                            .map(nodeIpInfo -> "redis://" + nodeIpInfo)
                            .toArray(String[]::new))
                    .setPassword(this.redisClusterPassword);
        }
        return Redisson.create(config);
    }

    @Override
    public Lock get(String key) {
        return this.redis.get().getSpinLock(key);
    }
}
