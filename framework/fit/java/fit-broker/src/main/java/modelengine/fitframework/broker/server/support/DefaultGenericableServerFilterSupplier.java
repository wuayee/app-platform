/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.broker.server.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.server.GenericableServerFilter;
import modelengine.fitframework.broker.server.GenericableServerFilterSupplier;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示获取默认的 {@link GenericableServerFilter} 实例列表的提供器。
 *
 * @author 李金绪
 * @since 2024-08-26
 */
public class DefaultGenericableServerFilterSupplier implements GenericableServerFilterSupplier {
    @Override
    public List<GenericableServerFilter> get(BeanContainer container) {
        return notNull(container, "The bean container cannot be null.").factories(GenericableServerFilter.class)
                .stream()
                .map(BeanFactory::<GenericableServerFilter>get)
                .collect(Collectors.toList());
    }
}