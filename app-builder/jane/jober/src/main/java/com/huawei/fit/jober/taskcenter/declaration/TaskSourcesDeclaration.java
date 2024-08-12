/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import lombok.Data;

import java.util.List;

/**
 * 为批量任务数据源提供声明。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
@Data
public class TaskSourcesDeclaration {
    private String taskId;

    private List<SourceDeclaration> sources;
}
