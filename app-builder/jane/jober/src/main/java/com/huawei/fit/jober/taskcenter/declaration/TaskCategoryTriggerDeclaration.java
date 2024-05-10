/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import com.huawei.fit.jane.task.util.UndefinableValue;

import lombok.Data;

import java.util.List;

/**
 * 为任务定义的声明提供任务类目变更触发器的声明。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-23
 */
@Data
public class TaskCategoryTriggerDeclaration {
    private UndefinableValue<String> category;

    private UndefinableValue<List<String>> fitableIds;
}
