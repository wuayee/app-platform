/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.server.support;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.serialization.TagLengthValues.TLV_WORKER_ID;
import static com.huawei.fitframework.serialization.TagLengthValues.TLV_WORKER_INSTANCE_ID;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.huawei.fitframework.broker.LocalExecutor;
import com.huawei.fitframework.broker.LocalExecutorFactory;
import com.huawei.fitframework.broker.UniqueFitableId;
import com.huawei.fitframework.broker.server.Dispatcher;
import com.huawei.fitframework.broker.server.Response;
import com.huawei.fitframework.conf.runtime.WorkerConfig;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.exception.MethodInvocationException;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.RequestMetadata;
import com.huawei.fitframework.serialization.ResponseMetadata;
import com.huawei.fitframework.serialization.TagLengthValues;
import com.huawei.fitframework.serialization.tlv.ValueSerializer;
import com.huawei.fitframework.serialization.tlv.support.ExceptionPropertiesValueSerializer;
import com.huawei.fitframework.util.ExceptionUtils;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link Dispatcher} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-10-17
 */
public class DefaultDispatcher implements Dispatcher {
    private static final Logger log = Logger.get(DefaultDispatcher.class);

    private final WorkerConfig workerConfig;
    private final LazyLoader<LocalExecutorFactory> localExecutorFactoryLoader;

    /**
     * 通过 Bean 容器和当前进程配置来初始化 {@link DefaultDispatcher} 的新实例。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param workerConfig 表示当前进程配置的 {@link WorkerConfig}。
     */
    public DefaultDispatcher(BeanContainer container, WorkerConfig workerConfig) {
        notNull(container, "The bean container cannot be null.");
        this.workerConfig = notNull(workerConfig, "The worker config cannot be null.");
        this.localExecutorFactoryLoader = new LazyLoader<>(() -> container.factory(LocalExecutorFactory.class)
                .map(BeanFactory::<LocalExecutorFactory>get)
                .orElseThrow(() -> new IllegalStateException("No local executor factory.")));
    }

    @Override
    public Response dispatch(RequestMetadata metadata, Object[] data) {
        try {
            LocalExecutor executor = this.getLocalExecutor(metadata);
            Object response = executor.execute(data);
            log.debug("Execute fitable successfully. [genericableId={}, fitableId={}]",
                    metadata.genericableId(),
                    metadata.fitableId());
            return Response.create(this.successfulResponseMetadataBytes(metadata),
                    executor.method().getGenericReturnType(),
                    response);
        } catch (Throwable cause) {
            log.error("Failed to execute fitable. [genericableId={}, fitableId={}, cause={}]",
                    metadata.genericableId(),
                    metadata.fitableId(),
                    cause.getMessage(),
                    cause);
            return Response.create(this.getResponseMetadata(cause, metadata));
        } finally {
            log.debug("Prepare to clear global context.");
        }
    }

    private LocalExecutor getLocalExecutor(RequestMetadata reqMetadata) {
        return this.localExecutorFactoryLoader.get()
                .get(UniqueFitableId.create(reqMetadata.genericableId(), reqMetadata.fitableId()))
                .orElseThrow(() -> new IllegalStateException("No local executor."));
    }

    private ResponseMetadata getResponseMetadata(Throwable cause, RequestMetadata metadata) {
        if (cause instanceof MethodInvocationException) {
            Throwable actualCause = ExceptionUtils.getActualCause((MethodInvocationException) cause);
            return this.handleException(actualCause, metadata);
        } else {
            return this.handleException(cause, metadata);
        }
    }

    private ResponseMetadata handleException(Throwable cause, RequestMetadata metadata) {
        if (cause instanceof FitException) {
            FitException actualCause = ObjectUtils.cast(cause);
            TagLengthValues tagLengthValues = this.buildTagLengthValues(actualCause.getProperties());
            return this.responseMetadataBytes(metadata,
                    actualCause.getCode(),
                    actualCause.getMessage(),
                    tagLengthValues);
        }
        return this.responseMetadataBytes(metadata, -1, cause.getMessage(), null);
    }

    private TagLengthValues buildTagLengthValues(Map<String, String> properties) {
        TagLengthValues tagLengthValues = TagLengthValues.create();
        tagLengthValues.putTag(ValueSerializer.TAG_EXCEPTION_PROPERTIES,
                ExceptionPropertiesValueSerializer.INSTANCE.serialize(properties));
        return tagLengthValues;
    }

    private ResponseMetadata successfulResponseMetadataBytes(RequestMetadata metadata) {
        return this.responseMetadataBytes(metadata, 0, StringUtils.EMPTY, null);
    }

    private ResponseMetadata responseMetadataBytes(RequestMetadata metadata, int code, String message,
            TagLengthValues tagLengthValues) {
        TagLengthValues actual = ObjectUtils.getIfNull(tagLengthValues, TagLengthValues::create);
        actual.putTag(TLV_WORKER_ID, this.workerConfig.id().getBytes(UTF_8));
        actual.putTag(TLV_WORKER_INSTANCE_ID, this.workerConfig.instanceId().getBytes(UTF_8));
        return ResponseMetadata.custom()
                .dataFormat(valueFormat(metadata.dataFormat()))
                .code(code)
                .message(message)
                .tagValues(actual)
                .build();
    }

    private static byte valueFormat(int format) {
        return (byte) (format & 0xFF);
    }
}
