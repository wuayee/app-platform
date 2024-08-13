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
 * @author 孙怡菲
 * @since 2023-12-12
 */
public interface MetaInstanceConverter {
    /**
     * 将 {@link InstanceDeclarationInfo} 转换为 {@link TaskInstance.Declaration}
     *
     * @param instanceDeclarationInfo 待转换的 {@link InstanceDeclarationInfo} 对象
     * @return 转换后的 {@link TaskInstance.Declaration} 对象
     */
    TaskInstance.Declaration convert(InstanceDeclarationInfo instanceDeclarationInfo);

    /**
     * 将 {@link TaskEntity} 和 {@link TaskEntity} 转换为 {@link Instance}T
     *
     * @param task 待转换的 {@link TaskEntity} 对象
     * @param instance 待转换的 {@link TaskInstance} 对象
     * @return 转换后的 {@link Instance} 对象
     */
    Instance convert(TaskEntity task, TaskInstance instance);

    /**
     * 将 {@link MetaInstanceFilter} 转换为 {@link TaskInstance.Filter}
     *
     * @param metaInstanceFilter 待转换的 {@link MetaInstanceFilter} 对象
     * @return 转换后的 {@link TaskInstance.Filter} 对象
     */
    TaskInstance.Filter convert(MetaInstanceFilter metaInstanceFilter);

    /**
     * 将 {@link List<String>} 转换为 {@link List<OrderBy>}
     *
     * @param orderBys 待转换的 {@link List<String>} 对象
     * @return 转换后的 {@link List<OrderBy>} 对象
     */
    List<OrderBy> convertOrderBys(List<String> orderBys);

    // 以下方法暂用，待删除
    /**
     * 将 {@link com.huawei.fit.jane.meta.instance.InstanceDeclarationInfo} 转换为 {@link TaskInstance.Declaration}
     *
     * @param instanceDeclarationInfo 待转换的 {@link com.huawei.fit.jane.meta.instance.InstanceDeclarationInfo} 对象
     * @return 转换后的 {@link TaskInstance.Declaration} 对象
     */
    TaskInstance.Declaration convert(com.huawei.fit.jane.meta.instance.InstanceDeclarationInfo instanceDeclarationInfo);

    /**
     * 将 {@link TaskEntity} 和 {@link TaskInstance} 转换为 {@link com.huawei.fit.jane.meta.instance.Instance}
     *
     * @param task 待转换的 {@link TaskEntity} 对象
     * @param instance 待转换的 {@link TaskInstance} 对象
     * @return 转换后的 {@link com.huawei.fit.jane.meta.instance.Instance} 对象
     */
    com.huawei.fit.jane.meta.instance.Instance convert1(TaskEntity task, TaskInstance instance);

    /**
     * 将 {@link com.huawei.fit.jane.meta.instance.MetaInstanceFilter} 转换为 {@link TaskInstance.Filter}
     *
     * @param metaInstanceFilter 待转换的 {@link com.huawei.fit.jane.meta.instance.MetaInstanceFilter} 对象
     * @return 转换后的 {@link TaskInstance.Filter} 对象
     */
    TaskInstance.Filter convert(com.huawei.fit.jane.meta.instance.MetaInstanceFilter metaInstanceFilter);
}
