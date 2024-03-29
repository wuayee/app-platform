/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler.parameter;

import com.huawei.fit.http.annotation.RequestForm;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.server.handler.PropertyValueMapperResolver;
import com.huawei.fit.http.server.handler.SourceFetcher;
import com.huawei.fit.http.server.handler.support.FormUrlEncodedEntityFetcher;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;

import java.lang.annotation.Annotation;

/**
 * 表示解析带有 {@link RequestForm} 注解的参数的 {@link PropertyValueMapperResolver}。
 *
 * @author 邬涨财 w00575064
 * @since 2023-11-20
 */
public class RequestFormMapperResolver extends AbstractRequestParamMapperResolver {
    /**
     * 通过注解解析器来实例化 {@link RequestFormMapperResolver}。
     *
     * @param annotationResolver 表示注解解析器的 {@link AnnotationMetadataResolver}。
     * @throws IllegalArgumentException 当 {@code annotationResolver} 为 {@code null} 时。
     */
    public RequestFormMapperResolver(AnnotationMetadataResolver annotationResolver) {
        super(annotationResolver);
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return RequestForm.class;
    }

    @Override
    protected SourceFetcher createSourceFetcher(RequestParam requestParam) {
        return new FormUrlEncodedEntityFetcher(requestParam.name());
    }
}
