/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.eventhandler.converter;

import modelengine.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import modelengine.fit.jane.task.domain.TaskProperty;

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
     * 将 {@link TaskProperty} 转换为 {@link modelengine.fit.jober.entity.task.TaskProperty}
     *
     * @param taskProperty 待转换的 {@link TaskProperty} 对象
     * @return 转换后的 {@link modelengine.fit.jober.entity.task.TaskProperty} 对象
     */
    modelengine.fit.jober.entity.task.TaskProperty convert(TaskProperty taskProperty);
}
