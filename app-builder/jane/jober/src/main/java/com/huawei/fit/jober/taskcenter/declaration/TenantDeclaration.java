/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.declaration;

import com.huawei.fit.jane.task.util.UndefinableValue;

import lombok.Data;

import java.util.List;

/**
 * 表示租户的声明。
 *
 * @author 陈镕希 c00572808
 * @since 2023-09-28
 */
@Data
public class TenantDeclaration {
    private UndefinableValue<String> name;

    private UndefinableValue<String> description;

    private UndefinableValue<String> avatarId;

    private UndefinableValue<List<String>> tags;
}
