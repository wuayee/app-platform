/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Degradation;
import com.huawei.fitframework.broker.ConfigurableFitable;
import com.huawei.fitframework.broker.ConfigurableGenericable;
import com.huawei.fitframework.broker.FitableFactory;
import com.huawei.fitframework.broker.Genericable;
import com.huawei.fitframework.broker.GenericableFactory;
import com.huawei.fitframework.broker.GenericableRepository;
import com.huawei.fitframework.broker.LocalExecutor;
import com.huawei.fitframework.broker.UniqueFitableId;
import com.huawei.fitframework.broker.UniqueGenericableId;
import com.huawei.fitframework.broker.event.LocalExecutorRegisteredObserver;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolvers;
import com.huawei.fitframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示 {@link GenericableRepository} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-08
 */
public class DefaultGenericableRepository implements GenericableRepository, LocalExecutorRegisteredObserver {
    private final String name;
    private final Map<UniqueGenericableId, ConfigurableGenericable> genericables = new ConcurrentHashMap<>();
    private final GenericableFactory genericableFactory;
    private final FitableFactory fitableFactory;

    public DefaultGenericableRepository(String name, GenericableFactory genericableFactory,
            FitableFactory fitableFactory) {
        this.name = notBlank(name, "The genericable repository name cannot be blank.");
        this.genericableFactory = notNull(genericableFactory, "The genericable factory cannot be null.");
        this.fitableFactory = notNull(fitableFactory, "The fitable factory cannot be null.");
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Optional<Genericable> get(String id, String version) {
        notBlank(id, "The genericable id cannot be blank.");
        notBlank(version, "The genericable version cannot be blank.");
        UniqueGenericableId uniqueGenericableId = UniqueGenericableId.create(id, version);
        return Optional.ofNullable(this.genericables.get(uniqueGenericableId));
    }

    @Override
    public void onLocalExecutorRegistered(UniqueFitableId id, LocalExecutor executor) {
        notNull(id, "The unique fitable id cannot be null.");
        notNull(executor, "The local fitable executor cannot be null.");
        UniqueGenericableId uniqueGenericableId =
                UniqueGenericableId.create(id.genericableId(), id.genericableVersion());
        ConfigurableGenericable genericable =
                this.genericables.computeIfAbsent(uniqueGenericableId, key -> this.createGenericable(executor, key));
        ConfigurableFitable fitable =
                this.fitableFactory.create(id.fitableId(), id.fitableVersion()).aliases(executor.aliases());
        fitable.genericable(genericable);
        AnnotationMetadata annotations = AnnotationMetadataResolvers.create().resolve(executor.method());
        if (annotations.isAnnotationPresent(Degradation.class)) {
            fitable.degradationFitableId(annotations.getAnnotation(Degradation.class).to());
        }
        if (executor.isMicro() && executor.metadata().preferred()) {
            genericable.route(fitable.id());
        }
        genericable.appendFitable(fitable);
    }

    private ConfigurableGenericable createGenericable(LocalExecutor executor, UniqueGenericableId id) {
        Method genericableMethod = ReflectionUtils.getInterfaceMethod(executor.method()).orElse(executor.method());
        return this.genericableFactory.create(id)
                .name(ReflectionUtils.toLongString(genericableMethod))
                .method(genericableMethod);
    }
}
