/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.model;

import lombok.Data;

/**
 * 日志信息
 *
 * @author 董建华
 * @since 2023-09-10
 */
@Data
public class LogSubject {
    /**
     * 操作时间
     */
    private String startTime;

    /**
     * 消耗时间,单位为秒
     */
    private double spentSeconds;

    /**
     * 方法完整名称:类路径+方法名称
     */
    private String methodFullName;
}
