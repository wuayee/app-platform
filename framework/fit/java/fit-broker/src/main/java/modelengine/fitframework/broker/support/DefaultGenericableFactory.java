/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.broker.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.ConfigurableGenericable;
import modelengine.fitframework.broker.DynamicRouter;
import modelengine.fitframework.broker.GenericableFactory;
import modelengine.fitframework.broker.UniqueGenericableId;

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
