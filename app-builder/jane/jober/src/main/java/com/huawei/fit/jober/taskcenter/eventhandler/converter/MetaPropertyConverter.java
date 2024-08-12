/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler.converter;

import com.huawei.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import com.huawei.fit.jane.task.domain.TaskProperty;

/**
 * Meta属性的转换类
 *
 * @author 陈镕希
 * @since 2023-12-26
 */
public interface MetaPropertyConverter {
    /**
     * 将 {@link MetaPropertyDeclarationInfo} 转换为 {@link TaskProperty.Declaration}
     *
     * @param metaPropertyDeclarationInfo 待转换的 {@link MetaPropertyDeclarationInfo} 对象
     * @return 转换后的 {@link TaskProperty.Declaration} 对象
     */
    TaskProperty.Declaration convert(MetaPropertyDeclarationInfo metaPropertyDeclarationInfo);

    /**
     * 将 {@link TaskProperty} 转换为 {@link com.huawei.fit.jober.entity.task.TaskProperty}
     *
     * @param taskProperty 待转换的 {@link TaskProperty} 对象
     * @return 转换后的 {@link com.huawei.fit.jober.entity.task.TaskProperty} 对象
     */
    com.huawei.fit.jober.entity.task.TaskProperty convert(TaskProperty taskProperty);
}
