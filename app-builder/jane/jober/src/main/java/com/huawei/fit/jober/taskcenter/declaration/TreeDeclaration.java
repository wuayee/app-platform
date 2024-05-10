/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import com.huawei.fit.jane.task.util.UndefinableValue;

import lombok.Data;

/**
 * 为任务树提供声明。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-09
 */
@Data
public class TreeDeclaration {
    private UndefinableValue<String> name;

    private UndefinableValue<String> taskId;
}
