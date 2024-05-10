/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 表示通过定时任务同步数据的数据源。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleSourceEntity extends SourceEntity {
    private String fitableId;

    private int interval;

    private Map<String, Object> filter;
}

