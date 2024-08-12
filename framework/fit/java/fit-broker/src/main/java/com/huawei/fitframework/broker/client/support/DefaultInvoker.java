/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.client.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.broker.CommunicationType;
import com.huawei.fitframework.broker.ConfigurableGenericable;
import com.huawei.fitframework.broker.Genericable;
import com.huawei.fitframework.broker.GenericableMetadata;
import com.huawei.fitframework.broker.GenericableRepository;
import com.huawei.fitframework.broker.InvocationContext;
import com.huawei.fitframework.broker.UniqueFitableId;
import com.huawei.fitframework.broker.client.FitableNotFoundException;
import com.huawei.fitframework.broker.client.GenericableNotFoundException;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.conf.runtime.CommunicationProtocol;
import com.huawei.fitframework.conf.runtime.SerializationFormat;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;

/**
 * {@link Invoker} 的默认实现。
 *
 * @author 季聿阶
 * @since 2021-06-17
 */
public class DefaultInvoker implements Invoker {
    /** 以下属性为通过构造函数传入的属性。 */

    private final GenericableRepository microGenericableRepository;
    private final GenericableRepository macroGenericableRepository;
    private final InvocationContext.Builder contextBuilder;
    private InvocationContext context;

    /** 以下属性为在当前调用器中设置的属性。 */

    private Filter filter;
    private final List<UniqueFitableId> filterWith = new ArrayList<>();
    private BinaryOperator<Object> accumulator = (first, second) -> first;

    DefaultInvoker(GenericableRepository microGenericableRepository, GenericableRepository macroGenericableRepository,
            String genericableId, InvocationContext.Builder contextBuilder) {
        this.microGenericableRepository =
                notNull(microGenericableRepository, "The micro genericable repository cannot be null.");
        this.macroGenericableRepository =
                notNull(macroGenericableRepository, "The macro genericable repository cannot be null.");
        this.contextBuilder = notNull(contextBuilder,
                "The invocation context builder cannot be null. [genericableId={0}]",
                genericableId).isMulticast(false).accumulator(this.accumulator).withDegradation(true);
    }

    @Override
    public Invoker filter(Filter filter) {
        this.filter = Filter.combine(this.filter, filter);
        this.contextBuilder.loadBalanceFilter(this.filter);
        return this;
    }

    @Override
    public Invoker filterWith(List<UniqueFitableId> ids) {
        this.filterWith.addAll(ids);
        this.contextBuilder.loadBalanceWith(this.filterWith);
        return this;
    }

    @Override
    public Invoker filterWithSpecifiedEnvironment(String environment) {
        this.contextBuilder.specifiedEnvironment(environment);
        return this;
    }

    @Override
    public Invoker unicast() {
        this.contextBuilder.isMulticast(false);
        return this;
    }

    @Override
    public Invoker communicationType(CommunicationType communicationType) {
        this.contextBuilder.communicationType(communicationType);
        return this;
    }

    @Override
    public Invoker multicast(BinaryOperator<Object> accumulator) {
        this.accumulator = accumulator;
        this.contextBuilder.isMulticast(true).accumulator(accumulator);
        return this;
    }

    @Override
    public Invoker retry(int maxCount) {
        if (maxCount >= 0) {
            this.contextBuilder.retry(maxCount);
        }
        return this;
    }

    @Override
    public Invoker timeout(long timeout, TimeUnit timeoutUnit) {
        if (timeout > 0) {
            this.contextBuilder.timeout(timeout).timeoutUnit(timeoutUnit);
        }
        return this;
    }

    @Override
    public Invoker protocol(CommunicationProtocol protocol) {
        this.contextBuilder.protocol(protocol);
        return this;
    }

    @Override
    public Invoker format(SerializationFormat format) {
        this.contextBuilder.format(format);
        return this;
    }

    @Override
    public Invoker ignoreDegradation() {
        this.contextBuilder.withDegradation(false);
        return this;
    }

    @Override
    public Invoker filterExtensions(Map<String, Object> filterExtensions) {
        this.contextBuilder.filterExtensions(filterExtensions);
        return this;
    }

    @Override
    public <R> R invoke(Object... args) {
        try {
            Genericable genericable = this.getGenericable();
            return cast(genericable.execute(this.context, args));
        } catch (Throwable e) {
            throw FitException.wrap(e, this.context.genericableId());
        }
    }

    @Override
    public Genericable getGenericable() {
        this.context = this.contextBuilder.build();
        String genericableId = notBlank(this.context.genericableId(),
                () -> new GenericableNotFoundException("The genericable id cannot be blank."));
        Genericable genericable = this.getGenericable(genericableId);
        if (genericable instanceof ConfigurableGenericable) {
            ((ConfigurableGenericable) genericable).method(this.context.genericableMethod());
        }
        return genericable;
    }

    private Genericable getGenericable(String genericableId) {
        GenericableRepository repository =
                this.context.isMicro() ? this.microGenericableRepository : this.macroGenericableRepository;
        return repository.get(genericableId, GenericableMetadata.DEFAULT_VERSION)
                .orElseThrow(() -> FitException.wrap(new FitableNotFoundException(StringUtils.format(
                        "No fitables. [genericableId={0}, isMicro={1}]",
                        genericableId,
                        this.context.isMicro())), genericableId));
    }
}
