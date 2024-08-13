/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.data.repository;

import com.huawei.fit.data.repository.exception.CapacityOverflowException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.StringUtils;

/**
 * 缓存数据校验服务的默认实现。
 *
 * @author 邬涨财
 * @author 季聿阶
 * @since 2024-01-24
 */
@Component
public class DefaultValidationService implements ValidationService {
    @Override
    public void validateCapacity(long toAllocateMemory) throws CapacityOverflowException {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        if (toAllocateMemory > freeMemory) {
            String message = StringUtils.format("No enough remaining space for data caching. "
                            + "[maxMem={0}, usedMem={1}, freeMem={2}, toAllocatedMem={3}]",
                    totalMemory,
                    usedMemory,
                    freeMemory,
                    toAllocateMemory);
            throw new CapacityOverflowException(message, totalMemory, usedMemory, freeMemory, toAllocateMemory);
        }
    }
}
