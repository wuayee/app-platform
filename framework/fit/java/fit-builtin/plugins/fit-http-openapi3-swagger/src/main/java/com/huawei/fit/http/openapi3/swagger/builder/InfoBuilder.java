/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.openapi3.swagger.builder;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.openapi3.swagger.EntityBuilder;
import com.huawei.fit.http.openapi3.swagger.entity.Info;
import com.huawei.fitframework.conf.runtime.ApplicationConfig;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;

/**
 * 表示 {@link Info} 的构建器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-08-23
 */
public class InfoBuilder implements EntityBuilder<Info> {
    private final ApplicationConfig applicationConfig;

    InfoBuilder(BeanContainer container) {
        this.applicationConfig =
                notNull(container, "The bean container cannot be null.").lookup(ApplicationConfig.class)
                        .map(BeanFactory::get)
                        .map(ApplicationConfig.class::cast)
                        .orElseThrow(() -> new IllegalStateException("The application config not found."));
    }

    @Override
    public Info build() {
        return Info.custom()
                .title("OpenAPI 3.0 for " + this.applicationConfig.name())
                .summary("该文档由 FIT for Java 进行构建")
                .description("- 默认显示的 `OpenAPI` 文档地址为 `/v3/openapi`，如果需要修改，可以在顶端搜索栏自定义修改。\n"
                        + "- 如果需要去除某一个 `API` 的文档显示，可以在对应的方法上增加 `@DocumentIgnored` 注解。")
                .version("FIT:3.3.11-SNAPSHOT Swagger-UI:v5.17.12")
                .build();
    }
}
