/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.filter;

import com.huawei.fit.jane.task.util.UndefinableValue;

import lombok.Data;

import java.util.List;

/**
 * 为任务提供过滤器。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
@Data
public class TaskFilter {
    private UndefinableValue<List<String>> ids = UndefinableValue.undefined();

    private UndefinableValue<List<String>> names = UndefinableValue.undefined();

    private UndefinableValue<List<String>> templateIds = UndefinableValue.undefined();

    private UndefinableValue<List<String>> categories = UndefinableValue.undefined();

    private UndefinableValue<List<String>> creators = UndefinableValue.undefined();

    private UndefinableValue<List<String>> orderBys = UndefinableValue.undefined();
}
