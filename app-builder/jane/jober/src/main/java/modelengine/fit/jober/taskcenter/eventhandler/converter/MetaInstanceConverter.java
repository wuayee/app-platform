/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.eventhandler.converter;

import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
import modelengine.fit.jober.taskcenter.util.sql.OrderBy;

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
     * 将 {@link modelengine.fit.jane.meta.instance.InstanceDeclarationInfo} 转换为 {@link TaskInstance.Declaration}
     *
     * @param instanceDeclarationInfo 待转换的 {@link modelengine.fit.jane.meta.instance.InstanceDeclarationInfo} 对象
     * @return 转换后的 {@link TaskInstance.Declaration} 对象
     */
    TaskInstance.Declaration convert(
            modelengine.fit.jane.meta.instance.InstanceDeclarationInfo instanceDeclarationInfo);

    /**
     * 将 {@link TaskEntity} 和 {@link TaskInstance} 转换为 {@link modelengine.fit.jane.meta.instance.Instance}
     *
     * @param task 待转换的 {@link TaskEntity} 对象
     * @param instance 待转换的 {@link TaskInstance} 对象
     * @return 转换后的 {@link modelengine.fit.jane.meta.instance.Instance} 对象
     */
    modelengine.fit.jane.meta.instance.Instance convert1(TaskEntity task, TaskInstance instance);

    /**
     * 将 {@link modelengine.fit.jane.meta.instance.MetaInstanceFilter} 转换为 {@link TaskInstance.Filter}
     *
     * @param metaInstanceFilter 待转换的 {@link modelengine.fit.jane.meta.instance.MetaInstanceFilter} 对象
     * @return 转换后的 {@link TaskInstance.Filter} 对象
     */
    TaskInstance.Filter convert(modelengine.fit.jane.meta.instance.MetaInstanceFilter metaInstanceFilter);
}
