/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

/**
 * 表示任务数据源的类型。
 *
 * @author 陈镕希
 * @since 2023-08-07
 */
public enum SourceType {
    /**
     * 表示三方系统推送。
     */
    THIRD_PARTY_PUSH,

    /**
     * 表示定时任务获取。
     */
    SCHEDULE,

    /**
     * 表示调用刷新。
     */
    REFRESH_IN_TIME,
}
