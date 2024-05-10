/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import lombok.Data;

import java.util.List;

/**
 * 为批量任务属性触发器提供声明。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-08
 */
@Data
public class SourceTriggersDeclaration {
    private String sourceId;

    private List<TriggerDeclaration> triggers;
}
