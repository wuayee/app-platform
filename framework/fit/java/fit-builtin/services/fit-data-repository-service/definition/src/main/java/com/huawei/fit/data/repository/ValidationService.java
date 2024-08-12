/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.data.repository;

import com.huawei.fit.data.repository.exception.CapacityOverflowException;

/**
 * 缓存数据校验服务。
 *
 * @author 邬涨财
 * @since 2024-01-24
 */
public interface ValidationService {
    /**
     * 判断内存是否可以分配。
     *
     * @param toAllocateMemory 表示需要分配的内存值的 {@code long}。
     * @throws CapacityOverflowException 如果分配该内存值后，内存容量会溢出，则会抛出该异常。
     */
    void validateCapacity(long toAllocateMemory) throws CapacityOverflowException;
}
