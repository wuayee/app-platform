/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.domain.Index;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表示任务的声明。
 *
 * @author 陈镕希
 * @since 2023-08-07
 */
@Data
public class TaskDeclaration {
    private UndefinableValue<String> name = UndefinableValue.undefined();

    private UndefinableValue<String> category = UndefinableValue.undefined();

    private UndefinableValue<String> templateId = UndefinableValue.undefined();

    private UndefinableValue<Map<String, Object>> attributes = UndefinableValue.undefined();

    private UndefinableValue<List<TaskProperty.Declaration>> properties = UndefinableValue.undefined();

    private UndefinableValue<List<TaskCategoryTriggerDeclaration>> categoryTriggers = UndefinableValue.undefined();

    private UndefinableValue<List<Index.Declaration>> indexes = UndefinableValue.undefined();
}
