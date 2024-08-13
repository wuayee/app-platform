/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.broker.ConfigurableGenericable;
import com.huawei.fitframework.broker.DynamicRouter;
import com.huawei.fitframework.broker.GenericableFactory;
import com.huawei.fitframework.broker.UniqueGenericableId;

/**
 * 表示 {@link GenericableFactory} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-03-26
 */
public class DefaultGenericableFactory implements GenericableFactory {
    private final DynamicRouter dynamicRouter;

    public DefaultGenericableFactory(DynamicRouter dynamicRouter) {
        this.dynamicRouter = notNull(dynamicRouter, "The dynamic router cannot be null.");
    }

    @Override
    public ConfigurableGenericable create(String id, String version) {
        return new DefaultGenericable(this.dynamicRouter, id, version);
    }

    @Override
    public ConfigurableGenericable create(UniqueGenericableId id) {
        return this.create(id.genericableId(), id.genericableVersion());
    }
}
