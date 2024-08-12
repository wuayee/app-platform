/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import com.huawei.fit.jane.task.util.UndefinableValue;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表示任务实例的声明。
 *
 * @author 梁济时
 * @since 2023-08-14
 */
@Data
public class InstanceDeclaration {
    private UndefinableValue<String> typeId;

    private UndefinableValue<String> sourceId;

    private UndefinableValue<Map<String, Object>> info;

    private UndefinableValue<List<String>> tags;
}
