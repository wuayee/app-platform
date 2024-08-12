/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import com.huawei.fit.jane.task.util.UndefinableValue;

import lombok.Data;

import java.util.List;

/**
 * 为任务树的节点提供声明。
 *
 * @author 梁济时
 * @since 2023-08-09
 */
@Data
public class NodeDeclaration {
    private UndefinableValue<String> name;

    private UndefinableValue<String> parentId;

    private UndefinableValue<List<String>> sourceIds;
}
