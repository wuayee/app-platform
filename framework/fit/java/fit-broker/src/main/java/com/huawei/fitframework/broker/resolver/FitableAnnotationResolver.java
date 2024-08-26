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
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * 为解析 {@link Fitable} 注解所定义的服务实现提供代理解析工具。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-09-24
 */
public class FitableAnnotationResolver implements LocalExecutorResolver {
    /**
     * 该模式仅支持数字、大小写字母以及 '-'、'_'、'*'、'.' 字符且长度在128以内。
     */
    private static final Pattern ID_PATTERN = Pattern.compile("^[\\w\\-\\.\\*]{1,128}+$");
    private static final Logger LOG = Logger.get(FitableAnnotationResolver.class);

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
        if (!ID_PATTERN.matcher(opGenericableId.get()).matches() || !ID_PATTERN.matcher(fitableId).matches()) {
            LOG.error("Genericable id or fitable id does not meet the naming requirements. "
                    + "[genericableId={}, fitableId={}]", opGenericableId.get(), fitableId);
            throw new IllegalStateException("Genericable id or fitable id does not meet the naming requirements: "
                    + "only numbers, uppercase and lowercase letters, and '-', '_', '*', '.' are supported, "
                    + "and the length is less than 128.");
        }
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
