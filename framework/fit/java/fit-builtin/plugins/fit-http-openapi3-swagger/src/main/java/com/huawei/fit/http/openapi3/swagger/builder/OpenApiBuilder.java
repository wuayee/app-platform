/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.openapi3.swagger.builder;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.openapi3.swagger.EntityBuilder;
import com.huawei.fit.http.openapi3.swagger.entity.OpenApi;
import com.huawei.fitframework.ioc.BeanContainer;

/**
 * 表示 {@link OpenApi} 的构建器。
 *
 * @author 季聿阶
 * @since 2023-08-23
 */
public class OpenApiBuilder implements EntityBuilder<OpenApi> {
    private final InfoBuilder infoBuilder;
    private final TagsBuilder tagsBuilder;
    private final ComponentsBuilder componentsBuilder;
    private final PathsBuilder pathsBuilder;

    public OpenApiBuilder(BeanContainer container) {
        notNull(container, "The bean container cannot be null.");
        this.infoBuilder = new InfoBuilder(container);
        this.tagsBuilder = new TagsBuilder(container);
        this.componentsBuilder = new ComponentsBuilder(container);
        this.pathsBuilder = new PathsBuilder(container);
    }

    @Override
    public OpenApi build() {
        OpenApi.Builder documentBuilder = OpenApi.custom().openapi("3.1.0");
        documentBuilder.info(this.infoBuilder.build());
        documentBuilder.tags(this.tagsBuilder.build());
        documentBuilder.components(this.componentsBuilder.build());
        documentBuilder.paths(this.pathsBuilder.build());
        return documentBuilder.build();
    }
}
