/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplate;

import lombok.Data;

import java.util.List;

/**
 * 为批量属性提供声明。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-08
 */
@Data
public class TaskPropertiesDeclaration {
    private String taskId;

    private List<TaskProperty.Declaration> properties;

    private TaskTemplate template;
}
