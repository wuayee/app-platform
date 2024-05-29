/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.biz.service;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.Condition;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;

/**
 * TimeSchedulerDataEngine生效条件
 *
 * @author 孙怡菲 s00664640
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
