/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.eventhandler.converter;

import modelengine.fit.jane.meta.definition.Meta;
import modelengine.fit.jane.meta.definition.MetaDeclarationInfo;
import modelengine.fit.jane.meta.definition.MetaFilter;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.declaration.TaskDeclaration;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.filter.TaskFilter;

/**
 * Meta的转换类
 *
 * @author 孙怡菲
 * @since 2023-12-12
 */
public interface MetaConverter {
    /**
     * 将MetaDeclarationInfo转换为TaskDeclaration
     *
     * @param metaDeclarationInfo 待转换的MetaDeclarationInfo对象
     * @return 转换后的TaskDeclaration对象
     */
    TaskDeclaration convert(MetaDeclarationInfo metaDeclarationInfo);

    /**
     * 将多版本的MetaDeclarationInfo转换为TaskDeclaration
     *
     * @param metaDeclarationInfo 待转换的多版本MetaDeclarationInfo对象
     * @return 转换后的TaskDeclaration对象
     */
    TaskDeclaration convertMultiVersionDeclaration(
            modelengine.fit.jane.meta.multiversion.definition.MetaDeclarationInfo metaDeclarationInfo);

    /**
     * 将TaskEntity转换为Meta
     *
     * @param task 待转换的TaskEntity对象
     * @param context 操作上下文
     * @return 转换后的Meta对象
     */
    Meta convert(TaskEntity task, OperationContext context);

    /**
     * 将TaskEntity转换为多版本的Meta
     *
     * @param task 待转换的TaskEntity对象
     * @param context 操作上下文
     * @return 转换后的多版本Meta对象
     */
    modelengine.fit.jane.meta.multiversion.definition.Meta convert2MultiVersionMeta(TaskEntity task,
            OperationContext context);

    /**
     * 将MetaFilter转换为TaskFilter
     *
     * @param metaFilter 待转换的MetaFilter对象
     * @return 转换后的TaskFilter对象
     */
    TaskFilter convert(MetaFilter metaFilter);
}
