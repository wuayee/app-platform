/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import java.util.Map;

/**
 * 分布式Map缓存接口
 *
 * @author l00611472
 * @since 2024/02/06
 */
public interface DistributedMapService {
    /**
     * 获取指定名称的Map缓存。
     *
     * @param mapName 分布式缓存Map名称。
     * @return 指定名称的Map缓存。
     */
    Map<Object, Object> getMapCache(String mapName);

    /**
     * 设置Map缓存失效时间
     *
     * @param mapName 分布式缓存Map名称。
     * @param milliseconds 失效时间。
     * @return true-成功 false-失败
     */
    boolean expire(String mapName, long milliseconds);

    /**
     * 写入缓存。
     *
     * @param mapName 分布式缓存Map名称。
     * @param key 分布式缓存的键。
     * @param value 分布式缓存对应的值。
     */
    void put(String mapName, Object key, Object value);

    /**
     * 获取缓存数据。
     *
     * @param mapName 分布式缓存Map名称。
     * @param key 分布式缓存的键。
     * @return 分布式缓存对应的值。
     */
    Object get(String mapName, Object key);

    /**
     * 移除缓存数据指定key。
     *
     * @param mapName 分布式缓存Map名称。
     * @param key 分布式缓存的键。
     */
    void remove(String mapName, Object key);

    /**
     * 删除分布式缓存Map。
     *
     * @param mapName 分布式缓存Map名称。
     */
    void delete(String mapName);
}
