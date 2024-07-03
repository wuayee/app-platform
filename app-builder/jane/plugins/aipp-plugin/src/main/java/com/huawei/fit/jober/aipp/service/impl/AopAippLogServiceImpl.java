/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jober.aipp.aop.AippLogInsert;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import com.huawei.fit.jober.aipp.mapper.AippLogMapper;
import com.huawei.fit.jober.aipp.service.AopAippLogService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;

/**
 * 用于 aop 的 aipp 实例历史记录服务接口实现
 *
 * @author 邬涨财 w00575064
 * @since 2024-07-03
 */
@Component
public class AopAippLogServiceImpl implements AopAippLogService {
    private static final Logger log = Logger.get(AopAippLogServiceImpl.class);

    private final AippLogMapper aippLogMapper;

    public AopAippLogServiceImpl(AippLogMapper aippLogMapper) {
        this.aippLogMapper = aippLogMapper;
    }

    /**
     * 插入aipp的历史记录
     *
     * @param logDto 插入数据
     * @throws IllegalArgumentException 不合法的参数时抛出。
     */
    @Override
    @AippLogInsert
    public void insertLog(AippLogCreateDto logDto) throws IllegalArgumentException {
        if (logDto.allFieldsNotNull()) {
            aippLogMapper.insertOne(logDto);
            return;
        }
        log.error("null field exists in req {}", logDto);
        // 待各个参数独立校验
        throw new AippParamException(AippErrCode.UNKNOWN);
    }
}
