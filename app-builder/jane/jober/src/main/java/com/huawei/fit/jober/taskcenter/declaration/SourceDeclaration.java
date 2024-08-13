/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import com.huawei.fit.jane.task.util.UndefinableValue;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表示任务数据源的声明。
 *
 * @author 陈镕希
 * @since 2023-08-07
 */
@Data
public class SourceDeclaration {
    // 兼容逻辑，待删除，直接使用任务类型的名称。
    private UndefinableValue<String> name;

    private UndefinableValue<String> app;

    private UndefinableValue<String> type;

    private UndefinableValue<List<TriggerDeclaration>> triggers;

    private UndefinableValue<String> fitableId;

    private UndefinableValue<Integer> interval;

    private UndefinableValue<Map<String, Object>> filter;

    private UndefinableValue<List<InstanceEventDeclaration>> events;

    // Refresh In Time
    private UndefinableValue<Map<String, Object>> metadata;

    private UndefinableValue<String> createFitableId;

    private UndefinableValue<String> patchFitableId;

    private UndefinableValue<String> deleteFitableId;

    private UndefinableValue<String> retrieveFitableId;

    private UndefinableValue<String> listFitableId;
}
