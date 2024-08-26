/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.http.server.handler;

import modelengine.fitframework.ioc.BeanContainer;

/**
 * 表示 {@link PropertyValueMapperResolver} 的提供者。
 *
 * @author 季聿阶
 * @since 2023-01-11
 */
@FunctionalInterface
public interface PropertyValueMapperResolverSupplier {
    /**
     * 从指定容器中获取属性值的解析器。
     *
     * @param container 表示指定容器的 {@link BeanContainer}。
     * @return 表示获取到的参数的解析器的 {@link PropertyValueMapperResolver}。
     */
    PropertyValueMapperResolver get(BeanContainer container);
}
