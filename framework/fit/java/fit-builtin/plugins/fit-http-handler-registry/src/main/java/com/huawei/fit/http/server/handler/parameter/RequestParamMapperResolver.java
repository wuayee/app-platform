/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler.parameter;

import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.server.handler.PropertyValueMapperResolver;
import com.huawei.fit.http.server.handler.Source;
import com.huawei.fit.http.server.handler.SourceFetcher;
import com.huawei.fit.http.server.handler.support.CookieFetcher;
import com.huawei.fit.http.server.handler.support.FormUrlEncodedEntityFetcher;
import com.huawei.fit.http.server.handler.support.HeaderFetcher;
import com.huawei.fit.http.server.handler.support.ObjectEntityFetcher;
import com.huawei.fit.http.server.handler.support.PathVariableFetcher;
import com.huawei.fit.http.server.handler.support.QueryFetcher;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.util.MapBuilder;

import java.util.Map;
import java.util.function.Function;

/**
 * 表示解析带有 {@link RequestParam} 注解的参数的 {@link PropertyValueMapperResolver}。
 *
 * @author 季聿阶 j00559309
 * @since 2022-08-29
 */
public class RequestParamMapperResolver extends AbstractRequestParamMapperResolver {
    private static final Map<Source, Function<String, SourceFetcher>> SOURCE_FETCHER_MAPPING =
            MapBuilder.<Source, Function<String, SourceFetcher>>get()
                    .put(Source.QUERY, QueryFetcher::new)
                    .put(Source.HEADER, HeaderFetcher::new)
                    .put(Source.COOKIE, CookieFetcher::new)
                    .put(Source.PATH, PathVariableFetcher::new)
                    .put(Source.BODY, ObjectEntityFetcher::new)
                    .put(Source.FORM, FormUrlEncodedEntityFetcher::new)
                    .build();

    /**
     * 通过注解解析器来实例化 {@link RequestParamMapperResolver}。
     *
     * @param annotationResolver 表示注解解析器的 {@link AnnotationMetadataResolver}。
     * @throws IllegalArgumentException 当 {@code annotationResolver} 为 {@code null} 时。
     */
    public RequestParamMapperResolver(AnnotationMetadataResolver annotationResolver) {
        super(annotationResolver);
    }

    @Override
    protected SourceFetcher createSourceFetcher(RequestParam requestParam) {
        Function<String, SourceFetcher> function = SOURCE_FETCHER_MAPPING.get(requestParam.in());
        return function.apply(requestParam.name());
    }
}
