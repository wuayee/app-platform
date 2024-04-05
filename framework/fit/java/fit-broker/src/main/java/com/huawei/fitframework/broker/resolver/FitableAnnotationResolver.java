/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.resolver;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.broker.LocalExecutor;
import com.huawei.fitframework.broker.LocalExecutorRepository;
import com.huawei.fitframework.broker.LocalExecutorResolver;
import com.huawei.fitframework.broker.UniqueFitableId;
import com.huawei.fitframework.broker.support.LocalFitableExecutor;
import com.huawei.fitframework.broker.util.AnnotationUtils;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 为解析 {@link Fitable} 注解所定义的服务实现提供代理解析工具。
 *
 * @author 梁济时 l00815032
 * @author 季聿阶 j00559309
 * @since 2020-09-24
 */
public class FitableAnnotationResolver implements LocalExecutorResolver {
    private final BeanContainer container;
    private final LocalExecutorRepository.Registry registry;

    public FitableAnnotationResolver(BeanContainer container, LocalExecutorRepository.Registry registry) {
        this.container = notNull(container, "Container of a local proxy resolver cannot be null.");
        this.registry = notNull(registry, "The registry to register local proxy cannot be null.");
    }

    @Override
    public boolean resolve(BeanMetadata metadata, Method method) {
        notNull(metadata, "Metadata of bean to resolve local proxy cannot be null.");
        notNull(method, "Method to resolve local proxy cannot be null.");
        AnnotationMetadata annotations = metadata.runtime().resolverOfAnnotations().resolve(method);
        Fitable annotation = annotations.getAnnotation(Fitable.class);
        if (annotation == null || StringUtils.isBlank(annotation.id())) {
            return false;
        }
        Optional<String> opGenericableId = this.resolveGenericableId(method, annotation);
        if (!opGenericableId.isPresent()) {
            return false;
        }
        String fitableId = annotation.id();
        Supplier<Object> beanSupplier = () -> this.container.beans().get(metadata.type());
        UniqueFitableId uniqueFitableId = UniqueFitableId.create(opGenericableId.get(), fitableId);
        LocalExecutor executor = new LocalFitableExecutor(uniqueFitableId, false, metadata, beanSupplier, method);
        this.registry.register(uniqueFitableId, executor);
        return true;
    }

    private Optional<String> resolveGenericableId(Method method, Fitable annotation) {
        if (StringUtils.isNotBlank(annotation.genericable())) {
            return Optional.of(annotation.genericable());
        }
        return ReflectionUtils.getInterfaceMethod(method).flatMap(AnnotationUtils::getGenericableId);
    }
}
