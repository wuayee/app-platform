/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import com.huawei.fit.jane.task.util.UndefinableValue;

import lombok.Data;

/**
 * 表示任务属性触发器的定义。
 *
 * @author 陈镕希
 * @since 2023-08-07
 */
@Data
public class TriggerDeclaration {
    private UndefinableValue<String> propertyName;

    private UndefinableValue<String> fitableId;
}
