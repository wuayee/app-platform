/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.parameter;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.RequestBean;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestCookie;
import com.huawei.fit.http.annotation.RequestForm;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.annotation.RequestQuery;
import com.huawei.fit.http.server.handler.PropertyValueMapper;
import com.huawei.fit.http.server.handler.PropertyValueMetadataResolver;
import com.huawei.fit.http.server.handler.Source;
import com.huawei.fit.http.server.handler.SourceFetcher;
import com.huawei.fit.http.server.handler.support.CookieFetcher;
import com.huawei.fit.http.server.handler.support.FormUrlEncodedEntityFetcher;
import com.huawei.fit.http.server.handler.support.HeaderFetcher;
import com.huawei.fit.http.server.handler.support.MultiSourcePropertyValueMapper;
import com.huawei.fit.http.server.handler.support.ObjectEntityFetcher;
import com.huawei.fit.http.server.handler.support.PathVariableFetcher;
import com.huawei.fit.http.server.handler.support.QueryFetcher;
import com.huawei.fit.http.server.handler.support.SourceFetcherInfo;
import com.huawei.fit.http.server.handler.support.TypeTransformationPropertyValueMapper;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.value.PropertyValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * 表示解析带有 {@link RequestBean} 注解的参数的 {@link PropertyValueMetadataResolver}。
 *
 * @author 邬涨财 w00575064
 * @since 2023-11-14
 */
public class RequestBeanMapperResolver extends AbstractPropertyValueMapperResolver {
    /**
     * <p>考虑到 PathVariable 等请求映射参数注解带有 RequestParam 注解，所以在初始化 IS_ARRAY_MAPPING 时，需要将 RequestParam
     * 注解的 put 操作放置在最后；否则其他参数注解可能不会正确的从 IS_ARRAY_MAPPING 取出。</p>
     */
    private static final Map<Class<? extends Annotation>, Function<PropertyValue, Boolean>> IS_ARRAY_MAPPING =
            MapBuilder.<Class<? extends Annotation>, Function<PropertyValue, Boolean>>get(LinkedHashMap::new)
                    .put(PathVariable.class, (propertyValue -> false))
                    .put(RequestBody.class, (propertyValue -> false))
                    .put(RequestCookie.class, (propertyValue -> false))
                    .put(RequestForm.class, (propertyValue -> List.class.isAssignableFrom(propertyValue.getType())))
                    .put(RequestQuery.class, (propertyValue -> List.class.isAssignableFrom(propertyValue.getType())))
                    .put(RequestParam.class, (propertyValue -> List.class.isAssignableFrom(propertyValue.getType())))
                    .build();
    private static final Map<Source, Function<String, SourceFetcher>> SOURCE_FETCHER_MAPPING =
            MapBuilder.<Source, Function<String, SourceFetcher>>get()
                    .put(Source.QUERY, QueryFetcher::new)
                    .put(Source.HEADER, HeaderFetcher::new)
                    .put(Source.COOKIE, CookieFetcher::new)
                    .put(Source.PATH, PathVariableFetcher::new)
                    .put(Source.BODY, ObjectEntityFetcher::new)
                    .put(Source.FORM, FormUrlEncodedEntityFetcher::new)
                    .build();
    private static final String DESTINATION_NAME_SEPARATOR = ".";

    private final AnnotationMetadataResolver annotationResolver;

    /**
     * 通过注解解析器来实例化 {@link RequestBeanMapperResolver}。
     *
     * @param annotationResolver 表示注解解析器的 {@link AnnotationMetadataResolver}。
     * @throws IllegalArgumentException 当 {@code annotationResolver} 为 {@code null} 时。
     */
    public RequestBeanMapperResolver(AnnotationMetadataResolver annotationResolver) {
        super(annotationResolver);
        this.annotationResolver = notNull(annotationResolver, "The annotation metadata resolver cannot be null.");
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return RequestBean.class;
    }

    @Override
    protected Optional<PropertyValueMapper> resolve(PropertyValue propertyValue,
            AnnotationMetadata annotationMetadata) {
        List<SourceFetcherInfo> propertyValueMappers = this.getSourceFetcherInfos(propertyValue, StringUtils.EMPTY);
        return Optional.of(new TypeTransformationPropertyValueMapper(new MultiSourcePropertyValueMapper(
                propertyValueMappers), propertyValue.getType(), false, null));
    }

    private List<SourceFetcherInfo> getSourceFetcherInfos(PropertyValue propertyValue, String destinationName) {
        List<SourceFetcherInfo> sourceFetcherInfos = new ArrayList<>();
        for (Field field : ReflectionUtils.getDeclaredFields(propertyValue.getType())) {
            PropertyValue fieldPropertyValue = PropertyValue.createFieldValue(field);
            String fieldPath = destinationName + DESTINATION_NAME_SEPARATOR + fieldPropertyValue.getName();
            AnnotationMetadata annotationMetadata = this.annotationResolver.resolve(fieldPropertyValue.getElement());
            if (annotationMetadata.isAnnotationPresent(this.getAnnotation())) {
                sourceFetcherInfos.addAll(this.getSourceFetcherInfos(fieldPropertyValue, fieldPath));
                continue;
            }
            if (!annotationMetadata.isAnnotationPresent(RequestParam.class)) {
                continue;
            }
            RequestParam annotation = annotationMetadata.getAnnotation(RequestParam.class);
            SourceFetcher sourceFetcher = this.getSourceFetcher(annotation);
            sourceFetcherInfos.add(new SourceFetcherInfo(sourceFetcher,
                    fieldPath.substring(DESTINATION_NAME_SEPARATOR.length()),
                    this.isArray(fieldPropertyValue, annotationMetadata)));
        }
        return sourceFetcherInfos;
    }

    private Boolean isArray(PropertyValue propertyValue, AnnotationMetadata annotationMetadata) {
        return IS_ARRAY_MAPPING.entrySet()
                .stream()
                .filter(entry -> annotationMetadata.isAnnotationPresent(entry.getKey()))
                .findFirst()
                .map(entry -> entry.getValue().apply(propertyValue))
                .orElseThrow(() -> new IllegalStateException("Failed to judge whether property value is array."));
    }

    private SourceFetcher getSourceFetcher(RequestParam requestParam) {
        Function<String, SourceFetcher> function = SOURCE_FETCHER_MAPPING.get(requestParam.in());
        return function.apply(requestParam.name());
    }
}
