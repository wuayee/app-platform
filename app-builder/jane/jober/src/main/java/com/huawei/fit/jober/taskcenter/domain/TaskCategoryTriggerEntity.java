/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import lombok.Data;

import java.util.List;

/**
 * 表示任务的类目变更触发器。
 *
 * @author 梁济时
 * @since 2023-08-23
 */
@Data
public class TaskCategoryTriggerEntity {
    private String category;

    private List<String> fitableIds;
}
