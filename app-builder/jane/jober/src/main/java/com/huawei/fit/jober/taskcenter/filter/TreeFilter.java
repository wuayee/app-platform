/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.filter;

import com.huawei.fit.jane.task.util.UndefinableValue;

import lombok.Data;

import java.util.List;

/**
 * 为任务树提供过滤器。
 *
 * @author 梁济时
 * @since 2023-08-09
 */
@Data
public class TreeFilter {
    private UndefinableValue<List<String>> ids;

    private UndefinableValue<List<String>> names;

    private UndefinableValue<List<String>> taskIds;
}
