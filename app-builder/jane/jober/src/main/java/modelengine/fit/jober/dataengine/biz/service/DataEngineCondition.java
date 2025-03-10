/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.dataengine.biz.service;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.Condition;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;

/**
 * TimeSchedulerDataEngine生效条件
 *
 * @author 孙怡菲
 * @since 2024-05-29
 */
@Component
public class DataEngineCondition implements Condition {
    /**
     * 检查条件是否匹配。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param annotations 表示需要匹配的元数据定义的 {@link AnnotationMetadata}。
     * @return 表示是否匹配的 {@code boolean}。
     */
    @Override
    public boolean match(BeanContainer container, AnnotationMetadata annotations) {
        return container.plugin().config().get("jane.useDataEngine", boolean.class);
    }
}
