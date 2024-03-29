/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation;

import com.huawei.fitframework.ioc.annotation.support.DefaultAnnotationMetadataResolver;

/**
 * 为 {@link AnnotationMetadataResolver} 提供工具方法。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-25
 */
public final class AnnotationMetadataResolvers {
    private static final AnnotationMetadataResolver INSTANCE = new DefaultAnnotationMetadataResolver();

    private AnnotationMetadataResolvers() {}

    /**
     * 获取一个注解解析器的实例。
     *
     * @return 表示注解解析器的实例的 {@link AnnotationMetadataResolver}。
     */
    public static AnnotationMetadataResolver create() {
        return INSTANCE;
    }
}
