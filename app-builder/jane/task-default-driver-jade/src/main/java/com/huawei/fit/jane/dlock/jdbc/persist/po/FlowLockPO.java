/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.dlock.jdbc.persist.po;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 流程实例锁持久化类
 *
 * @author l00862071
 * @since 2023/11/29
 */
@Builder
@Getter
@Setter
public class FlowLockPO {
    private String lockKey;

    private LocalDateTime expiredAt;

    private String lockedClient;
}

