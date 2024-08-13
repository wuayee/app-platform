/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.annotation.Alias;
import com.huawei.fitframework.broker.Fitable;
import com.huawei.fitframework.broker.InvocationContext;
import com.huawei.fitframework.broker.LocalExecutor;
import com.huawei.fitframework.broker.Target;
import com.huawei.fitframework.broker.UniqueFitableId;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.exception.MethodInvocationException;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.BeanMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.support.BeanFactoryResolver;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示本地调用的执行器。
 *
 * @author 季聿阶
 * @since 2023-03-24
 */
public class LocalFitableExecutor extends AbstractUnicastFitableExecutor implements LocalExecutor {
    private final UniqueFitableId id;
    private final boolean isMicro;
    private final BeanMetadata metadata;
    private final LazyLoader<Object> targetLoader;
    private final Method method;

    public LocalFitableExecutor(UniqueFitableId id, boolean isMicro, BeanMetadata metadata,
            Supplier<Object> targetSupplier, Method method) {
        this.id = id;
        this.isMicro = isMicro;
        this.metadata = notNull(metadata, "The bean metadata cannot be null.");
        this.targetLoader = new LazyLoader<>(notNull(targetSupplier, "The target supplier cannot be null."));
        this.method = notNull(method, "The method cannot be null.");
    }

    @Override
    public UniqueFitableId id() {
        return this.id;
    }

    @Override
    public Set<String> aliases() {
        Set<String> originAliases = new HashSet<>(this.metadata.aliases());
        if (!StringUtils.startsWithIgnoreCase(this.metadata.name(), BeanFactoryResolver.DEFAULT_BEAN_NAME_PREFIX)) {
            originAliases.add(this.metadata.name());
        }
        AnnotationMetadata annotations =
                this.metadata.container().runtime().resolverOfAnnotations().resolve(this.method);
        if (annotations.isAnnotationPresent(Alias.class)) {
            Alias[] aliases = annotations.getAnnotationsByType(Alias.class);
            Stream.of(aliases).filter(Objects::nonNull).map(Alias::value).forEach(originAliases::add);
        }
        return originAliases;
    }

    @Override
    public BeanMetadata metadata() {
        return this.metadata;
    }

    @Override
    public boolean isMicro() {
        return this.isMicro;
    }

    @Override
    public Method method() {
        return this.method;
    }

    @Override
    public Object execute(Object[] args) {
        this.validateParams(args);
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        Object result;
        try {
            Thread.currentThread().setContextClassLoader(this.metadata.container().plugin().pluginClassLoader());
            result = ReflectionUtils.invoke(this.targetLoader.get(), this.method, args);
        } catch (MethodInvocationException e) {
            Throwable cause = e.getCause();
            throw FitException.wrap(cause, this.id.genericableId(), this.id.fitableId());
        } catch (Throwable e) {
            throw FitException.wrap(e, this.id.genericableId(), this.id.fitableId());
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
        return cast(result);
    }

    @Override
    protected Object execute(Fitable fitable, Target target, InvocationContext context, Object[] args) {
        try {
            return this.execute(args);
        } catch (Throwable e) {
            throw FitException.wrap(e, fitable.genericable().id(), fitable.id());
        }
    }

    private void validateParams(Object[] args) {
        Parameter[] parameters = this.method.getParameters();
        int actualArgLen = args == null ? 0 : args.length;
        int expectedArgLen = parameters.length;
        Validation.equals(actualArgLen,
                expectedArgLen,
                "Argument number mismatch. [argumentTypes={0}]",
                Stream.of(parameters).map(Parameter::getType).map(Class::getName).collect(Collectors.toList()));
        for (int i = 0; i < actualArgLen; i++) {
            Object actualArg = args[i];
            if (actualArg == null) {
                continue;
            }
            Class<?> expected = ReflectionUtils.ignorePrimitiveClass(parameters[i].getType());
            Class<?> actual = actualArg.getClass();
            if (expected.isAssignableFrom(actual)) {
                continue;
            }
            throw new IllegalArgumentException(StringUtils.format(
                    "Argument[{0}] mismatch. [expectedArgumentType={1}, actualArgumentType={2}]",
                    i,
                    parameters[i].getType().getName(),
                    actualArg.getClass().getName()));
        }
    }
}
