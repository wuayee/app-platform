/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.resolver;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.fitframework.broker.LocalExecutor;
import com.huawei.fitframework.broker.LocalExecutorRepository;
import com.huawei.fitframework.broker.LocalExecutorResolver;
import com.huawei.fitframework.broker.UniqueFitableId;
import com.huawei.fitframework.broker.support.LocalFitableExecutor;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.util.GenericableUtils;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * 表示 {@link LocalExecutorResolver} 的默认解析器。
 * <p>该解析器解析没有 {@link com.huawei.fitframework.annotation.Fitable} 注解的本地方法。</p>
 *
 * @author 季聿阶
 * @since 2022-07-04
 */
public class DefaultFitableResolver implements LocalExecutorResolver {
    private final BeanContainer container;
    private final LocalExecutorRepository.Registry registry;

    public DefaultFitableResolver(BeanContainer container, LocalExecutorRepository.Registry registry) {
        this.container = notNull(container, "Container of a local proxy resolver cannot be null.");
        this.registry = notNull(registry, "The registry to register local proxy cannot be null.");
    }

    @Override
    public boolean resolve(BeanMetadata metadata, Method method) {
        notNull(metadata, "Metadata of bean to resolve local proxy cannot be null.");
        notNull(method, "Method to resolve local proxy cannot be null.");
        if (this.shouldSkip(method)) {
            return false;
        }
        Method interfaceMethod = ReflectionUtils.getInterfaceMethod(method).orElse(null);
        if (interfaceMethod == null) {
            return false;
        }
        AnnotationMetadata annotations = this.container.runtime().resolverOfAnnotations().resolve(interfaceMethod);
        if (annotations.isAnnotationNotPresent(Genericable.class)) {
            return false;
        }
        Genericable annotation = annotations.getAnnotation(Genericable.class);
        String genericableId = getGenericableId(interfaceMethod, annotation);
        String fitableId = metadata.name();
        UniqueFitableId uniqueFitableId = UniqueFitableId.create(genericableId, fitableId);
        LocalExecutor executor =
                new LocalFitableExecutor(uniqueFitableId, true, metadata, this.beanSupplier(metadata.type()), method);
        this.registry.register(uniqueFitableId, executor);
        return true;
    }

    private static String getGenericableId(Method method, Genericable annotation) {
        return StringUtils.isNotBlank(annotation.id())
                ? annotation.id()
                : GenericableUtils.getGenericableId(method.getDeclaringClass(), method.getName(),
                        method.getParameterTypes());
    }

    private boolean shouldSkip(Method method) {
        if (Modifier.isPrivate(method.getModifiers())) {
            // 私有方法无法被外部调用，因此不需要生成代理。
            return true;
        }
        if (method.getName().startsWith("access$")) {
            // 存在内部类时，会自动生成 access$ 前缀的方法，需要跳过。
            return true;
        }
        return Modifier.isStatic(method.getModifiers());
    }

    private Supplier<Object> beanSupplier(Type type) {
        return () -> this.container.beans().get(type);
    }
}
