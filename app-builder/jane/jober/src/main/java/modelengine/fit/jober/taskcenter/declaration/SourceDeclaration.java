/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.declaration;

import lombok.Data;
import modelengine.fit.jane.task.util.UndefinableValue;

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
