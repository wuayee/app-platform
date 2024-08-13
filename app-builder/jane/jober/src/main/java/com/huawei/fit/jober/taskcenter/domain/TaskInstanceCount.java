/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import lombok.Data;

/**
 * 表示任务实例的数量。
 *
 * @author 梁济时
 * @since 2023-08-25
 */
@Data
public class TaskInstanceCount {
    private String taskId;

    private String taskName;

    private Long count;
}
