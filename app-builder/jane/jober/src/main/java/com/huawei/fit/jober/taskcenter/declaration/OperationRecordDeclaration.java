/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import com.huawei.fit.jane.task.util.UndefinableValue;

import lombok.Data;

/**
 * 操作记录的声明
 *
 * @author 姚江
 * @since 2023-11-17 14:16
 */
@Data
public class OperationRecordDeclaration {
    private UndefinableValue<String> objectType;

    private UndefinableValue<String> objectId;

    private UndefinableValue<String> message;

    private UndefinableValue<String> operate;
}
