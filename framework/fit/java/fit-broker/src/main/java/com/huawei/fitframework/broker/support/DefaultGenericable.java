/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;

import com.huawei.fitframework.broker.ConfigurableGenericable;
import com.huawei.fitframework.broker.DynamicRouter;
import com.huawei.fitframework.broker.Fitable;
import com.huawei.fitframework.broker.Genericable;
import com.huawei.fitframework.broker.GenericableExecutor;
import com.huawei.fitframework.broker.GenericableMethod;
import com.huawei.fitframework.broker.GenericableType;
import com.huawei.fitframework.broker.InvocationContext;
import com.huawei.fitframework.broker.Route;
import com.huawei.fitframework.broker.Tags;
import com.huawei.fitframework.broker.UniqueFitableId;
import com.huawei.fitframework.broker.UniqueGenericableId;
import com.huawei.fitframework.broker.client.FitableNotFoundException;
import com.huawei.fitframework.broker.client.TooManyFitablesException;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 表示 {@link Genericable} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-10
 */
public class DefaultGenericable implements ConfigurableGenericable {
    private final DynamicRouter dynamicRouter;
    private final GenericableExecutor retryableExecutor;
    private final GenericableExecutor degradableExecutor;

    private final String id;
    private final String version;
    private String name;
    private GenericableType type;
    private final ConfigurableGenericableMethod method;
    private final ConfigurableRoute route;
    private final ConfigurableTags tags;
    private final Map<UniqueFitableId, Fitable> fitables = new ConcurrentHashMap<>();
    private final UniqueGenericableId uniqueId;

    DefaultGenericable(DynamicRouter dynamicRouter, String id, String version) {
        this.dynamicRouter = dynamicRouter;
        this.retryableExecutor = new RetryableGenericableExecutor(new UnicastGenericableExecutor());
        this.degradableExecutor = new DegradableGenericableExecutor(this.retryableExecutor);

        this.id = notBlank(id, "The genericable id cannot be blank.");
        this.version = notBlank(version, "The genericable version cannot be blank.");
        this.type = GenericableType.DEFAULT;
        this.method = new ConfigurableGenericableMethod();
        this.route = new ConfigurableRoute();
        this.tags = new ConfigurableTags();
        this.uniqueId = UniqueGenericableId.create(this.id, this.version);
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public GenericableType type() {
        return this.type;
    }

    @Override
    public GenericableMethod method() {
        return this.method;
    }

    @Override
    public Route route() {
        return this.route;
    }

    @Override
    public Tags tags() {
        return this.tags;
    }

    @Override
    public List<Fitable> fitables() {
        return Collections.unmodifiableList(new ArrayList<>(this.fitables.values()));
    }

    @Override
    public UniqueGenericableId toUniqueId() {
        return this.uniqueId;
    }

    @Override
    public Object execute(InvocationContext context, Object[] args) {
        List<Fitable> routedFitables = this.dynamicRouter.route(this, context, args);
        if (CollectionUtils.isEmpty(routedFitables)) {
            FitableNotFoundException exception = new FitableNotFoundException(StringUtils.format(
                    "No matched fitables left after routing filter. "
                            + "[genericableId={0}, genericableVersion={1}, toFilterFitables={2}, filter={3}]",
                    this.id,
                    this.version,
                    this.fitables(),
                    context.routingFilter()));
            exception.associateGenericable(this.id);
            throw exception;
        }
        if (!context.isMulticast() && routedFitables.size() > 1) {
            List<String> fitableIds = routedFitables.stream().map(Fitable::id).collect(Collectors.toList());
            TooManyFitablesException exception = new TooManyFitablesException(StringUtils.format(
                    "More than 1 fitables left before unicast invoking. "
                            + "[genericableId={0}, genericableVersion={1}, fitableIds={2}]",
                    this.id,
                    this.version,
                    fitableIds));
            exception.associateGenericable(this.id);
            throw exception;
        }
        if (context.isMulticast()) {
            GenericableExecutor multicastExecutor = context.withDegradation()
                    ? new MulticastGenericableExecutor(this.degradableExecutor)
                    : new MulticastGenericableExecutor(this.retryableExecutor);
            return execute(multicastExecutor, this, routedFitables, context, args);
        }
        return context.withDegradation()
                ? execute(this.degradableExecutor, this, routedFitables, context, args)
                : execute(this.retryableExecutor, this, routedFitables, context, args);
    }

    private static Object execute(GenericableExecutor executor, Genericable genericable, List<Fitable> fitables,
            InvocationContext context, Object[] args) {
        try {
            return executor.execute(fitables, context, args);
        } catch (Throwable e) {
            throw FitException.wrap(e, genericable.id());
        }
    }

    @Override
    public ConfigurableGenericable name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public ConfigurableGenericable type(GenericableType type) {
        this.type = type;
        return this;
    }

    @Override
    public ConfigurableGenericable method(Method method) {
        if (method != null) {
            this.method.method(method);
        }
        return this;
    }

    @Override
    public ConfigurableGenericable route(String defaultFitableId) {
        this.route.defaultFitable(defaultFitableId);
        return this;
    }

    @Override
    public ConfigurableGenericable tags(Set<String> tags) {
        this.tags.clear();
        if (tags != null) {
            this.tags.set(tags);
        }
        return this;
    }

    @Override
    public ConfigurableGenericable appendTag(String tag) {
        this.tags.append(tag);
        return this;
    }

    @Override
    public ConfigurableGenericable removeTag(String tag) {
        this.tags.remove(tag);
        return this;
    }

    @Override
    public ConfigurableGenericable clearTags() {
        this.tags.clear();
        return this;
    }

    @Override
    public ConfigurableGenericable fitables(List<Fitable> fitables) {
        this.fitables.clear();
        if (fitables != null) {
            fitables.stream()
                    .filter(Objects::nonNull)
                    .forEach(fitable -> this.fitables.put(fitable.toUniqueId(), fitable));
        }
        return this;
    }

    @Override
    public ConfigurableGenericable appendFitable(Fitable fitable) {
        this.fitables.put(fitable.toUniqueId(), fitable);
        return this;
    }

    @Override
    public ConfigurableGenericable clearFitables() {
        this.fitables.clear();
        return this;
    }

    @Override
    public String toString() {
        return "{\"id\": \"" + this.id + '\"' + ", \"version\": \"" + this.version + '\"' + ", \"name\": \"" + this.name
                + '\"' + ", \"fitables\": " + this.fitables.values() + '}';
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || this.getClass() != another.getClass()) {
            return false;
        }
        DefaultGenericable that = (DefaultGenericable) another;
        return Objects.equals(this.id, that.id) && Objects.equals(this.version, that.version)
                && Objects.equals(this.name, that.name) && Objects.equals(this.fitables, that.fitables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.version, this.name, this.fitables);
    }
}
