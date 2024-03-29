/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import com.huawei.fitframework.annotation.Order;
import com.huawei.fitframework.broker.Genericable;
import com.huawei.fitframework.broker.GenericableRepository;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolvers;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示带优先级的 {@link Genericable} 仓库。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-25
 */
public class PriorityGenericableRepository implements GenericableRepository {
    private final List<GenericableRepository> repositories;

    public PriorityGenericableRepository(List<GenericableRepository> repositories) {
        this.repositories =
                repositories.stream().sorted(Comparator.comparingInt(this::getOrder)).collect(Collectors.toList());
    }

    private int getOrder(GenericableRepository repository) {
        AnnotationMetadataResolver resolver = AnnotationMetadataResolvers.create();
        AnnotationMetadata annotations = resolver.resolve(repository.getClass());
        return Optional.ofNullable(annotations.getAnnotation(Order.class)).map(Order::value).orElse(Order.MEDIUM);
    }

    @Override
    public String name() {
        return "priority";
    }

    @Override
    public Optional<Genericable> get(String id, String version) {
        for (GenericableRepository repository : this.repositories) {
            Optional<Genericable> genericable = repository.get(id, version);
            if (genericable.isPresent()) {
                return genericable;
            }
        }
        return Optional.empty();
    }
}
