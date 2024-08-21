/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.data.repository.entity;

import modelengine.fitframework.inspection.Validation;

/**
 * 表示缓存数据键的元数据信息。
 *
 * @author 邬涨财
 * @since 2024-01-23
 */
public class CacheKeyMetadata {
    private final String cacheKeyId;
    private final String workerId;

    public CacheKeyMetadata(String cacheKeyId, String workerId) {
        this.cacheKeyId = Validation.notBlank(cacheKeyId, "The cache key id cannot be blank.");
        this.workerId = Validation.notBlank(workerId, "The worker id cannot be blank.");
    }

    /**
     * 获取缓存数据键的唯一标识。
     *
     * @return 表示缓存数据键的唯一标识的 {@link String}。
     */
    public String getCacheKeyId() {
        return this.cacheKeyId;
    }

    /**
     * 获取缓存数据所在的进程的唯一标识。
     *
     * @return 表示缓存数据所在的进程的唯一标识的 {@link String}。
     */
    public String getWorkerId() {
        return this.workerId;
    }
}
