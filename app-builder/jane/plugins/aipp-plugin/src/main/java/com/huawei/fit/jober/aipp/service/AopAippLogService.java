/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jober.aipp.dto.aipplog.AippLogCreateDto;

/**
 * 用于 aop 的 aipp 实例历史记录服务接口
 *
 * @author 邬涨财 w00575064
 * @since 2024-07-03
 */
public interface AopAippLogService {
    /**
     * 插入aipp的历史记录
     *
     * @param logDto 插入数据
     * @throws IllegalArgumentException 不合法的参数时抛出。
     */
    void insertLog(AippLogCreateDto logDto) throws IllegalArgumentException;
}
