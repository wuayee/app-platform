/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.filter;

import com.huawei.fit.jane.task.util.UndefinableValue;

import lombok.Data;

import java.util.List;

/**
 * 为任务模板提供过滤器
 *
 * @author 姚江
 * @since 2023-12-04
 */
@Data
public class TaskTemplateFilter {
    private UndefinableValue<List<String>> ids = UndefinableValue.undefined();

    private UndefinableValue<List<String>> names = UndefinableValue.undefined();
}
