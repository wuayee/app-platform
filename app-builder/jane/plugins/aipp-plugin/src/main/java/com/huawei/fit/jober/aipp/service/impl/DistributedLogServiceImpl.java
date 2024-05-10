/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.service.DistributedMapService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.StringUtils;

import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * log分布式缓存接口实现
 *
 * @author l00611472
 * @since 2024/02/06
 */
@Component
public class DistributedLogServiceImpl implements DistributedMapService {
    private final String redisHost;
    private final String redisPassword;
    private final Integer dataBase;
    private final String redisClusterNodes;
    private final String redisClusterPassword;
    private final LazyLoader<RedissonClient> redis;

    public DistributedLogServiceImpl(@Value("${redis.host}") String redisHost,
            @Value("${redis.password}") String redisPassword, @Value("${redis.database}") Integer dataBase,
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

    /**
     * 获取指定名称的Map缓存。
     *
     * @param mapName 分布式缓存Map名称。
     * @return 指定名称的Map缓存。
     */
    @Override
    public Map<Object, Object> getMapCache(String mapName) {
        String redisMapName = Utils.getAippLogRedisMapName(mapName);
        RMap<Object, Object> rmap = redis.get().getMap(redisMapName);
        if (!rmap.isExists()) {
            return Collections.emptyMap();
        }
        return rmap.readAllMap();
    }

    /**
     * 设置Map缓存失效时间
     *
     * @param mapName 分布式缓存Map名称。
     * @param milliseconds 失效时间。
     * @return true-成功 false-失败
     */
    @Override
    public boolean expire(String mapName, long milliseconds) {
        String redisMapName = Utils.getAippLogRedisMapName(mapName);
        return redis.get().getMap(redisMapName).expire(Instant.now().plusMillis(milliseconds));
    }

    /**
     * 写入缓存。
     *
     * @param mapName 分布式缓存Map名称。
     * @param key 分布式缓存的键。
     * @param value 分布式缓存对应的值。
     */
    @Override
    public void put(String mapName, Object key, Object value) {
        String redisMapName = Utils.getAippLogRedisMapName(mapName);
        redis.get().getMap(redisMapName).fastPut(key, value);
    }

    /**
     * 获取缓存数据。
     *
     * @param mapName 分布式缓存Map名称。
     * @param key 分布式缓存的键。
     * @return 分布式缓存对应的值。
     */
    @Override
    public Object get(String mapName, Object key) {
        String redisMapName = Utils.getAippLogRedisMapName(mapName);
        RMap<Object, Object> rmap = redis.get().getMap(redisMapName);
        if (!rmap.isExists() || !rmap.containsKey(key)) {
            return null;
        }
        return rmap.get(key);
    }

    /**
     * 移除缓存数据指定key。
     *
     * @param mapName 分布式缓存Map名称。
     * @param key 分布式缓存的键。
     */
    @Override
    public void remove(String mapName, Object key) {
        String redisMapName = Utils.getAippLogRedisMapName(mapName);
        RMap<Object, Object> rmap = redis.get().getMap(redisMapName);
        if (!rmap.isExists()) {
            return;
        }
        rmap.fastRemove(key);
    }

    /**
     * 删除分布式缓存Map。
     *
     * @param mapName 分布式缓存Map名称。
     */
    @Override
    public void delete(String mapName) {
        String redisMapName = Utils.getAippLogRedisMapName(mapName);
        RMap<Object, Object> rmap = redis.get().getMap(redisMapName);
        if (!rmap.isExists()) {
            return;
        }
        rmap.delete();
    }
}
