/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler.converter;

import com.huawei.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import com.huawei.fit.jane.task.domain.TaskProperty;

/**
 * Meta属性的转换类
 *
 * @author 陈镕希 c00572808
 * @since 2023-12-26
 */
public interface MetaPropertyConverter {
    TaskProperty.Declaration convert(MetaPropertyDeclarationInfo metaPropertyDeclarationInfo);

    com.huawei.fit.jober.entity.task.TaskProperty convert(TaskProperty taskProperty);
}
