/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.filter;

import com.huawei.fit.jane.task.util.UndefinableValue;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 为任务实例提供过滤器。
 *
 * @author 梁济时
 * @since 2023-08-14
 */
@Data
public class InstanceFilter {
    private UndefinableValue<List<String>> ids;

    private UndefinableValue<List<String>> typeIds;

    private UndefinableValue<List<String>> sourceIds;

    private UndefinableValue<List<String>> tags;

    private UndefinableValue<List<String>> categories;

    private UndefinableValue<Map<String, List<String>>> infos;

    private UndefinableValue<List<String>> orderBy;
}
