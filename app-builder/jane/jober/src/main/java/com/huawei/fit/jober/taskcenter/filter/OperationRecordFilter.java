/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.filter;

import com.huawei.fit.jane.task.util.UndefinableValue;

import lombok.Data;

import java.util.List;

/**
 * 为操作记录提供过滤器。
 *
 * @author 姚江
 * @since 2023-11-17 13:47
 */
@Data
public class OperationRecordFilter {
    private UndefinableValue<List<String>> objectTypes;

    private UndefinableValue<List<String>> objectIds;
}
