/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler.converter;

import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.util.sql.OrderBy;

import java.util.List;

/**
 * MetaInstance的转换类
 *
 * @author 孙怡菲 s00664640
 * @since 2023-12-12
 */
public interface MetaInstanceConverter {
    TaskInstance.Declaration convert(InstanceDeclarationInfo instanceDeclarationInfo);

    Instance convert(TaskEntity task, TaskInstance instance);

    TaskInstance.Filter convert(MetaInstanceFilter metaInstanceFilter);

    List<OrderBy> convertOrderBys(List<String> orderBys);

    // FIXME: 2024/3/29 0029 以下方法暂用，待删除
    TaskInstance.Declaration convert(com.huawei.fit.jane.meta.instance.InstanceDeclarationInfo instanceDeclarationInfo);

    com.huawei.fit.jane.meta.instance.Instance convert1(TaskEntity task, TaskInstance instance);

    TaskInstance.Filter convert(com.huawei.fit.jane.meta.instance.MetaInstanceFilter metaInstanceFilter);
}
