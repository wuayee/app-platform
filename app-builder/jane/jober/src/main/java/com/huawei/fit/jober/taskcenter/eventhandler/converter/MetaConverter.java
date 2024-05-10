/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler.converter;

import com.huawei.fit.jane.meta.definition.Meta;
import com.huawei.fit.jane.meta.definition.MetaDeclarationInfo;
import com.huawei.fit.jane.meta.definition.MetaFilter;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.declaration.TaskDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.filter.TaskFilter;

/**
 * Meta的转换类
 *
 * @author 孙怡菲 s00664640
 * @since 2023-12-12
 */
public interface MetaConverter {
    TaskDeclaration convert(MetaDeclarationInfo metaDeclarationInfo);

    TaskDeclaration convertMultiVersionDeclaration(
            com.huawei.fit.jane.meta.multiversion.definition.MetaDeclarationInfo metaDeclarationInfo);

    Meta convert(TaskEntity task, OperationContext context);

    com.huawei.fit.jane.meta.multiversion.definition.Meta convert2MultiVersionMeta(TaskEntity task,
            OperationContext context);

    TaskFilter convert(MetaFilter metaFilter);
}
